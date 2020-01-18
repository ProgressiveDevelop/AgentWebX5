package com.just.x5.uploadFile;

import android.os.Parcel;
import android.os.Parcelable;

import com.just.x5.util.LogUtils;

/**
 *
 */

public class FilePO implements Parcelable {
    private int id;
    private String contentPath;
    private String fileBase64;

    protected FilePO(Parcel in) {
        id = in.readInt();
        contentPath = in.readString();
        fileBase64 = in.readString();
    }

    public FilePO(int id, String contentPath, String fileBase64) {
        this.id = id;
        this.contentPath = contentPath;
        this.fileBase64 = fileBase64;
        LogUtils.getInstance().e("FilePO", "file:" + fileBase64);
    }

    public static final Creator<FilePO> CREATOR = new Creator<FilePO>() {
        @Override
        public FilePO createFromParcel(Parcel in) {
            return new FilePO(in);
        }

        @Override
        public FilePO[] newArray(int size) {
            return new FilePO[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(contentPath);
        dest.writeString(fileBase64);
    }

    @Override
    public String toString() {
        return "FileParcel{" +
                "id=" + id +
                ", contentPath='" + contentPath + '\'' +
                ", fileBase64='" + fileBase64 + '\'' +
                '}';
    }
}
