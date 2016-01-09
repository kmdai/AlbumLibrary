package com.codyy.widgets;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.widgets.model.entities.AlbumBase;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.zoomable.ZoomableDraweeView;

import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity implements OnClickListener {
    public final static String IMAGE_TYPT = "image_info";
    public final static String PAGE_INFO = "page_info";
    /**
     * 全部预览
     */
    public final static int TYPE_ALL = 0x001;
    /**
     * 选中预览
     */
    public final static int TYPE_PREVIEW = 0x002;

    private int mType;
    private ViewPager mViewPager;
    private ArrayList<PhotoInfo> mPhotoInfoList;
    private PreviewAdapter mPreviewAdapter;
    private int mPage;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private TextView mTitleSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);
        mType = getIntent().getIntExtra(IMAGE_TYPT, TYPE_ALL);
        mPage = getIntent().getIntExtra(PAGE_INFO, 0);
        if (mType == TYPE_ALL) {
            mPhotoInfoList = AlbumBase.PHOTO_INFO;
        } else {
            mPhotoInfoList = new ArrayList<>(AlbumBase.SELECT_INFO);
        }
        init();
        mViewPager.setCurrentItem(mPage, false);
    }

    private void init() {
        mTitleView = (TextView) findViewById(R.id.title_size);
        mTitleSelect = (TextView) findViewById(R.id.title_select);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.preview_viewpager);
        mPreviewAdapter = new PreviewAdapter();
        mViewPager.setAdapter(mPreviewAdapter);
        mToolbar.collapseActionView();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        if (mPhotoInfoList != null && mPhotoInfoList.size() > 0) {
            PhotoInfo info = mPhotoInfoList.get(mPage);
            mTitleView.setText((mPage + 1) + "/" + mPhotoInfoList.size());
            if (info.ismCheck()) {
                mTitleSelect.setBackgroundResource(R.drawable.oval_bg_true);
                mTitleSelect.setText(String.valueOf(info.getmPosition()));
            } else {
                mTitleSelect.setBackgroundResource(R.drawable.oval_bg_pre_false);
                mTitleSelect.setText("");
            }
        }
        mTitleSelect.setOnClickListener(this);
    }

    public List<PhotoInfo> getPhotoInfoList() {
        return mPhotoInfoList;
    }

    public void setPhotoInfoList(ArrayList<PhotoInfo> mPhotoInfoList) {
        this.mPhotoInfoList = mPhotoInfoList;
        mPreviewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(AlbumActivity.GET_SELET_INFO);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(AlbumActivity.GET_SELET_INFO);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mTitleView.setText((position + 1) + "/" + mPhotoInfoList.size());
            PhotoInfo photoInfo = mPhotoInfoList.get(position);
            if (photoInfo.ismCheck()) {
                mTitleSelect.setBackgroundResource(R.drawable.oval_bg_true);
                mTitleSelect.setText(String.valueOf(photoInfo.getmPosition()));
            } else {
                mTitleSelect.setBackgroundResource(R.drawable.oval_bg_pre_false);
                mTitleSelect.setText("");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_select:
                PhotoInfo info = mPhotoInfoList.get(mViewPager.getCurrentItem());
                AlbumBase.onPhotoSelect(info);
                if (!info.ismCheck()) {
                    mTitleSelect.setText("");
                    mTitleSelect.setBackgroundResource(R.drawable.oval_bg_pre_false);
                } else {
                    mTitleSelect.setText(String.valueOf(info.getmPosition()));
                    mTitleSelect.setBackgroundResource(R.drawable.oval_bg_true);
                }
                break;
        }
    }

    class PreviewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPhotoInfoList == null ? 0 : mPhotoInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomableDraweeView view = new ZoomableDraweeView(container.getContext());
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + mPhotoInfoList.get(position).getPath())).setAutoRotateEnabled(true).build();
            view.setController(Fresco.newDraweeControllerBuilder().setImageRequest(imageRequest)
                    .setAutoPlayAnimations(true)
                    .build());
            GenericDraweeHierarchy hierarchy =
                    new GenericDraweeHierarchyBuilder(container.getResources())
                            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                            .build();
            view.setHierarchy(hierarchy);
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }
    }

}
