package com.codyy.widgets.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.widgets.PreviewActivity;
import com.codyy.widgets.R;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
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
    private ArrayList<PhotoInfo> mPhotoSelect;
    private TakePhoto mTakePhoto;
    /**
     * 索引
     */
    private int mIndex;
    /**
     * 索引最大值
     */
    private int mMaxIndex;
    /**
     * 中间值
     */
    private int mMedIndex;

    public AlbumAdapter(Context context, ArrayList<PhotoInfo> mPhotoInfos, ImagePipelineConfig imagePipelineConfig) {
        mPhotoSelect = new ArrayList<>();
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
                //position减一，因为第一个是拍照功能
                bindViewHoler(holder, position - 1);
                break;
        }
    }

    private void bindViewHoler(RecyclerView.ViewHolder holder, final int position) {
        final PhotoInfo info = mPhotoInfos.get(position);
        final AlbumHolder albumHolder = (AlbumHolder) holder;
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(info.getContent());
        imageRequestBuilder.setResizeOptions(new ResizeOptions(
                albumHolder.mSimpleDraweeView.getLayoutParams().width,
                albumHolder.mSimpleDraweeView.getLayoutParams().height));
        albumHolder.mSimpleDraweeView.setImageURI(info.getContent());
        ImageRequest imageRequest = imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true).build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(albumHolder.mSimpleDraweeView.getController())
                .setAutoPlayAnimations(true)
                .build();
        albumHolder.mSimpleDraweeView.setController(draweeController);
        albumHolder.mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PreviewActivity.class);
                intent.putParcelableArrayListExtra(PreviewActivity.IMAGE_INFO, mPhotoInfos);
                intent.putExtra(PreviewActivity.PAGE_INFO, position);
                mContext.startActivity(intent);
            }
        });
        if (info.ismCheck()) {
            int index = info.getmPosition();
            albumHolder.mTextView.setText(String.valueOf(index));
            info.setSize(-1);
            albumHolder.mTextView.setBackgroundResource(R.drawable.oval_bg_true);
        } else {
            albumHolder.mTextView.setText("");
            info.setSize(1);
            albumHolder.mTextView.setBackgroundResource(R.drawable.oval_bg_false);
        }
        albumHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.ismCheck()) {
                    mPhotoSelect.remove(info);
                    if (info.getmPosition() < mMaxIndex) {
                        mMedIndex = info.getmPosition();
                        setPosition();
                        notifyDataSetChanged();
                    }
                    mIndex--;
                    info.setmPosition(-1);
                    albumHolder.mTextView.setText("");
                    info.setmCheck(false);
                    albumHolder.mTextView.setBackgroundResource(R.drawable.oval_bg_false);
                } else {
                    albumHolder.mTextView.setText(String.valueOf(++mIndex));
                    mPhotoSelect.add(mIndex - 1, info);
                    mMaxIndex = mIndex;
                    info.setmCheck(true);
                    info.setmPosition(mIndex);
                    albumHolder.mTextView.setBackgroundResource(R.drawable.oval_bg_true);
                }
                if (mTakePhoto != null) {
                    mTakePhoto.imageSelect();
                }
            }
        });
    }

    /**
     * 设置position
     */
    private void setPosition() {
        for (PhotoInfo info : mPhotoSelect) {
            int index = info.getmPosition();
            if (index > mMedIndex) {
                info.setmPosition(--index);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoInfos.size() <= 0 ? 1 : mPhotoInfos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? PhotoInfo.TYPE_CAMERA : mPhotoInfos.get(position - 1).getType();
    }

    public void shutDown() {
        Fresco.shutDown();
    }

    /**
     * 图片
     */
    class AlbumHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mSimpleDraweeView;
        TextView mTextView;

        public AlbumHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpledraweeview_item);
            mTextView = (TextView) itemView.findViewById(R.id.textview_check);
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

    public ArrayList<PhotoInfo> getSelectImage() {
        return mPhotoSelect;
    }

    public interface TakePhoto {
        void takePhoto();

        void imageSelect();
    }
}
