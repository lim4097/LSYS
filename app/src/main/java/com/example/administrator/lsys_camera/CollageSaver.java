package com.example.administrator.lsys_camera;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.TextureView;

import java.util.ArrayList;

public class CollageSaver extends Saver{

    // The JPEG image
    private  Bitmap imageBitmap;
    TextureView textureView;

    public CollageSaver(Context context , TextureView textureView, Bitmap bitmap , Object saveObject) {
        super(context,saveObject);

        this.textureView = textureView;
        this.imageBitmap = bitmap;
    }

    public void run() {
        synchronized (saveObject) {

        }
    }

}
