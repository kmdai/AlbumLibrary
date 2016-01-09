package com.codyy.widgets;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.widgets.adapter.AlbumAdapter;
import com.codyy.widgets.imagepipeline.ImagePipelineConfigFactory;
import com.codyy.widgets.model.entities.AlbumBase;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlbumActivity extends AppCompatActivity implements AlbumAdapter.TakePhoto, OnClickListener {
    /**
     * 传入已选择的图片的信息
     */
    public static final String SET_SELECT_INFO = "set_select_info";
    private RecyclerView mRecyclerView;
    private AlbumAdapter mAlbumAdapter;
    private String mPhotoName;
    /**
     * 图片路径基本目录
     */
    public static final String IMAGE_BASE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final int TAKE_PHOTO = 0x001;
    /**
     * 获取预览返回数据
     */
    public static final int GET_SELET_INFO = 0x002;
    /**
     * 预览图片数目
     */
    private TextView mPreviewSize;
    /**
     * 预览
     */
    private TextView mPreview;
    /**
     * 确定发送
     */
    private TextView mSend;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        AlbumBase.initialize();
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();
        ArrayList<PhotoInfo> infos = intent.getParcelableArrayListExtra(SET_SELECT_INFO);
        if (infos != null) {
            AlbumBase.selectInit(infos);
        }
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mPreviewSize = (TextView) findViewById(R.id.tv_preview_image);
        mPreview = (TextView) findViewById(R.id.textView_preview);
        mPreview.setOnClickListener(this);
        mSend = (TextView) findViewById(R.id.btn_ok);
        mSend.setOnClickListener(this);
        if (AlbumBase.SELECT_INFO == null || AlbumBase.SELECT_INFO.size() == 0) {
            mPreviewSize.setVisibility(View.GONE);
        }
        mToolbar.collapseActionView();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AlbumBase.PHOTO_INFO == null || AlbumBase.PHOTO_INFO.size() == 0) {
            mAlbumAdapter = new AlbumAdapter(AlbumActivity.this, ImagePipelineConfigFactory.getImagePipelineConfig(AlbumActivity.this));
            mAlbumAdapter.setmTakePhoto(this);
            mRecyclerView.setAdapter(mAlbumAdapter);
        }
        if (AlbumBase.PHOTO_INFO.size() == 0) {
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
    public void imageSelect() {
        int a = AlbumBase.SELECT_INFO.size();
        if (a == 0) {
            mPreviewSize.setVisibility(View.GONE);
        } else if (mPreviewSize.getVisibility() == View.GONE) {
            mPreviewSize.setVisibility(View.VISIBLE);
        }
        mPreviewSize.setText(String.valueOf(a));
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
                        AlbumBase.PHOTO_INFO.add(0, photoInfo);
                        mAlbumAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case GET_SELET_INFO:
                imageSelect();
                mAlbumAdapter.dataInit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlbumBase.clear();
        if (mAlbumAdapter != null) {
            mAlbumAdapter.shutDown();
        }
        System.gc();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_preview:
                if (AlbumBase.SELECT_INFO.size() > 0) {
                    Intent intent = new Intent(this, PreviewActivity.class);
                    intent.putExtra(PreviewActivity.IMAGE_TYPT, PreviewActivity.TYPE_PREVIEW);
                    intent.putExtra(PreviewActivity.PAGE_INFO, 0);
                    startActivityForResult(intent, GET_SELET_INFO);
                } else {
                    Snackbar.make(v, getResources().getString(R.string.no_select), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_ok:
                break;
        }
    }


    class LocalData extends AsyncTask<Integer, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(Integer... params) {
            return AlbumBase.scanPhoto(AlbumActivity.this);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlbumBase.dataInit();
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            if (flag) {
                if (mAlbumAdapter != null) {
                    mAlbumAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
