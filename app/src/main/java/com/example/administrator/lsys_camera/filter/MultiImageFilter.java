package com.example.administrator.lsys_camera.filter;


import android.content.Context;

import com.example.administrator.lsys_camera.LSYSUtility;
import com.example.administrator.lsys_camera.R;

public class MultiImageFilter extends Filter {
    private int program;
    private int texture2Id;

    public MultiImageFilter(Context context) {
        super(context);

        // Build shaders
        program = LSYSUtility.buildProgram(context, R.raw.vertext, R.raw.multi_image);

        // Load the texture will need for the shader
       // texture2Id = LSYSUtility.loadTexture(context, R.raw.tex00, new int[2]);
    }

    @Override
    public void onDraw(int cameraTexId, int canvasWidth, int canvasHeight) {
        setupShaderInputs(program,
                new int[]{canvasWidth, canvasHeight},
                new int[]{cameraTexId, cameraTexId},
                new int[][]{});
    }
}