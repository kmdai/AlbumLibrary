package com.codyy.widgets.model.entities;

import android.net.Uri;

import java.net.URL;

/**
 * Created by kmdai on 15-12-30.
 */
public class PhotoInfo {
    /**
     * 相机
     */
    public final static int TYPE_CAMERA = 0x001;
    /**
     * 图片
     */
    public final static int TYPE_PHOTO = 0x002;
    private String mName;
    private long mSize;
    private String mPath;
    private String mID;
    private Uri mContent;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long mSize) {
        this.mSize = mSize;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public Uri getContent() {
        return mContent;
    }

    public void setContent(Uri mContent) {
        this.mContent = mContent;
    }
}
