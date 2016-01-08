package com.codyy.widgets.model.entities;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-1-8.
 */
public class AlbumBase {
    /**
     * 保存相册信息
     */
    public static ArrayList<PhotoInfo> mPhotoInfos;
    /**
     * 保存选择的图片信息
     */
    public static ArrayList<PhotoInfo> mSelectInfo;

    /**
     * 索引
     */
    public static int mIndex;
    /**
     * 索引最大值
     */
    public static int mMaxIndex;
    /**
     * 中间值
     */
    public static int mMedIndex;

    public static void clear() {
        mSelectInfo.clear();
        mSelectInfo = null;
        mPhotoInfos.clear();
        mPhotoInfos = null;
    }
}
