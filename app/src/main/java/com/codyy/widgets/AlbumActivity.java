package com.codyy.widgets;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private ArrayList<PhotoInfo> mPhotoInfos;
    private AlbumAdapter mAlbumAdapter;


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
            mRecyclerView.setAdapter(mAlbumAdapter);
        }
        if (mPhotoInfos.size() == 0) {
            new LocalData().execute(0);
        }
    }

    /**
     * 粗略压缩
     *
     * @param path
     * @return
     */
    @Deprecated
    private Bitmap getFileBitmap(String path) {
        File file = new File(path);
        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            if (file.length() >= 1024 * 1024) {
                bitmapOptions.inSampleSize = 8;
            } else {
                bitmapOptions.inSampleSize = 5;
            }

            return BitmapFactory.decodeStream(new FileInputStream(file), null, bitmapOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    class LocalData extends AsyncTask<Integer, Integer, ArrayList<PhotoInfo>> {
        @Override
        protected ArrayList<PhotoInfo> doInBackground(Integer... params) {
            ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
            Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE};
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(externalContentUri, projection, null, null, null);
                String imageId;
                Uri imageUri;
                while (cursor.moveToNext()) {
                    imageId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                    PhotoInfo photoInfo = new PhotoInfo();
                    photoInfo.setContent(imageUri);
                    photoInfo.setType(PhotoInfo.TYPE_PHOTO);
                    photoInfo.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
                    photoInfo.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    photoInfo.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                    photoInfos.add(photoInfo);
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