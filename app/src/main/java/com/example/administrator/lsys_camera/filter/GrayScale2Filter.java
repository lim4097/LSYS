package com.example.administrator.lsys_camera.filter;

import android.content.Context;

import com.example.administrator.lsys_camera.LSYSUtility;
import com.example.administrator.lsys_camera.R;

public class GrayScale2Filter extends Filter {
    private int program;

    public GrayScale2Filter(Context context) {
        super(context);

        // Build shaders
        program = LSYSUtility.buildProgram(context, R.raw.vertext, R.raw.basic_b_c_s_2);
    }

    @Override
    public void onDraw(int cameraTexId, int canvasWidth, int canvasHeight) {
        setupShaderInputs(program,
                new int[]{canvasWidth, canvasHeight},
                new int[]{cameraTexId},
                new int[][]{});
    }
}