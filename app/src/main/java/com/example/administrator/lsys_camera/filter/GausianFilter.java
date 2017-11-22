package com.example.administrator.lsys_camera.filter;


import android.content.Context;

import com.example.administrator.lsys_camera.LSYSUtility;
import com.example.administrator.lsys_camera.R;

public class GausianFilter extends Filter {
    private int program;

    public GausianFilter(Context context) {
        super(context);

        // Build shaders
        program = LSYSUtility.buildProgram(context, R.raw.vertext, R.raw.gausian_filter);
    }

    @Override
    public void onDraw(int cameraTexId, int canvasWidth, int canvasHeight) {
        setupShaderInputs(program,
                new int[]{canvasWidth, canvasHeight},
                new int[]{cameraTexId},
                new int[][]{});
    }
}
