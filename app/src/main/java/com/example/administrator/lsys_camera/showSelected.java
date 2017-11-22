package com.example.administrator.lsys_camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;


public class showSelected extends AppCompatActivity {
    String urlPath;
    ImageView imageView;
    int position;
    File[] files;
    //File mediaStorageDir;
    //public static Activity showSelectedActivity;
    private boolean viewTouchSync;
    float viewPosX,viewPosY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ImageView showView = ;
        super.onCreate(savedInstanceState);
        // showSelectedActivity=showSelected.this;
        setContentView(R.layout.selceted_image);
        imageView=(ImageView)findViewById(R.id.showImage);

        Intent receiveIntent=getIntent();
        position=receiveIntent.getExtras().getInt("ImagePositon");
        Log.e(""+position,"");

        String imageUrl = MultiPhotoSelectActivity.imageUrls.get(position);
        // urlPath = "file://"+imageUrl;
        urlPath = imageUrl;
        // Glide.with(this) "file://"+
        //      .load("file://"+imageUrl)
        //        .into(imageView);
        // fullPath=GalleryAdapter.fileList.get(positon).getPath();
        Glide.with(this).load(imageUrl).thumbnail(0.1f).into(imageView);


        //  imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if(MultiPhotoSelectActivity.multiphotoselectActivity != null) {
            MultiPhotoSelectActivity.multiphotoselectActivity.finish();
            MultiPhotoSelectActivity.multiphotoselectActivity = null;
        }

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"LSYS");
        files = mediaStorageDir.listFiles();

        imageView.setOnTouchListener((new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        viewPosX = event.getX();
                        viewPosY = event.getY();
                        viewTouchSync = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("123123","77777");
                        float delPosX = event.getX() - viewPosX;
                        float delPosY = event.getY() - viewPosY;
                        CheckMove(delPosX,delPosY);
                        break;


                }
                return true;
            }


        }));

    }


    public String getPathFromUri(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }

    protected void onDestroy()
    {
        super.onDestroy();
        imageView.setImageBitmap(null);
        imageView.setBackground(null);
        imageView = null;
        findViewById(R.id.showImage).setBackground(null);

        System.gc();
    }


    void CheckMove(float delX, float delY)
    {
        if(viewTouchSync) {
            Log.e("test1","test1");
            float norm = delX * delX + delY * delY; // norm^2
            float widthLength = imageView.getWidth() * imageView.getWidth() / 16;// widthLength^2 / 16
            if (norm > widthLength) // 휴대폰 가로길이의 1/4이 넘으면 필터 변경
            {
                float gradient = delY / delX;
                if (gradient >= -1.0 && gradient <= 1.0) // fbs(gradient) <= 1 //기울기는 1, -1 기준
                {
                    if(delX >0) // 슬라이드 왼쪽에서 오른쪽으로
                    {
                        if (position != 0)
                            position -= 1;
                        else
                            position = files.length-1;
                    }
                    else
                    {
                        if (position != files.length-1)
                            position += 1;
                        else
                            position = 0;
                    }
                    String nextImageUrl = MultiPhotoSelectActivity.imageUrls.get(position);
                    Log.e("ttposition"," "+position);
                    Log.e("tturl"," "+nextImageUrl);
                    imageView.setImageBitmap(null);
                    imageView.setBackground(null);
                    Glide.with(this).load(nextImageUrl).thumbnail(0.1f).into(imageView);

                    viewTouchSync = false;
                }
            }
        }
    }


    public void onClick(View v)
    {
        String fullPath;
        switch (v.getId())
        {
            case R.id.del:
                this.finish();
                Intent galleryOpenIntent=new Intent(getApplicationContext(),MultiPhotoSelectActivity.class);//갤러리를 오픈하기 위한 인텐트
                galleryOpenIntent.putExtra("isDeleteBool",true);
                //  = getPathFromUri(urlPath)
                galleryOpenIntent.putExtra("ImageFullPath",urlPath);
                startActivity(galleryOpenIntent);//갤러리 열어줌.

                break;


            case R.id.share:
                Intent msg = new Intent(Intent.ACTION_SEND);

                msg.setType("image/*");
                msg.putExtra(Intent.EXTRA_STREAM, Uri.parse(urlPath));
                startActivity(Intent.createChooser(msg, "이미지 공유하기"));
                break;
            case R.id.prevPhoto:
                if(position!=0)
                    position=position-1;
                else
                    position=files.length-1;

                String prevImageUrl = MultiPhotoSelectActivity.imageUrls.get(position);
                Log.e("ttposition"," "+position);
                Log.e("tturl"," "+prevImageUrl);
                imageView.setImageBitmap(null);
                imageView.setBackground(null);
                Glide.with(this).load(prevImageUrl).thumbnail(0.1f).into(imageView);

                break;
            case R.id.nextPhoto:
                if(position!=files.length-1)
                    position=position+1;
                else
                    position = 0;

                String nextImageUrl = MultiPhotoSelectActivity.imageUrls.get(position);
                Log.e("ttposition"," "+position);
                Log.e("tturl"," "+nextImageUrl);
                imageView.setImageBitmap(null);
                imageView.setBackground(null);
                Glide.with(this).load(nextImageUrl).thumbnail(0.1f).into(imageView);
                break;
        }

    }

    @Override
    public void onBackPressed()
    {

        Intent galleryOpenIntent=new Intent(getApplicationContext(),MultiPhotoSelectActivity.class);//갤러리를 오픈하기 위한 인텐트
        galleryOpenIntent.putExtra("isDeleteBool",false);
        startActivity(galleryOpenIntent);//갤러리 열어줌.
        this.finish();
    }

}
