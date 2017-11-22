package com.example.administrator.lsys_camera;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Saver implements Runnable {

    static final String FileName = "LSYS";
    Context context;
    Object saveObject;
    Handler mHandler = new Handler(Looper.getMainLooper());
    ImageView btChange, btFlash, btTimer, btCapture, collageButton, galleyButton, galleryButton;
    Switch switchOfGif;

    public Saver(Context context, Object saveObject) {
        this.context =context;
        this.saveObject =saveObject;
    }

    //abstract public void run();
    // 저장하여 갤러리에 반영시키기
    public void addImageToGallery(final String filePath, final Context context, String type) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/"+type);
        values.put(MediaStore.MediaColumns.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public String MakePath(String type)
    {
        String mPath;
        Date date = new Date();
        // miliSecond까지 해야 사진이 겹치지않는다
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_hh_mm_ss_SSS");
        String timeStr = dateFormat.format(date);
        String dirStr = Environment.getExternalStorageDirectory().toString() + "/" + FileName;
        mPath = dirStr + "/" + timeStr + "." + type;
        MakeDir(dirStr); // 디렉토리 생성
        return mPath;
    }

    public void MakeDir(String dirStr)
    {
        File dir = new File(dirStr);
        //해당 디렉토리의 존재여부를 확인
        if (!dir.exists()) {
            //없다면 생성
            dir.mkdirs();
        }
    }
}
