package com.example.administrator.lsys_camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.TextureView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.administrator.lsys_camera.filter.AsciiArtFilter;
import com.example.administrator.lsys_camera.filter.BloodyFilter;
import com.example.administrator.lsys_camera.filter.BrightFilter;
import com.example.administrator.lsys_camera.filter.CartoonFilter;
import com.example.administrator.lsys_camera.filter.ChromaticAberrationFilter;
import com.example.administrator.lsys_camera.filter.CrackedFilter;
import com.example.administrator.lsys_camera.filter.CrosshatchFilter;
import com.example.administrator.lsys_camera.filter.DiaFilter;
import com.example.administrator.lsys_camera.filter.EdgeDetectionFilter;
import com.example.administrator.lsys_camera.filter.Filter;
import com.example.administrator.lsys_camera.filter.GausianFilter;
import com.example.administrator.lsys_camera.filter.GrayScale1Filter;
import com.example.administrator.lsys_camera.filter.GrayScale2Filter;
import com.example.administrator.lsys_camera.filter.GrayScale3Filter;
import com.example.administrator.lsys_camera.filter.GreenFilterArctic;
import com.example.administrator.lsys_camera.filter.GreenFilterDesert;
import com.example.administrator.lsys_camera.filter.GreenFilterForest;
import com.example.administrator.lsys_camera.filter.GreenFilterOversky;
import com.example.administrator.lsys_camera.filter.GreenFilterRainbow;
import com.example.administrator.lsys_camera.filter.GreenFilterYourname;
import com.example.administrator.lsys_camera.filter.LegofiedFilter;
import com.example.administrator.lsys_camera.filter.LichtensteinEsqueFilter;
import com.example.administrator.lsys_camera.filter.MappingFilter;
import com.example.administrator.lsys_camera.filter.MappingFilterBlue1;
import com.example.administrator.lsys_camera.filter.MappingFilterBlue2;
import com.example.administrator.lsys_camera.filter.MappingFilterFire;
import com.example.administrator.lsys_camera.filter.MappingFilterLight;
import com.example.administrator.lsys_camera.filter.MappingFilterPink1;
import com.example.administrator.lsys_camera.filter.MappingFilterPink2;
import com.example.administrator.lsys_camera.filter.MappingFilterSmoke;
import com.example.administrator.lsys_camera.filter.MoneyFilter;
import com.example.administrator.lsys_camera.filter.MultiImageFilter;
import com.example.administrator.lsys_camera.filter.NoiseWarpFilter;
import com.example.administrator.lsys_camera.filter.OriginalFilter;
import com.example.administrator.lsys_camera.filter.PixelizeFilter;
import com.example.administrator.lsys_camera.filter.PolygonizationFilter;
import com.example.administrator.lsys_camera.filter.RefractionFilter;
import com.example.administrator.lsys_camera.filter.SimpleGreen2Filter;
import com.example.administrator.lsys_camera.filter.SimpleGreen3Filter;
import com.example.administrator.lsys_camera.filter.SimpleGreen4Filter;
import com.example.administrator.lsys_camera.filter.SimpleGreen5Filter;
import com.example.administrator.lsys_camera.filter.SimpleGreenFilter;
import com.example.administrator.lsys_camera.filter.TileMosaicFilter;
import com.example.administrator.lsys_camera.filter.GreenFilterBlossom;
import com.example.administrator.lsys_camera.filter.GreenFilterSpace;
import com.example.administrator.lsys_camera.filter.GreenFilterSea;
import com.example.administrator.lsys_camera.filter.GreenFilterGrand;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.SENSOR_SERVICE;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT;


//TESTEST
public class Renderer  implements Runnable, TextureView.SurfaceTextureListener {

    private static final int EGL_WINDOW_BIT = 4;
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int DRAW_INTERVAL = 1000 / 30;
    public static int control = 1;

    private int width, height;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    private EGLContext eglContext;
    private EGL10 egl10;

    private Camera camera;
    private SurfaceTexture cameraSurfaceTexture;
    private int cameraTextureId;
    public Thread renderThread;
    private Context context;
    private SurfaceTexture mainSurfaceTexture;
    private Filter selectedFilter;
    private int selectedFilterId = 0;
    private SparseArray<Filter> cameraFilterMap = new SparseArray<>();

    private boolean checkEdit=true;
    public boolean isFrontCamera = false;
    private boolean isFlash = false;
    private Camera.Parameters params;

    public SensorManager sensorManager;
    public OrientationListener oriListener;
    public Sensor oriSensor;
    private ImageView btChange;
    private ImageView btCapture;
    private ImageView btFlash;
    private ImageView btTimer;
    private  Switch switchOfGif;
    private Handler mHandler = new Handler();
    //private  StickerPro sticker;

    private Processing processing;
    private Object cameraUsed;
    private Thread saveThread;


    private float percentage=1;
    private Object drawObject;

    public static int checkmove;

    public ArrayList<StickerPro> stickerArry;
    public static int controlSticker = -1;


    // MainActivity의 요소들이 필요하므로 Context를 받아옴
    public Renderer(Context context, Object cameraUsed,Thread saveThread, Object drawObject) {
        this.context = context;
        this.cameraUsed = cameraUsed;
        this.saveThread = saveThread;
         this.drawObject = drawObject;
        checkmove = -1;
    }

    // SurfaceTuexture가 이용 가능하면 해당 함수 자동 실행,
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // 서피스텍스쳐 이용가능하면 카메라 오픈하고, 프리뷰 렌더링 스레드 실행

        if (renderThread != null && renderThread.isAlive()) {
            // 딴거 하다  다시 사진 찍을때 새로 쓰레드 생성
            renderThread.interrupt();
        }
        this.width = width;
        this.height = height;
         percentage=(float)width/(float)height;
        renderThread = new Thread(this);

        mainSurfaceTexture = surface;

        Processing.SetRotateScreen(0); // 프리뷰의 화면을 정방향으로 섷정
        StartCamera(Camera.CameraInfo.CAMERA_FACING_BACK); // 카메라 시작
        stickerArry= new ArrayList<StickerPro>();
//
//        // 카메라정보. 카메라 데이터를 어떤식으로 받아드릴지 결정
//        processing = new Processing(context);
//
//        // 스티커 객체 생성
//        sticker = new StickerPro(context);
//
//        // 카메라 프리뷰 Start
//        try {
//            //   camera.setPreviewTexture(cameraSurfaceTexture);
//            //   camera.startPreview();
//
//            // For 오토포커싱
//            // 센서 이벤트가  camera객체를 필요하므로 camera객체 생성뒤에  초기화
//            // 센서매니저에서 객체를얻어와서 방향센서설정 및 방향센서 리스너 생성
//            sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);    // SensorManager 인스턴스를 가져옴
//            oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);    // 방향 센서
//            oriListener = new OrientationListener(camera);        // 방향 센서 리스너 인스턴스
//            sensorManager.registerListener(oriListener, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);    // 방향 센서 리스너 오브젝트를 등록
//
//
//        } catch (Exception e) {
//            // Something bad happened
//            Log.e("before Thread"," error happend!!!");
//        }

        // Start rendering
        renderThread.start();
    }

    // SurfaceTexture가 사용불가능하면 해당함수 실행. destroy
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        synchronized (cameraUsed) {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
                Log.e("camera", " release");
            }
            if(sensorManager != null)
            sensorManager.unregisterListener(oriListener);    // unregister acceleration listener

            if(saveThread != null && saveThread.isAlive()){
                saveThread.interrupt();
            }

            if (renderThread != null && renderThread.isAlive()) {
                renderThread.interrupt();
            }

            if(processing != null)
            processing.release();

            // 초기화
            MainActivity.checked=false;
            MainActivity.newStickeron=false;
            MainActivity.textureviewTouch=false;
            MainActivity.TextureviewClicked=false;

            Log.e("SurfaceTextureDestroyed", " happen!");
        }
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }



    private void setMaxTextureSize() {

        //egl10 = (EGL10) EGLContext.getEGL();
        EGLContext ctx = egl10.eglGetCurrentContext();
        GL10 gl = (GL10) ctx.getGL();
        IntBuffer val = IntBuffer.allocate(1);
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, val);
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, val);

        int size = val.get(0); // 최대 크기 구함
        MainActivity.TEX_MAX_SIZE =size;
        Log.e("GL_MAX_TEXURE_SIZE: ", "  "+size);
        //Constants.setMaxTextureSize(size); // Constants는 글로벌 변수 저장용
    }

    // 받은 id를 위치한 필터의 객체를 얻는다.
    public void setSelectedFilter(int id) {
        selectedFilterId = id;
        selectedFilter = cameraFilterMap.get(id);
        if (selectedFilter != null)
            selectedFilter.onAttach();
    }

    // 카메라 객체 얻어오기
    public Camera getCamera()
    {
        if(camera != null)
            return camera;
        else
            return null;
    }

    // 카메라 열기
    private Camera OpenCamera(int facing) {
        int cameraCount;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int cameraId = 0; cameraId < cameraCount; cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == facing) {
                // 전면카메라일때
                try {
                    cam = Camera.open(cameraId);
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        return cam;
    }

    private void StartCamera(int facing) {
        camera = OpenCamera(facing);
        if (camera == null) { // 후면카메라일때
            camera = Camera.open();
            isFrontCamera = false;
        }
        // 새롭게 SurfaceTexture 생성

        // Texture 바인딩
        cameraTextureId = LSYSUtility.genTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        //cameraTextureId = LSYSUtility.genTexture(GLES11Ext.GL_TEXTURE_GEN_MODE_OES);
       // cameraTextureId = LSYSUtility.loadTexture(context, R.raw.tex00, new int[2]);
        // 바인딩으로 얻은 Texture를 이용하여 SurfaceTexture 생성
        cameraSurfaceTexture = new SurfaceTexture(cameraTextureId);

        try {
            // 프리뷰 설정
            camera.setPreviewTexture(cameraSurfaceTexture);
            camera.startPreview();
        } catch (IOException e) {
            // Something bad happened
        }
    }

    // 카메라 전후방 변환
    private void ChangeCamera()
    {
        synchronized (cameraUsed) {
            CloseCamera();
            Log.e("close"," camera Func");
            isFrontCamera = !isFrontCamera;

            if (isFrontCamera) {
                StartCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                Processing.SetRotateScreen(2);
            } else {
                StartCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                Processing.SetRotateScreen(0);
            }
            oriListener.SetCamera(camera);

            OrientationListener.mAutoFocus = true; // mAutoFocus 초기화
            MainActivity.pushChangeBtn = false; // 케메라 전환 완료
            MainActivity.pushFlashBtn = false; // 플래쉬 버튼 초기화
            Log.e("change","camera End");


            mHandler.post(new Runnable() {
                //            @Override
                public void run() {
                     // 버튼 초기화
                    AppCompatActivity activity = (AppCompatActivity)context;
                    btCapture = (ImageView)activity.findViewById(R.id.id_icon_circle);
                    btCapture.setEnabled(true);
                    btChange = (ImageView)activity.findViewById(R.id.id_icon_change);
                    btChange.setEnabled(true);
                    btFlash = (ImageView)activity.findViewById(R.id.id_icon_flash);
                    btFlash.setEnabled(true);
                    btTimer = (ImageView)activity.findViewById(R.id.id_icon_timer);
                    btTimer.setEnabled(true);
                    switchOfGif = (Switch)activity.findViewById(R.id.id_switch_gif);
                    switchOfGif.setEnabled(true);
                }
            });
        }
    }

    public void FlashOn()
    {
        if(camera != null) {
            params = camera.getParameters(); // open된 카메라의 설정값을 받아옴
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); //플래쉬 모드에서 플래쉬를 켠다
            camera.setParameters(params); // 설정한(open)한 카메라에 설정값을 저장
        }
    }

    public void FlashOff()
    {
        if(camera != null) {
            params = camera.getParameters(); // open된 카메라의 설정값을 받아옴
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF); //플래쉬 모드에서 플래쉬를 끈다
            camera.setParameters(params); // 설정한(open)한 카메라에 설정값을 저장
        }
    }

    private void CloseCamera()
    {
        if(camera!=null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void run() {
        // initEGL();
        initializeEGL(mainSurfaceTexture);
        setMaxTextureSize();

//        cameraFilterMap.append(0, new OriginalFilter(context)); // 오리지널
//        cameraFilterMap.append(1, new GrayScale1Filter(context)); // 흑백 1
//        cameraFilterMap.append(2, new GrayScale2Filter(context)); // 흑백 2
//        cameraFilterMap.append(3, new GrayScale3Filter(context)); // 흑백 3
//        cameraFilterMap.append(4, new BrightFilter(context)); // 백열등
//        cameraFilterMap.append(5, new EdgeDetectionFilter(context)); // 스케치 3
//        cameraFilterMap.append(6, new BloodyFilter(context)); // 흡혈귀
//        cameraFilterMap.append(7, new MultiImageFilter(context)); // 녹색이를 찾아라
//        cameraFilterMap.append(8, new PixelizeFilter(context)); // 모자이크 4
//        cameraFilterMap.append(9, new LegofiedFilter(context)); // 모자이크 3
//        cameraFilterMap.append(10, new TileMosaicFilter(context)); // 모자이크 2
//        cameraFilterMap.append(11, new ChromaticAberrationFilter(context)); // 울렁이
//        cameraFilterMap.append(12, new SimpleGreen2Filter(context)); // 그린스크린 무지개
//        cameraFilterMap.append(13, new SimpleGreen3Filter(context)); // 지지직맵핑
//        cameraFilterMap.append(14, new NoiseWarpFilter(context)); // 쭈구리
//        cameraFilterMap.append(15, new RefractionFilter(context)); // 울퉁불퉁
//        cameraFilterMap.append(16, new CrosshatchFilter(context)); // 스케치 1
//        cameraFilterMap.append(17, new AsciiArtFilter(context)); // 아스키
//        cameraFilterMap.append(18, new MoneyFilter(context)); // 스케치 2
//        cameraFilterMap.append(19, new CrackedFilter(context));  // 와장창
//        cameraFilterMap.append(20, new LichtensteinEsqueFilter(context)); // 모자이크 1
//        cameraFilterMap.append(21, new PolygonizationFilter(context)); // 부스스
//        cameraFilterMap.append(22, new GausianFilter(context)); // 흐림
//        cameraFilterMap.append(23, new CartoonFilter(context)); // 카툰
//        cameraFilterMap.append(24, new DiaFilter(context)); // 디아블로

//        cameraFilterMap.append(26, new SimpleGreenFilter(context)); // 그린스크린 맵핑

        // 시연을위해 삭제한 필터(확정)///////////////////
        //        cameraFilterMap.append(2, new SimpleGreen2Filter(context)); // 그린스크린 무지개
        //        cameraFilterMap.append(3, new DiaFilter(context)); // 디아블로
//        cameraFilterMap.append(14, new AsciiArtFilter(context)); // 아스키
//        cameraFilterMap.append(9, new PolygonizationFilter(context)); // 부스스
//        cameraFilterMap.append(10, new CrackedFilter(context));  // 와장창
        ////////////////////////////////////////////


        cameraFilterMap.append(0, new OriginalFilter(context)); // 오리지널
        cameraFilterMap.append(1, new BrightFilter(context)); // 백열등
        cameraFilterMap.append(2, new GrayScale1Filter(context)); // 흑백 1
        cameraFilterMap.append(3, new GrayScale2Filter(context)); // 흑백 2
        cameraFilterMap.append(4, new GrayScale3Filter(context)); // 흑백 3
        cameraFilterMap.append(5, new GausianFilter(context)); // 흐림
        cameraFilterMap.append(6, new BloodyFilter(context)); // 흡혈귀
        cameraFilterMap.append(7, new NoiseWarpFilter(context)); // 쭈구리
        cameraFilterMap.append(8, new RefractionFilter(context)); // 울퉁불퉁
        cameraFilterMap.append(9, new PixelizeFilter(context)); // 모자이크 4
        cameraFilterMap.append(10, new LegofiedFilter(context)); // 모자이크 3
        cameraFilterMap.append(11, new AsciiArtFilter(context)); // 아스키
        cameraFilterMap.append(12, new MoneyFilter(context)); // 스케치 2
        cameraFilterMap.append(13, new CrosshatchFilter(context)); // 스케치 1
        cameraFilterMap.append(14, new CartoonFilter(context)); // 카툰
        cameraFilterMap.append(15, new MappingFilter(context)); //  텍스쳐 맵핑 // 불로
        cameraFilterMap.append(16, new MultiImageFilter(context)); // 녹색이를 찾아라
        cameraFilterMap.append(17, new GreenFilterSpace(context)); // 그린스크린 맵핑_우주를줄게
        cameraFilterMap.append(18, new GreenFilterBlossom(context)); // 그린스크린 맵핑_벚꽃엔딩
        cameraFilterMap.append(19, new GreenFilterSea(context)); // 그린스크린 맵핑_여수밤바다
        cameraFilterMap.append(20, new GreenFilterArctic(context)); // 그린스크린 맵핑_북극
        cameraFilterMap.append(21, new GreenFilterDesert(context)); // 그린스크린 맵핑_사막
        cameraFilterMap.append(22, new GreenFilterForest(context)); // 그린스크린 맵핑_포레스트
        cameraFilterMap.append(23, new GreenFilterOversky(context)); // 그린스크린 맵핑_하늘위
        cameraFilterMap.append(24, new GreenFilterRainbow(context)); // 그린스크린 맵핑_무지개
        cameraFilterMap.append(25, new GreenFilterYourname(context)); // 그린스크린 맵핑_너의이름은
        cameraFilterMap.append(26, new GreenFilterGrand(context)); // 그린스크린 맵핑_그랜드캐니언




        // 임시필터 - greenscreen 대체, 살짝 테두리가 보임
        //cameraFilterMap.append(R.id.filter5, new SimpleGreen5Filter(context));

        // 받은 id를 textureView에 연결
        setSelectedFilter(selectedFilterId);

        // 카메라정보. 카메라 데이터를 어떤식으로 받아드릴지 결정
        processing = new Processing(context);

        // 스티커 객체 생성

        // 카메라 프리뷰 Start
        try {
            //   camera.setPreviewTexture(cameraSurfaceTexture);
            //   camera.startPreview();

            // For 오토포커싱
            // 센서 이벤트가  camera객체를 필요하므로 camera객체 생성뒤에  초기화
            // 센서매니저에서 객체를얻어와서 방향센서설정 및 방향센서 리스너 생성
            sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);    // SensorManager 인스턴스를 가져옴
            oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);    // 방향 센서
            oriListener = new OrientationListener(camera);        // 방향 센서 리스너 인스턴스
            sensorManager.registerListener(oriListener, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);    // 방향 센서 리스너 오브젝트를 등록


        } catch (Exception e) {
            // Something bad happened
            Log.e("in Start"," error happend!!!");
        }


        // Render loop
        // 현재스레드가 중지중이지 않은 상태
        // 종료시키고싶을때 interrupt 시켜주면 된다
        while (!Thread.currentThread().isInterrupted()) {
              synchronized (drawObject)
              {
                  checkEdit=false;
                  if(!MainActivity.canEdit)
                  {
                      checkEdit=true;
                  }

                  try {
                // 카메라 전후방 변환시
                if (MainActivity.pushChangeBtn) {
                    ChangeCamera();
                }

                // Buffer Bit Color 비워주기
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                // Update the camera preview texture
                // onSurfaceTextureUpdate 를 실행시켜줌
                synchronized (this) {
                    cameraSurfaceTexture.updateTexImage();
                }

                if(MainActivity.newStickeron)
                {
                    if(stickerArry.size()<3) {
                        stickerArry.add(new StickerPro(context, MainActivity.stickerNum, percentage));
                        StickerPro temp = stickerArry.get(stickerArry.size() - 1);
                        temp.stickerSwitch = true;
                    }
                    /*
                    if(!sticker.stickerSwitch)
                        sticker.stickerSwitch=true;
                    else if(!sticker2.stickerSwitch)
                        sticker2.stickerSwitch=true;
                    else if(!sticker3.stickerSwitch)
                        sticker3.stickerSwitch=true;
                    else if(!sticker4.stickerSwitch)
                        sticker4.stickerSwitch=true;
                    else if(!sticker5.stickerSwitch)
                        sticker5.stickerSwitch=true;
                        */
                    MainActivity.newStickeron=false;
                }

                // Draw camera preview
                // 카메라 프리뷰 그려주기
                processing.draw(cameraTextureId, width, height, selectedFilter, context); //////////////////////////////////////
                int releaseNum=-1;
                //    if (MainActivity.stickerOn || MainActivity.textOn)
                //        sticker.draw(width, height, MainActivity.textureviewTouch);
                //int releaseNum=-1;

                /* prev sticker draw
                for(int i=0;i<stickerArry.size();i++)
                {
                    StickerPro  temp=stickerArry.get(i);
                    temp.check(i);

                    temp.draw(width,height);
                    if(!temp.stickerSwitch)
                    {
                        temp.release();
                        stickerArry.remove(i);
                    }

                }
                */

                if(MainActivity.canEdit) {
                    for (int i = stickerArry.size() - 1; i >= 0; i--) {
                        StickerPro temp = stickerArry.get(i);
                        temp.check(i);


                        if (!temp.stickerSwitch) {
                            checkmove = -1;
                            temp.release();
                            releaseNum = i;

                        }

                    }
                }
                controlSticker=-1;

                if(releaseNum!=-1)
                {
                    stickerArry.remove(releaseNum);
                }
                for(int i=0;i<stickerArry.size();i++)
                {
                    StickerPro  temp=stickerArry.get(i);
                    temp.draw(width,height);
                }

                // Flush
                GLES20.glFlush();
                egl10.eglSwapBuffers(eglDisplay, eglSurface);

                //
                Thread.sleep(DRAW_INTERVAL);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
                  //싱크용 와일 삽입.
                  if(checkEdit)
                  {
                      MainActivity.canCapt=true;
                      checkEdit = false;
                      MainActivity.canEdit = true;
                  }
                  else
                  {
                      MainActivity.canCapt=false;
                  }
              } // syn
        }

        for(int i=0;i<stickerArry.size();i++)
        {
            stickerArry.get(i).release();
        }
        stickerArry.clear();
        Log.e("stickerArray ","clear");
        cameraSurfaceTexture.release();
        GLES20.glDeleteTextures(1, new int[]{cameraTextureId}, 0);
    }


    private void initEGL() {
        egl10 = (EGL10) EGLContext.getEGL();

        eglDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }

        int[] version = new int[2];
        if (!egl10.eglInitialize(eglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }

        int[] numOfConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        EGLConfig eglConfig = null;
        int[] configAttrib = {
                EGL10.EGL_RENDERABLE_TYPE, EGL_WINDOW_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
        };


        if (egl10.eglChooseConfig(eglDisplay, configAttrib, configs, 1, numOfConfigs)) //numOfConfigs로 제대로 선택되었는지 판별. numOfConfigs>0이면 제대로 선택된것
        {                                                                                 //eglChooseConfig는 configAttrib 속성과 일치하는 EGLConfig를 선택합니다.
            if (numOfConfigs[0] > 0)
                eglConfig = configs[0];
        } else {
            throw new IllegalArgumentException("eglChooseConfig failed " + android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }

        if (eglConfig == null) {
            throw new RuntimeException("eglConfig not initialized");
        }

        int[] contextAttrib = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
        eglContext = egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, contextAttrib);
        eglSurface = egl10.eglCreateWindowSurface(eglDisplay, eglConfig, mainSurfaceTexture, null); // null 위치에 전후방 버퍼 선택옵션?

        if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
            int error = egl10.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                Log.e("Renderer", "eglCreateWindowSurface returned EGL10.EGL_BAD_NATIVE_WINDOW");
                return;
            }
            throw new RuntimeException("eglCreateWindowSurface failed " +
                    android.opengl.GLUtils.getEGLErrorString(error));
        }

        if (!egl10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw new RuntimeException("eglMakeCurrent failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }
    }

    // opgles 3.0 프로그래밍 가이드 참조
    private void initializeEGL(Object nativeWindow) {

        egl10 = (EGL10) EGLContext.getEGL();

        int configAttribs[] =
                {
                        EGL10.EGL_RENDERABLE_TYPE, EGL_WINDOW_BIT,
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        EGL_NONE
                };
        int contextAttribs[] =
                {
                        EGL_CONTEXT_CLIENT_VERSION, 3,
                        EGL_NONE
                };

        //EGLDisplay display = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        eglDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if(eglDisplay == EGL10.EGL_NO_DISPLAY)
        {
            throw new RuntimeException("eglGetDisplay failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }

        int[] majorMinor = new int[2];
        if(!egl10.eglInitialize(eglDisplay,majorMinor))
        {
            throw new RuntimeException("eglInitialize failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }
        EGLConfig[] config = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if( !egl10.eglChooseConfig(eglDisplay,configAttribs,config,1,numConfigs))
        {
            throw new RuntimeException("eglChooseConfig failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }
        //EGLSurface window = egl10.eglCreateWindowSurface(eglDisplay,config[0],nativeWindow,null);
        eglSurface = egl10.eglCreateWindowSurface(eglDisplay,config[0],nativeWindow,null);

        if(eglSurface == EGL10.EGL_NO_SURFACE)
        {
            int error = egl10.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                Log.e("eglCreateWindowSurface", " EGL_BAD_NATIVE_WINDOW");
                return;
            }
            throw new RuntimeException("eglCreateWindowSurface failed " +
                    android.opengl.GLUtils.getEGLErrorString(error));
        }

        EGLContext context = egl10.eglCreateContext(eglDisplay,config[0],EGL_NO_CONTEXT,contextAttribs);

        if(context == EGL_NO_CONTEXT)
        {
            throw new RuntimeException("eglCreateContext failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }
        if(!egl10.eglMakeCurrent(eglDisplay,eglSurface,eglSurface,context))
        {
            throw new RuntimeException("eglMakeCurrent failed " +
                    android.opengl.GLUtils.getEGLErrorString(egl10.eglGetError()));
        }

    }
}
