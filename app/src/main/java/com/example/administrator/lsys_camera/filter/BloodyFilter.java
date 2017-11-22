package com.example.administrator.lsys_camera.filter;


import android.content.Context;

import com.example.administrator.lsys_camera.LSYSUtility;
import com.example.administrator.lsys_camera.R;

public class BloodyFilter  extends Filter {
    private int program;
    private  int texture2Id;

    public BloodyFilter(Context context) {
        super(context);

        // Build shaders
        program = LSYSUtility.buildProgram(context, R.raw.vertext, R.raw.bloody);

        texture2Id =  LSYSUtility.loadTexture(context, R.drawable.filter02, new int[2]);
    }

    @Override
    public void onDraw(int cameraTexId, int canvasWidth, int canvasHeight) {
        setupShaderInputs(program,
                new int[]{canvasWidth, canvasHeight},
                new int[]{cameraTexId},
                new int[][]{});
    }
}