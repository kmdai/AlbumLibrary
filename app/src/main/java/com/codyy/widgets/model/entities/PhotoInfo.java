package com.codyy.widgets.model.entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

/**
 * Created by kmdai on 15-12-30.
 */
public class PhotoInfo implements Parcelable {
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
    private int mPosition;
    private boolean mCheck;

    public boolean ismCheck() {
        return mCheck;
    }

    public void setmCheck(boolean mCheck) {
        this.mCheck = mCheck;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

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

    public PhotoInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeLong(this.mSize);
        dest.writeString(this.mPath);
        dest.writeString(this.mID);
        dest.writeParcelable(this.mContent, 0);
        dest.writeInt(this.type);
        dest.writeInt(this.mPosition);
        dest.writeByte(mCheck ? (byte) 1 : (byte) 0);
    }

    protected PhotoInfo(Parcel in) {
        this.mName = in.readString();
        this.mSize = in.readLong();
        this.mPath = in.readString();
        this.mID = in.readString();
        this.mContent = in.readParcelable(Uri.class.getClassLoader());
        this.type = in.readInt();
        this.mPosition = in.readInt();
        this.mCheck = in.readByte() != 0;
    }

    public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {
        public PhotoInfo createFromParcel(Parcel source) {
            return new PhotoInfo(source);
        }

        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    };
}
