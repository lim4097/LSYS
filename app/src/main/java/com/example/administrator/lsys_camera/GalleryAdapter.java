package com.example.administrator.lsys_camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.io.File;
import java.io.ObjectInput;
import java.lang.ref.WeakReference;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;




public class GalleryAdapter extends BaseAdapter{
    int CustomGalleryItemBg;
    String mBasePath;
    Context mContext;
    static String[] mImgs;
    Bitmap bm;
    public static List<File>  fileList;
    DataSetObservable mDataSetObservable = new DataSetObservable(); // DataSetObservable(DataSetObserver)의 생성
    private List<WeakReference<View>> mRecycleList = new ArrayList<WeakReference<View>>();



    public String TAG = "Gallery Adapter Example :: ";

    public GalleryAdapter(Context context, String basepath){
        this.mContext = context;
        this.mBasePath = basepath;

        File file = new File(mBasePath);
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.e("","here");
            }
        }




        //mImgs = file.list();

        fileList= Arrays.asList( file.listFiles());
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                long flag = rhs.lastModified()-lhs.lastModified();
                if(flag < 0){
                    return -1;
                }else if(flag > 0){
                    return 1;
                }else{
                    return 0;
                }
            }
        });

        TypedArray array = mContext.obtainStyledAttributes(R.styleable.GalleryThema);
        CustomGalleryItemBg = array.getResourceId(0, R.styleable.GalleryThema_android_galleryItemBackground);
        array.recycle();
    }

    @Override
    public int getCount() {
        File dir = new File(mBasePath);
        mImgs = dir.list();
        return mImgs.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    // Adapter 내 Item에서 직접 주소를 받아오도록 method 추가.

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getItemPath(int position)
    {
        return fileList.get(position).getPath();
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

/*
        ImageView iv;
        if (convertView != null)
        iv = (ImageView)convertView;
        else {
            iv  = new ImageView(mContext);
            // ....
        }

        Bitmap bmpForImageView = mCache.get(Integer.toString(position));
        if (bmpForImageView == null) {
            bmpForImageView = decodeFile(..) / createScaledBitmap(...);
            mCache.put(Integer.toString(position), bmpFromImageView);
        }
        iv.setImageBitmap(bmpForImageView);
        return iv;

*/

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            // convertView가 null이면 Holder 객체를 생성하고
            // 생성한 Holder 객체에 inflating 한 뷰의 참조값을 저장
           // holder = new ViewHolder();
           // LayoutInflater inflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            //convertView = inflater.inflate(R.layout.photogallery, parent, false);
            //holder.image = (ImageView) convertView.findViewById(R.id.text_item_name);
            ////holder.textType = (TextView) convertView.findViewById(R.id.text_item_type);

            // View의 태그에 Holder 객체를 저장
            //convertView.setTag(holder);
        } else {
            imageView = (ImageView) convertView;
        }
        //String fileFullPath=mBasePath + File.separator + mImgs[position];

        try {
            imageView.setPadding(8, 8, 8, 8);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
            Glide.with(mContext).load(fileList.get(position).getPath()).thumbnail(0.1f).override(300,300).into(imageView);
        } catch (OutOfMemoryError e) {
            recycleHalf();
            System.gc();
            return getView(position, convertView, parent);
        }

        mRecycleList.add(new WeakReference<View>(imageView));



        return imageView;
    }

    public void recycleHalf() {
        int halfSize = mRecycleList.size() / 2;
        List<WeakReference<View>> recycleHalfList = mRecycleList.subList(0, halfSize);

        RecycleUtils.recursiveRecycle(recycleHalfList);

        for (int i = 0; i < halfSize; i++)
            mRecycleList.remove(0);
    }

    public void recycle() {
        RecycleUtils.recursiveRecycle(mRecycleList);
    }



    @Override
    public void registerDataSetObserver(DataSetObserver observer){ // DataSetObserver의 등록(연결)
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer){ // DataSetObserver의 해제
        mDataSetObservable.unregisterObserver(observer);
    }

    @Override
    public void notifyDataSetChanged(){ // 위에서 연결된 DataSetObserver를 통한 변경 확인
        mDataSetObservable.notifyChanged();
    }
}

