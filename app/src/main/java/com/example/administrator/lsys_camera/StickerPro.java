package com.example.administrator.lsys_camera;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;

public class StickerPro extends AbstractSticker {

    public boolean moveOn=false;//이 스티커 이동할 예정일 시 트루
    public boolean rotateOn=false;//회전예정시 트루
    public boolean sizeOn=false;//크기 예정시

    private int stickerNum;//스티커의 이미지가 몇번째  이미지리소스 인지
    public boolean stickerSwitch;//좌우 변동인지?

    public boolean thisBoundChecked;//이 스티커 선택중인지?

    public float perc=1;//비율(핸드폰마다 사각형 비율이 다르게 나오는거 해결용)=>정사각형 변화

    StickerBound stickerBound;//윤곽선 클래스(스티커 클래스의 아래 클래스)

    float size =0.2f;//이미지 회전용

    float disatance =0.2f;//이미지 크기 조절용

    float tempX,tempY;

    //스티커의 중심점
    float posX=0.0f;
    float posY=0.0f;

    float cos=1.0f;
    float sin=0.0f;

    float SQUARE_COORDS[] = {
            0.2f, -0.2f,
            -0.2f, -0.2f,
            0.2f, 0.2f,
            -0.2f, 0.2f,
    };
    //회전 후의 코드
    float BEFORE_MOVE_SQUARE_COORDS[]= {
            0.2f, -0.2f,
            -0.2f, -0.2f,
            0.2f, 0.2f,
            -0.2f, 0.2f,
    };//돌리기 전의 코드
    public FloatBuffer VERTEX_BUF;
    int ORIGIN_PROGRAM = 0;

    public FloatBuffer ROATED_TEXTURE_COORD_BUF;

    int[] stickerTexId;

    public StickerPro(Context context,int Num,float percent)
    {

        stickerTexId = new int[1];
        stickerNum=Num;//스티커리소스 번호 받아옴
        perc=percent;//화면 비율도 받아옴.

        for(int i=0;i<4;i++)//화면 비율에 따라 이미지 정사각형화
        {
            SQUARE_COORDS[i*2+1]=(BEFORE_MOVE_SQUARE_COORDS[i*2+1])*perc;
        }

        // Setup default Buffers
        if (VERTEX_BUF == null) {
            VERTEX_BUF = ByteBuffer.allocateDirect(SQUARE_COORDS.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            VERTEX_BUF.put(SQUARE_COORDS);
            VERTEX_BUF.position(0);
        }

        if (ROATED_TEXTURE_COORD_BUF == null) {
            ROATED_TEXTURE_COORD_BUF = ByteBuffer.allocateDirect(ROATED_TEXTURE_COORDS.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            ROATED_TEXTURE_COORD_BUF.put(ROATED_TEXTURE_COORDS);
            ROATED_TEXTURE_COORD_BUF.position(0);
        }


        ORIGIN_PROGRAM = LSYSUtility.buildProgram(context, R.raw.vertext, R.raw.original);

        this.context = context;
        stickerBound= new StickerBound(context, VERTEX_BUF, ROATED_TEXTURE_COORD_BUF,thisBoundChecked,percent);//윤곽선 생성해주기(보이지거나 작동은 않음)

        stickerSwitch=false;//좌우반전 ㄴㄴ


    }


    void check(int num)
    {
        if(MainActivity.TextureviewClicked) {//텍스쳐 뷰가 처음 눌렷을시
            moveOn=false;
            rotateOn=false;
            //예정이 없다고 각각 초기화 하는것

            switch (stickerBound.justcheck(SQUARE_COORDS, this)) {
                case 1: {
                    if(Renderer.checkmove<=num)
                    {
                        moveOn = true;//움직일 얘정
                        MainActivity.TextureviewClicked=false;//더 이상 텍스쳐뷰가 눌린걸로 판단하지 않음.
                        Renderer.checkmove=num;
                        tempX=MainActivity.touchPosX-posX;
                        tempY=MainActivity.touchPosY-posY;
                    }
                    break;
                }
                case 2://size and Rotate
                {
                    rotateOn = true;
                    MainActivity.TextureviewClicked=false;
                    break;
                }
                case 3: {//좌우
                    textureLR();
                    MainActivity.TextureviewClicked=false;
                    break;
                }
                case 0: {//아무일도 일어나지 않음.
                    MainActivity.TextureviewClicked=true;
                    break;
                }
                case 4: {
                    MainActivity.TextureviewClicked=false;
                    break;
                }
            }
        }


        if(Renderer.checkmove==num)
        {
            thisBoundChecked=true;
        }
        else
        {
            thisBoundChecked=false;
        }

        if(MainActivity.textureviewTouch&&!MainActivity.TextureviewClicked&&thisBoundChecked) {
            //텍스쳐 뷰 누른 상태에서 손을 안때고 움직일때.

            //예정이 있으면?
            if(moveOn)
            {
                move();//그림 이동
            }
            if(rotateOn)
            {
                setRotate();//회전
            }
        }
    }


    void draw(int canvasWidth, int canvasHeight)
    {
        Bitmap textBitmap;


        GLES20.glUseProgram(ORIGIN_PROGRAM);




        if(!MainActivity.canEdit)
        {
            thisBoundChecked=false;
            Renderer.checkmove=-1;
        }
        if(thisBoundChecked) {//선택된 스티커가 이거면?
            stickerBound.draw(canvasWidth, canvasHeight,SQUARE_COORDS);
        }
        //윤곽선이 스티커를 따라 나타남.

        //알맞은 이미지 리소스 할당.
        if(stickerNum==1)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_01);
        else if(stickerNum==2)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_02);
        else if(stickerNum==3)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_03);
        else if(stickerNum==4)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_04);
        else if(stickerNum==5)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_05);
        else if(stickerNum==6)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_06);
        else if(stickerNum==7)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_07);
        else if(stickerNum==8)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_08);
        else if(stickerNum==9)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_09);
        else if(stickerNum==10)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_10);
        else if(stickerNum==11)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_11);
        else if(stickerNum==12)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_12);
        else if(stickerNum==13)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_13);
        else if(stickerNum==14)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_14);
        else if(stickerNum==15)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_15);
        else if(stickerNum==16)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_16);
        else if(stickerNum==17)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_17);
        else if(stickerNum==18)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_18);
        else if(stickerNum==19)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_19);
        else if(stickerNum==20)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_20);
        else if(stickerNum==21)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_21);
        else if(stickerNum==22)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_22);
        else if(stickerNum==23)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_23);
        else if(stickerNum==24)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_24);
        else if(stickerNum==25)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_25);
        else if(stickerNum==26)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_26);
        else if(stickerNum==27)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_27);
        else if(stickerNum==28)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_28);
        else if(stickerNum==29)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_29);
        else if(stickerNum==30)
            stickerTexId[0] = LSYSUtility.loadStickerTexture(context,R.drawable.sticker_line_30);

        int vPositionLocation = GLES20.glGetAttribLocation(ORIGIN_PROGRAM, "vPosition");
        GLES20.glEnableVertexAttribArray(vPositionLocation);
        GLES20.glVertexAttribPointer(vPositionLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, VERTEX_BUF);


        int vTexCoordLocation = GLES20.glGetAttribLocation(ORIGIN_PROGRAM, "vTexCoord");
        GLES20.glEnableVertexAttribArray(vTexCoordLocation);
        GLES20.glVertexAttribPointer(vTexCoordLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, ROATED_TEXTURE_COORD_BUF);

        // Render to texture

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDeleteTextures(1, stickerTexId, 0);

    }

    void move()//움직이는 함수
    {



        posX = MainActivity.touchPosX-tempX; // virtual center coord
        posY = MainActivity.touchPosY-tempY;
        upload();

    }

    void textureLR()//스티커 텍스쳐만 좌우 반전
    {
        for(int i=0;i<5;i+=4)
        {
            float temp;
            temp=ROATED_TEXTURE_COORDS[i];
            ROATED_TEXTURE_COORDS[i]=ROATED_TEXTURE_COORDS[i+2];
            ROATED_TEXTURE_COORDS[i+2]=temp;

            ROATED_TEXTURE_COORD_BUF = ByteBuffer.allocateDirect(ROATED_TEXTURE_COORDS.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            ROATED_TEXTURE_COORD_BUF.put(ROATED_TEXTURE_COORDS);
            ROATED_TEXTURE_COORD_BUF.position(0);
        }
    }

    //계산식은 주석으로 자세히 설명하지는 않을 것. 적어도 의미가..



    void setRotate()//일단 우측 하단을 기준으로 함.
    {
        //touchX,Y는 유저의 손가락 위치를 나타내는 변수이며 기본적으로 우측 하단을 기준으로 한다.
        float touchposX=MainActivity.touchPosX; //touch value
        float touchposY=MainActivity.touchPosY;
        //정사각형의 중심점 을 원점으로 옮겼을시의 가상의 touchX,Y값
        float virtualX;
        float virtualY;


        //square= 중심점부터 손가락위치까지의 거리의 제곱 값
        float square;


        virtualX=touchposX-posX; // vector a
        virtualY=touchposY-posY; // vector a
        square=virtualX*virtualX+virtualY*virtualY; // vector a size^2



        if(square!=0) {
            size = (float) Math.sqrt(square / 2); // vector a가 사각형의 중점으로부터 우측 꼭지점으로 가는 벡터,
            // vector b는 크기가 a이고 방향이 (1/sqrt(2),-1/sqrt(2))인 벡터

            //cos = (size * virtualX - size*virtualY) / square;
            cos = (size * virtualX - size*virtualY) / square;
            sin = (float) Math.sqrt(1 - (cos * cos));

            if((virtualX+virtualY)<0)//가상의 touchpos(X,Y)가 y=-x의 왼쪽아래일경우
                sin=-sin;//sin의 값을 음수로 설정.

            if(size<0.1f)
                size=0.1f;

            calcSize(0,cos,sin);
            calcSize(1,cos,sin);
            calcSize(2,cos,sin);
            calcSize(3,cos,sin);


            upload();

        }

    }

    void calcSize(int number,float cos, float sin)
    {
        int x=number*2;
        int y=1+x;
        float virtualX=-size;
        float virtualY=size;

        if(number==1)
        {
            virtualX=size;
            virtualY=-size;
        }
        if(number==3)
        {

            virtualY=-size;
        }
        if(number==0)
        {
            virtualX=size;
        }


        float sizeX=virtualX*cos-virtualY*sin;
        float sizeY=virtualX*sin+virtualY*cos;

        BEFORE_MOVE_SQUARE_COORDS[x]=sizeX;
        BEFORE_MOVE_SQUARE_COORDS[y]=sizeY;


    }


    void upload()//설정을 마친 모서리를 그리기 위한 배열에 넣음.
    {
        for(int i=0;i<4;i++)
        {
            SQUARE_COORDS[i*2]=BEFORE_MOVE_SQUARE_COORDS[i*2]+posX;
            SQUARE_COORDS[i*2+1]=(BEFORE_MOVE_SQUARE_COORDS[i*2+1]+posY)*perc;
        }

        VERTEX_BUF = ByteBuffer.allocateDirect(SQUARE_COORDS.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        VERTEX_BUF.put(SQUARE_COORDS);

        stickerBound.moveBound(SQUARE_COORDS);
        VERTEX_BUF.position(0);

    }
    public void release() {//리소스 해제시 하위것들 해제하고 자신 해제후 선택 반환.
        stickerBound.release();
        ORIGIN_PROGRAM = 0;
        stickerSwitch=false;
    }


}
