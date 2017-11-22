package com.example.administrator.lsys_camera;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


public class MultiPhotoSelectActivity extends AppCompatActivity {

    public static Activity multiphotoselectActivity;
    private ImageAdapter imageAdapter;
    private String basePath;
    public static ArrayList<String> imageUrls;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        multiphotoselectActivity=MultiPhotoSelectActivity.this;
        setContentView(R.layout.layout_multi_photo_select);

        Intent receiveIntent=getIntent();
        boolean isDelete=receiveIntent.getBooleanExtra("isDeleteBool",false);
        if(isDelete) {
            String urlPath = receiveIntent.getStringExtra("ImageFullPath");
            File file= new File(urlPath);
            Log.e("ddd"," "+file.getName());

            File delFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"LSYS/"+file.getName());

            delFile.delete();
        }

        if(MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
        }

        /*
        if(showSelected.showSelectedActivity != null) {
            showSelected.showSelectedActivity.finish();
            showSelected.showSelectedActivity = null;
        }
        */


        imageUrls = loadPhotosFromNativeGallery();
        initializeRecyclerView(imageUrls);

    }


    protected void onDestroy()
    {
        multiphotoselectActivity = null;

        super.onDestroy();
        findViewById(R.id.recycler_view).setBackground(null);
        System.gc();
    }

    /*
    public void btnChoosePhotosClick(View v){

        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();

        if (selectedItems!= null && selectedItems.size() > 0) {
            Toast.makeText(MultiPhotoSelectActivity.this, "Total photos selected: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
            Log.d(MultiPhotoSelectActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
        }
    }
    */


    private ArrayList<String> loadPhotosFromNativeGallery() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"LSYS");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
            }
        }
        basePath = mediaStorageDir.getPath();
    /*
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");
        Uri path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "LSYS");


    */
        // ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls = new ArrayList<String>();

        //  File file = new File(path);
        File[] files = mediaStorageDir.listFiles();

        for(int i = files.length-1; i >= 0;i--) {
            if(files[i].isDirectory()) {
                Log.v("Directory : ", files[i].getName());
            } else {
                imageUrls.add(Uri.fromFile(files[i]).toString());
                Log.v("File :", files[i].getName());
            }
        }


        //   fileList= Arrays.asList( file.listFiles());
        /*
        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));

            System.out.println("=====> Array path => "+imageUrls.get(i));
        }
        */

        return imageUrls;
    }

    private void initializeRecyclerView(ArrayList<String> imageUrls) {
        imageAdapter = new ImageAdapter(this, imageUrls);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
        recyclerView.setAdapter(imageAdapter);

    }

    @Override
    public void onBackPressed()
    {
        Intent mainOpenIntent=new Intent(getApplicationContext(),MainActivity.class);//카메라를 오픈하기 위한 인텐트
        startActivity(mainOpenIntent);//
        this.finish();
    }

}