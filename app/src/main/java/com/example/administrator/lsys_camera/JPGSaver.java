package com.example.administrator.lsys_camera;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JPGSaver extends Saver {

    // The JPEG image
    private Bitmap imageBitmap;
    private  TextureView textureView;


    public JPGSaver(Context context, TextureView textureView, Object saveObject) {
        super(context,saveObject);
        this.textureView = textureView;

        this.imageBitmap = textureView.getBitmap();
    }

    // Collage Saver
    public JPGSaver(Context context , TextureView textureView, Bitmap bitmap ,Object saveObject) {
        super(context,saveObject);
        this.textureView = textureView;

        this.imageBitmap = bitmap;
    }

    @Override
    public void run()
    {
        synchronized (saveObject) {

            MainActivity.canEdit=false;

            /*
            while (!MainActivity.canCapt)
            {

            }

            this.imageBitmap =textureView.getBitmap();
            *///-카메라 윤곽선 지우기


            File mFile;

            String mPath = MakePath("jpg"); // 경로(저장될파일명)생성
            OutputStream fileOutputStream = null;

            try {
                mFile = new File(mPath);
                fileOutputStream = new FileOutputStream(mFile);

                // 비트맵을 파일스트림을이용하여 JPEG 형태로 보냄
                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 99, fileOutputStream);
                    Log.e("jpgjpg", "yes");
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    addImageToGallery(mFile.toString(), context,"jpg");
                    //  }
                }

                // 저장이 되어야 다음촬영실행
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mHandler.post(new Runnable() {
                    // 찍힌게 완료 됐을 때
                    @Override
                    public void run() {
                        AppCompatActivity activity = (AppCompatActivity) context;
                        //Toast.makeText(context.getApplicationContext(), "complete", Toast.LENGTH_SHORT).show();

                        AlphaAnimation anim = new AlphaAnimation(1, 0.5f);
                        anim.setDuration(100);        // 에니메이션 동작 주기
                        anim.setRepeatCount(0);    // 에니메이션 반복 회수
                        //anim.setRepeatMode(Animation.REVERSE);// 반복하는 방법
                        textureView.startAnimation(anim);

                        btCapture = (ImageView) activity.findViewById(R.id.id_icon_circle);
                        btCapture.setEnabled(true);
                        btChange = (ImageView) activity.findViewById(R.id.id_icon_change);
                        btChange.setEnabled(true);
                        btFlash = (ImageView) activity.findViewById(R.id.id_icon_flash);
                        btFlash.setEnabled(true);
                        btTimer = (ImageView) activity.findViewById(R.id.id_icon_timer);
                        btTimer.setEnabled(true);
                        collageButton = (ImageView) activity.findViewById(R.id.id_icon_collage);
                        collageButton.setEnabled(true);
                        galleryButton = (ImageView) activity.findViewById(R.id.id_icon_gallery);
                        galleryButton.setEnabled(true);
                        MainActivity.collageAdapterLock =true;
                        switchOfGif = (Switch) activity.findViewById(R.id.id_switch_gif);
                        switchOfGif.setEnabled(true);
                    }
                });

                if (null != fileOutputStream) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        MainActivity.canCapt=false;
    }


}
