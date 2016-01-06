package com.codyy.widgets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.codyy.widgets.adapter.AlbumAdapter;
import com.codyy.widgets.imagepipeline.ImagePipelineConfigFactory;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlbumActivity extends AppCompatActivity implements AlbumAdapter.TakePhoto {
    private RecyclerView mRecyclerView;
    private ArrayList<PhotoInfo> mPhotoInfos;
    private AlbumAdapter mAlbumAdapter;
    private String mPhotoName;
    /**
     * 图片路径基本目录
     */
    public static final String IMAGE_BASE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final int TAKE_PHOTO = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_album);
        init();
    }

    private void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPhotoInfos == null) {
            mPhotoInfos = new ArrayList<>();
            mAlbumAdapter = new AlbumAdapter(AlbumActivity.this, mPhotoInfos, ImagePipelineConfigFactory.getImagePipelineConfig(AlbumActivity.this));
            mAlbumAdapter.setmTakePhoto(this);
            mRecyclerView.setAdapter(mAlbumAdapter);
        }
        if (mPhotoInfos.size() == 0) {
            new LocalData().execute(0);
        }
    }

    @Override
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        long createTime = System.currentTimeMillis();
        Date d1 = new Date(createTime);
        String t1 = format.format(d1);
        String cameraPath = AlbumActivity.IMAGE_BASE_PATH + "/Camera/";
        File file = new File(cameraPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        mPhotoName = cameraPath + t1 + ".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + mPhotoName));
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    File file = new File(mPhotoName);
                    if (file.exists()) {
                        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        mediaScanIntent.setData(Uri.parse("file://" + mPhotoName));
                        sendBroadcast(mediaScanIntent);
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setType(PhotoInfo.TYPE_PHOTO);
                        photoInfo.setPath(mPhotoName);
                        photoInfo.setContent(Uri.fromFile(file));
                        photoInfo.setSize(file.length());
                        mPhotoInfos.add(0, photoInfo);
                        mAlbumAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumAdapter != null) {
            mAlbumAdapter.shutDown();
        }
    }

    class LocalData extends AsyncTask<Integer, Integer, ArrayList<PhotoInfo>> {
        String mPicId = MediaStore.Images.Media._ID;
        String mPicData = MediaStore.Images.Media.DATA;
        String mPicName = MediaStore.Images.Media.DISPLAY_NAME;
        String mPicSize = MediaStore.Images.Media.SIZE;
        String mDateAdd = MediaStore.Images.Media.DATE_ADDED;

        @Override
        protected ArrayList<PhotoInfo> doInBackground(Integer... params) {
            ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
            Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {mPicId, mPicData, mPicName, mPicSize, mDateAdd};
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(externalContentUri, projection, null, null, mDateAdd + " DESC");
                String imageId;
                Uri imageUri;
                int idNmb = cursor.getColumnIndexOrThrow(mPicId);
                int sizeNmb = cursor.getColumnIndex(mPicSize);
                int dataNmb = cursor.getColumnIndex(mPicData);
                int nameNmb = cursor.getColumnIndex(mPicName);
                while (cursor.moveToNext()) {
                    long size = cursor.getLong(sizeNmb);
                    if (size > 0) {
                        imageId = cursor.getString(idNmb);
                        imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setContent(imageUri);
                        photoInfo.setType(PhotoInfo.TYPE_PHOTO);
                        photoInfo.setSize(size);
                        photoInfo.setPath(cursor.getString(dataNmb));
                        photoInfo.setName(cursor.getString(nameNmb));
                        photoInfos.add(photoInfo);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return photoInfos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPhotoInfos.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<PhotoInfo> s) {
            super.onPostExecute(s);
            mPhotoInfos.addAll(s);
            mAlbumAdapter.notifyDataSetChanged();
        }
    }
}
