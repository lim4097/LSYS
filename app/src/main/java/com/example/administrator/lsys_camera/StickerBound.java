package com.example.administrator.lsys_camera;


import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class StickerBound extends AbstractSticker {



    public  FloatBuffer VERTEX_BUF;
    int ORIGIN_PROGRAM = 0;


    private  FloatBuffer ROATED_TEXTURE_COORD_BUF;
    int[] textTexId;
    int[] stickerTexId;
    Context context;
    MiniSticker stickerX;
    MiniSticker stickerSize;
    MiniSticker stickerRL;

    int nowCheck;

    public StickerBound(Context context, FloatBuffer vertexBuf, FloatBuffer roatedTextureCoordBuf,boolean thisBoundChecked,float percent)
    {
        textTexId = new int[1];
        stickerTexId = new int[1];

        thisBoundChecked=false;
        VERTEX_BUF=vertexBuf;
        ROATED_TEXTURE_COORD_BUF=roatedTextureCoordBuf;

        stickerX=new MiniSticker(context,vertexBuf,roatedTextureCoordBuf,4, percent);
        stickerSize=new MiniSticker(context,vertexBuf,roatedTextureCoordBuf,2,percent);
        stickerRL=new MiniSticker(context,vertexBuf,roatedTextureCoordBuf,3,percent);

        ORIGIN_PROGRAM = LSYSUtility.buildProgram(context, R.raw.vertext, R.raw.original);

        this.context = context;


    }


    void draw(int canvasWidth, int canvasHeight,float SQUARE_COORDS[])
    {

        Bitmap textBitmap;

        GLES20.glUseProgram(ORIGIN_PROGRAM);

        // Use shaders


        stickerTexId[0] = LSYSUtility.loadStickerTexture(context, R.drawable.stickerbound);

        int vPositionLocation = GLES20.glGetAttribLocation(ORIGIN_PROGRAM, "vPosition");
        GLES20.glEnableVertexAttribArray(vPositionLocation);
        GLES20.glVertexAttribPointer(vPositionLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, VERTEX_BUF);



        int vTexCoordLocation = GLES20.glGetAttribLocation(ORIGIN_PROGRAM, "vTexCoord");
        GLES20.glEnableVertexAttribArray(vTexCoordLocation);
        GLES20.glVertexAttribPointer(vTexCoordLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, ROATED_TEXTURE_COORD_BUF);


        // Render to texture

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDeleteTextures(1, stickerTexId, 0);
        stickerX.draw(canvasWidth,canvasHeight);
        stickerSize.draw(canvasWidth,canvasHeight);
        stickerRL.draw(canvasWidth,canvasHeight);

    }


    int justcheck(float[] SQUARE_COORDS,StickerPro stickerPro)//체크하는 함수
    {
        float touchposX=MainActivity.touchPosX;
        float touchposY=MainActivity.touchPosY;

        boolean checkpos=checkPos(touchposX,touchposY,SQUARE_COORDS);//스티커 내부에 손가락 있는지?
        if(stickerPro.thisBoundChecked) {//이 스티커가 조절 가능한 스티커일시
            if (stickerX.check(touchposX, touchposY)) {//종료용 스티커에 들어갔을시
                MainActivity.checked = false;//스티커 조절 작업을 그만둠.(이미지 미선택 상태)
                stickerPro.stickerSwitch = false;//리스트에서 빼기위한 밑작업.
                return stickerX.miniType;//뭘 눌렀는지 반환(삭제=4
            }
            else if (stickerSize.check(touchposX, touchposY)) {
                return stickerSize.miniType;//뭘 눌렀는지 반환(크기=5

            }
            else if (stickerRL.check(touchposX, touchposY)) {

                return stickerRL.miniType;//좌우
            }
            else if (checkpos) {//스티커 내부에 손가락 잇으면
                return 1;//이동
            }
            else//조절 스티커나 스티커 내부에 손가락 없음
            {
                MainActivity.checked = false;//이미지 미선택 상태
                Renderer.checkmove=-1;
                return 0;//선택해제
            }
        }
        else //if(!MainActivity.checked)//아무 이미지도 미선택시>>에서 만약 이 이미지가 선택된 이미지가 아니라면
        {
            if (checkpos) {//스티커 내부 손가락?
                MainActivity.checked = true;//이미지 조절 or 선택중.
                return 1;//기본은 무브
            }
        }
        return  0;
    }

    void moveBound( float[] SQUARE_COORDS)
    {
        VERTEX_BUF = ByteBuffer.allocateDirect(SQUARE_COORDS.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        VERTEX_BUF.put(SQUARE_COORDS);

        VERTEX_BUF.position(0);
        stickerSize.move(SQUARE_COORDS[2],SQUARE_COORDS[3]);
        stickerX.move(SQUARE_COORDS[4],SQUARE_COORDS[5]);
        stickerRL.move(SQUARE_COORDS[6],SQUARE_COORDS[7]);

    }

    public void release() {
        stickerSize.release();
        stickerX.release();
        stickerRL.release();
        ORIGIN_PROGRAM = 0;
    }

    boolean checkPos(float x, float y,float[] SQUARE_COORDS)//손가락이 스티커 내부에 있는지 판단
    //수식은 기억 안남. 기울기 하고 이것저것 복잡.. 트루면 있는거고 폴스면 밖인거.
    {
        float slopeHor;
        float slopeVer;
        float horBot,horTop;
        float verLef,verRig;
        float horizon,vertical;

        Log.e("0","co");

        if(SQUARE_COORDS[0]!=SQUARE_COORDS[2])
        {
            slopeHor=(SQUARE_COORDS[3]-SQUARE_COORDS[1])/(SQUARE_COORDS[2]-SQUARE_COORDS[0]);

            horBot=SQUARE_COORDS[1]-slopeHor*SQUARE_COORDS[0];

            horTop=SQUARE_COORDS[5]-slopeHor*SQUARE_COORDS[4];
            horizon=y-x*slopeHor;

            if(horTop > horizon && horBot < horizon || horTop< horizon && horBot > horizon)
            {
                if(SQUARE_COORDS[4]!=SQUARE_COORDS[0]) {
                    slopeVer = (SQUARE_COORDS[5] - SQUARE_COORDS[1]) / (SQUARE_COORDS[4] - SQUARE_COORDS[0]);
                    verRig = SQUARE_COORDS[1] - slopeVer * SQUARE_COORDS[0];

                    verLef = SQUARE_COORDS[3] - slopeVer * SQUARE_COORDS[2];

                    vertical = y - x * slopeVer;

                    if (verLef > vertical && verRig < vertical || verLef < vertical && verRig > vertical)
                        return true;
                }
                else
                {
                    if (SQUARE_COORDS[0] > x && SQUARE_COORDS[2] < x || SQUARE_COORDS[0] < x && SQUARE_COORDS[2] > x)
                        return true;
                }
            }
        }
        else
        {
            if (SQUARE_COORDS[1] < y && SQUARE_COORDS[5] > y || SQUARE_COORDS[1] > y && SQUARE_COORDS[5] < y)
            {
                if(SQUARE_COORDS[4]!=SQUARE_COORDS[0]) {
                    slopeVer = (SQUARE_COORDS[5] - SQUARE_COORDS[1]) / (SQUARE_COORDS[4] - SQUARE_COORDS[0]);
                    verRig = SQUARE_COORDS[1] - slopeVer * SQUARE_COORDS[0];

                    verLef = SQUARE_COORDS[3] - slopeVer * SQUARE_COORDS[2];

                    vertical = y - x * slopeVer;
                    if (verLef > vertical && verRig < vertical || verLef < vertical && verRig > vertical)
                        return true;
                }
                else
                {
                    if(SQUARE_COORDS[0] > x && SQUARE_COORDS[2] < x || SQUARE_COORDS[0] < x && SQUARE_COORDS[2] > x)
                        return true;
                }
            }
        }
        return false;

    }




}
