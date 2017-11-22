package com.example.administrator.lsys_camera;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by shin on 2017-04-21.
 */


public class photoGallery extends AppCompatActivity{

    public static Activity galleryActivity;

    public String basePath = null;
    public GridView mGridView;
    public GalleryAdapter mGalleryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photogallery);

        galleryActivity=photoGallery.this;
        Intent receiveIntent=getIntent();
        boolean isDelete=receiveIntent.getBooleanExtra("isDeleteBool",false);
        if(isDelete) {
            String fullPath = receiveIntent.getStringExtra("ImageFullPath");
            File file= new File(fullPath);
            file.delete();
        }

        MainActivity.mainActivity.finish();

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"LSYS");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
            }
        }

        basePath = mediaStorageDir.getPath();

        mGridView = (GridView)findViewById(R.id.gridview); // .xml의 GridView와 연결
        mGalleryAdapter = new GalleryAdapter(this, basePath); // 앞에서 정의한 Custom Image Adapter와 연결
        mGridView.setAdapter(mGalleryAdapter); // GridView가 Custom Image Adapter에서 받은 값을 뿌릴 수 있도록 연결
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), mGalleryAdapter.getItemPath(position), Toast.LENGTH_LONG).show();
                Intent imageIntent = new Intent(getApplicationContext(),showSelected.class);
                imageIntent.putExtra("ImagePositon",position);
                startActivity(imageIntent);

            }
        });
    }
    @Override
    public void onBackPressed()
    {
        Intent mainOpenIntent=new Intent(getApplicationContext(),MainActivity.class);//카메라를 오픈하기 위한 인텐트
        startActivity(mainOpenIntent);//
    }

}


