package com.codyy.widgets.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.widgets.AlbumActivity;
import com.codyy.widgets.R;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kmdai on 15-12-31.
 */
public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<PhotoInfo> mPhotoInfos;
    private TakePhoto mTakePhoto;

    public AlbumAdapter(Context context, ArrayList<PhotoInfo> mPhotoInfos, ImagePipelineConfig imagePipelineConfig) {
        this.mContext = context;
        this.mPhotoInfos = mPhotoInfos;
        Fresco.initialize(context, imagePipelineConfig);
    }

    public TakePhoto getmTakePhoto() {
        return mTakePhoto;
    }

    public void setmTakePhoto(TakePhoto mTakePhoto) {
        this.mTakePhoto = mTakePhoto;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PhotoInfo.TYPE_CAMERA:
                return new CameraHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_album_camera, parent, false));
            case PhotoInfo.TYPE_PHOTO:
                return new AlbumHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_album_item, parent, false));
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

        public CameraHolder(View itemView) {
            super(itemView);
            init();
        }

        void init() {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTakePhoto != null) {
                        mTakePhoto.takePhoto();
                    }
                }
            });
        }
    }

    public interface TakePhoto {
        void takePhoto();
    }
}
