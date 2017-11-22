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
import java.util.ArrayList;
import java.util.Date;

public class GIFSaver extends Saver{

    ArrayList<Bitmap> bitmapList;
    private AnimatedGifEncoder encoder;
    private int gifGoalCount;

    public GIFSaver(Context context, ArrayList<Bitmap> bitmapList, int gifGoalCount, Object saveObject , int gifTextId) {
        super(context,saveObject);

        this.bitmapList = bitmapList;
        encoder = new AnimatedGifEncoder();
        this.gifGoalCount = gifGoalCount;
    }

    @Override
    public void run()
    {
        synchronized (saveObject) {

            File mFile;

            String mPath = MakePath("gif"); // 경로(저장될파일명)생성
            OutputStream fileOutputStream = null;

            try {
                mFile = new File(mPath);
                fileOutputStream = new FileOutputStream(mFile);

                // 파일스트림을이용하여 GIF 형태를 만듬
                    encoder.start(fileOutputStream);
                    encoder.setDelay(100); //변경가능, 나중에 설정만 생성자 인자로 받아서 바꾸면 될듯?
                    encoder.setRepeat(0);

                    //encoder.setSize(60,60); //이러면 원래 사진에서 잘림. 미리 사이즈를 줄여야할듯
                    //encoder.setFrameRate(20);
                    AppCompatActivity activity = (AppCompatActivity) context;
                    ProgressBar bar = (ProgressBar) activity.findViewById(R.id.progressBar);
                    int i;
                    for (i = 0; i < gifGoalCount && !Thread.currentThread().isInterrupted(); i++) {
                        // mBitmap = bitmapList[i];
                        Log.e("for","for");
                        float per =((float)i / (float)gifGoalCount) * 100;
                        bar.setProgress((int)per);
                        encoder.addFrame(bitmapList.get(i));

                    }
                    Log.e("for","for2");
                    encoder.finish();

                    for (int j = 0; j < gifGoalCount; j++) {
                        bitmapList.get(j).recycle();
                    }
                    bitmapList.clear();
                    Log.e("gifgif", "yes");
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    if(i != gifGoalCount) // 제대로 끝마쳐지지 않았을때
                    {
                        File file = new File(mPath);
                        file.delete();
                    }
                    else
                        addImageToGallery(mFile.toString(), context,"gif");



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

                        ProgressBar bar = (ProgressBar) activity.findViewById(R.id.progressBar);
                        bar.setVisibility(View.GONE);


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
                        galleyButton = (ImageView) activity.findViewById(R.id.id_icon_gallery);
                        galleyButton.setEnabled(true);
                        MainActivity.collageAdapterLock = true;
                        switchOfGif = (Switch)activity.findViewById(R.id.id_switch_gif);
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
    }


}
