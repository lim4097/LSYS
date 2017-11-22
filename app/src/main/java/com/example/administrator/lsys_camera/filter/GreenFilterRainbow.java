package com.example.administrator.lsys_camera.filter;

import android.content.Context;

import com.example.administrator.lsys_camera.LSYSUtility;
import com.example.administrator.lsys_camera.R;

public class GreenFilterRainbow extends Filter {
    private int program;
    private int texture2Id;

    public GreenFilterRainbow(Context context) {
        super(context);

        // Build shaders
        program = LSYSUtility.buildProgram(context, R.raw.vertext, R.raw.simple_green);

        // Load the texture will need for the shader
        texture2Id = LSYSUtility.loadTexture(context, R.raw.tex_rainbow, new int[2]);
    }

    @Override
    public void onDraw(int cameraTexId, int canvasWidth, int canvasHeight) {
        setupShaderInputs(program,
                new int[]{canvasWidth, canvasHeight},
                new int[]{texture2Id,cameraTexId},
                new int[][]{});
    }
}
