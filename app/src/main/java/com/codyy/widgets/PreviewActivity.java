package com.codyy.widgets;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.zoomable.ZoomableDraweeView;

import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    public final static String IMAGE_INFO = "image_info";
    public final static String PAGE_INFO = "page_info";
    private ViewPager mViewPager;
    private List<PhotoInfo> mPhotoInfoList;
    private PreviewAdapter mPreviewAdapter;
    private int mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);
        mPhotoInfoList = getIntent().getParcelableArrayListExtra(IMAGE_INFO);
        mPage = getIntent().getIntExtra(PAGE_INFO, 0);
        init();
        mViewPager.setCurrentItem(mPage, false);
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.preview_viewpager);
        mPreviewAdapter = new PreviewAdapter();
        mViewPager.setAdapter(mPreviewAdapter);
    }

    public List<PhotoInfo> getPhotoInfoList() {
        return mPhotoInfoList;
    }

    public void setPhotoInfoList(List<PhotoInfo> mPhotoInfoList) {
        this.mPhotoInfoList = mPhotoInfoList;
        mPreviewAdapter.notifyDataSetChanged();
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
            view.setController(Fresco.newDraweeControllerBuilder()
                    .setUri(mPhotoInfoList.get(position).getContent())
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
