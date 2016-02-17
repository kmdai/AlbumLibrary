package com.codyy.widgets.model.entities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-1-8.
 */
public class AlbumBase {
    /**
     * 保存相册信息
     */
    public static ArrayList<PhotoInfo> PHOTO_INFO;
    /**
     * 保存选择的图片信息
     */
    public static ArrayList<PhotoInfo> SELECT_INFO;

    /**
     * 索引
     */
    public static int MAX_INDEX;
    /**
     * 中间值
     */
    public static int MED_INDEX;
    /**
     * 最多选择多少张图片
     */
    public static int MAX_SELECT = -1;
    /**
     * 单张图片的大小
     */
    public static int MAX_SIZE = -1;

    public static void initialize() {
        PHOTO_INFO = new ArrayList<>();
        SELECT_INFO = new ArrayList<>();
    }

    public static void selectInit(@NonNull ArrayList<PhotoInfo> infos) {
        if (SELECT_INFO == null) {
            throw new NullPointerException("AlbumBase not initialize!!!");
        }
        SELECT_INFO.clear();
        SELECT_INFO.addAll(infos);
    }

    /**
     * 数据初始化
     */
    public static void dataInit() {
        MAX_INDEX = SELECT_INFO.size();
        MED_INDEX = -1;
    }

    public static void clear() {
        if (SELECT_INFO != null) {
            SELECT_INFO.clear();
            SELECT_INFO = null;
        }
        if (PHOTO_INFO != null) {
            PHOTO_INFO.clear();
            PHOTO_INFO = null;
        }
    }

    /**
     * 设置position
     */
    public static void setPosition() {
        for (PhotoInfo info : SELECT_INFO) {
            int index = info.getmPosition();
            if (index > MED_INDEX) {
                info.setmPosition(--index);
            }
        }
    }

    public static PhotoInfo getPhotoAt(int position) {
        if (PHOTO_INFO != null) {
            return PHOTO_INFO.get(position);
        }
        return null;
    }

    /**
     * 选择照片
     *
     * @param info
     */
    public static void onPhotoSelect(PhotoInfo info) {
        if (info.ismCheck()) {
            SELECT_INFO.remove(info);
            MED_INDEX = info.getmPosition();
            setPosition();
            MAX_INDEX = SELECT_INFO.size();
            info.setmPosition(-1);
            info.setmCheck(false);
        } else {
            SELECT_INFO.add(info);
            MAX_INDEX = SELECT_INFO.size();
            info.setmCheck(true);
            info.setmPosition(MAX_INDEX);
        }
    }

    public static boolean scanPhoto(Context context) {
        String mPicId = MediaStore.Images.Media._ID;
        String mPicData = MediaStore.Images.Media.DATA;
        String mPicName = MediaStore.Images.Media.DISPLAY_NAME;
        String mPicSize = MediaStore.Images.Media.SIZE;
        String mDateAdd = MediaStore.Images.Media.DATE_ADDED;
        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {mPicId, mPicData, mPicName, mPicSize, mDateAdd};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(externalContentUri, projection, null, null, mDateAdd + " DESC");
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
                    if (AlbumBase.SELECT_INFO != null) {
                        hasSelect(photoInfo);
                    }
                    PHOTO_INFO.add(photoInfo);
                }
            }
        } catch (Exception e) {
            Log.e("", e.toString());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        System.out.println(PHOTO_INFO.size());
        return true;
    }

    protected static void hasSelect(PhotoInfo photoInfo) {
        for (PhotoInfo info : AlbumBase.SELECT_INFO) {
            if (info.getPath().equals(photoInfo.getPath())) {
                photoInfo.setmCheck(true);
                photoInfo.setmPosition(info.getmPosition());
                break;
            }
        }
    }
}
