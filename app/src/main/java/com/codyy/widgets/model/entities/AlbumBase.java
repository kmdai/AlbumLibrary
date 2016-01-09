package com.codyy.widgets.model.entities;

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

    public static void scanPhoto() {

    }
}
