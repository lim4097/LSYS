package com.example.administrator.lsys_camera.collage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapCombine {
    public static Bitmap combineBitmapForCollage01(Bitmap first, Bitmap second)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;
        Bitmap bitmap = null;

        // 판 만들기
        bitmap = Bitmap.createScaledBitmap(
                first,
                first.getWidth()+second.getWidth(),
                first.getHeight(),
                true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);

        c.drawBitmap(
                first,
                0, 0,
                p);

        c.drawBitmap(
                second,
                first.getWidth(), 0,
                p);

        first.recycle();
        second.recycle();

        return bitmap;
    }

    public static Bitmap combineBitmapForCollage02(Bitmap first, Bitmap second)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;
        Bitmap bitmap = null;

        // 판 만들기
        bitmap = Bitmap.createScaledBitmap(
                first,
                first.getWidth(),
                first.getHeight()+second.getHeight(),
                true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);
        c.drawBitmap(
                first,
                0, 0, p);

        // merge 2
        c.drawBitmap(second,
                0, first.getHeight(),
                p);

        first.recycle();
        second.recycle();

        return bitmap;
    }

    public static Bitmap combineBitmapForCollage03(Bitmap first, Bitmap second, Bitmap third, Bitmap fourth)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;
        Bitmap bitmap = null;

        // 판만들기
        bitmap = Bitmap.createScaledBitmap(
                first,
                first.getWidth()+second.getWidth(),
                first.getHeight()+third.getHeight(),
                true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);

        c.drawBitmap(
                first,
                0, 0,
                p);

        c.drawBitmap(second,
                first.getWidth(), 0,
                p);

        c.drawBitmap(
                third,
                0,first.getHeight(),
                p);

        c.drawBitmap(
                fourth,
                first.getWidth(),first.getHeight(),
                p);

        first.recycle();
        second.recycle();
        third.recycle();
        fourth.recycle();

        return bitmap;
    }

    public static Bitmap combineBitmapForCollage04(Bitmap first, Bitmap second, Bitmap third)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;
        Bitmap bitmap = null;

        // 판 만들기
        bitmap = Bitmap.createScaledBitmap(
                first,
                first.getWidth()+second.getWidth()+third.getWidth(),
                first.getHeight(),
                true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);

        c.drawBitmap(
                first,
                0, 0,
                p);

        c.drawBitmap(
                second,
                first.getWidth(), 0,
                p);

        c.drawBitmap(
                third,
                first.getWidth()+second.getWidth(), 0,
                p);

        first.recycle();
        second.recycle();
        third.recycle();

        return bitmap;
    }

    public static Bitmap combineBitmapForCollage05(Bitmap first, Bitmap second, Bitmap third)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;
        Bitmap bitmap = null;

        // 판 만들기
        bitmap = Bitmap.createScaledBitmap(
                first,
                first.getWidth(),
                first.getHeight()+second.getHeight()+third.getHeight(),
                true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);

        c.drawBitmap(
                first,
                0, 0, p);

        c.drawBitmap(
                second,
                0, first.getHeight(),
                p);

        c.drawBitmap(
                third,
                0, first.getHeight()+second.getHeight(),
                p);

        first.recycle();
        second.recycle();
        third.recycle();

        return bitmap;
    }

    public static Bitmap combineBitmapForCollage06(Bitmap first, Bitmap second, Bitmap third, Bitmap fourth)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;
        Bitmap bitmap = null;

        // 판 만들기
        bitmap = Bitmap.createScaledBitmap(
                first,
                first.getWidth()+second.getWidth()+third.getWidth()+fourth.getWidth(),
                first.getHeight(),
                true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);

        c.drawBitmap(
                first,
                0, 0,
                p);

        c.drawBitmap(
                second,
                first.getWidth(), 0,
                p);

        c.drawBitmap(
                third,
                first.getWidth()+second.getWidth(), 0,
                p);

        c.drawBitmap(
                fourth,
                first.getWidth()+second.getWidth()+third.getWidth(), 0,
                p);

        first.recycle();
        second.recycle();
        third.recycle();
        fourth.recycle();

        return bitmap;
    }

    public static Bitmap combineBitmapForCollage07(Bitmap first, Bitmap second, Bitmap third, Bitmap fourth)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;
        Bitmap bitmap = null;

        // 판 만들기
        bitmap = Bitmap.createScaledBitmap(
                first,
                first.getWidth(),
                first.getHeight()+second.getHeight()+third.getHeight()+fourth.getHeight(),
                true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bitmap);

        c.drawBitmap(
                first,
                0, 0, p);

        c.drawBitmap(
                second,
                0, first.getHeight(),
                p);

        c.drawBitmap(
                third,
                0, first.getHeight()+second.getHeight(),
                p);

        c.drawBitmap(
                fourth,
                0, first.getHeight()+second.getHeight()+third.getHeight(),
                p);

        first.recycle();
        second.recycle();
        third.recycle();
        fourth.recycle();

        return bitmap;
    }
}
