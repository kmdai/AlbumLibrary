package com.codyy.widgets.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.widgets.R;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

/**
 * Created by kmdai on 15-12-31.
 */
public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<PhotoInfo> mPhotoInfos;

    public AlbumAdapter(Context context, ArrayList<PhotoInfo> mPhotoInfos, ImagePipelineConfig imagePipelineConfig) {
        this.mContext = context;
        this.mPhotoInfos = mPhotoInfos;
        Fresco.initialize(context, imagePipelineConfig);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PhotoInfo.TYPE_CAMERA:

                break;
            case PhotoInfo.TYPE_PHOTO:
//                GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(mContext.getResources())
//                        .setPlaceholderImage(mContext.getResources().getDrawable(R.drawable.ic_launcher))
//                        .setFailureImage(mContext.getResources().getDrawable(R.drawable.ic_launcher))
//                        .setProgressBarImage(new ProgressBarDrawable())
//                        .setActualImageScaleType(ScalingUtils.ScaleType.CENTER)
//                        .build();
//                SimpleDraweeView simpleDraweeView = new SimpleDraweeView(mContext, gdh);
                return new AlbumHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_album_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case PhotoInfo.TYPE_CAMERA:
                break;
            case PhotoInfo.TYPE_PHOTO:
                AlbumHolder albumHolder = (AlbumHolder) holder;
                ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(mPhotoInfos.get(position).getContent());
                imageRequestBuilder.setResizeOptions(new ResizeOptions(
                        albumHolder.mSimpleDraweeView.getLayoutParams().width,
                        albumHolder.mSimpleDraweeView.getLayoutParams().height));
                albumHolder.mSimpleDraweeView.setImageURI(mPhotoInfos.get(position).getContent());
                ImageRequest imageRequest = imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true).build();
                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(albumHolder.mSimpleDraweeView.getController())
                        .setAutoPlayAnimations(true)
                        .build();
                albumHolder.mSimpleDraweeView.setController(draweeController);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPhotoInfos.get(position).getType();
    }

    public void shutDown() {
        Fresco.shutDown();
    }

    /**
     * 图片
     */
    class AlbumHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mSimpleDraweeView;

        public AlbumHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpledraweeview_item);
        }
    }

    /**
     * 相机
     */
    class CameraHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mSimpleDraweeView;

        public CameraHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpledraweeview_item);
        }
        void init(){
        }
    }
}