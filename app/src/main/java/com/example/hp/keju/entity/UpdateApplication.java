package com.example.hp.keju.entity;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;

public class UpdateApplication extends BmobObject implements Parcelable {

    private String version;
    private int versionCode;
    private String versionInfo;
    private String download;

    public UpdateApplication() {
    }

    protected UpdateApplication(Parcel in) {
        version = in.readString();
        versionCode = in.readInt();
        versionInfo = in.readString();
        download = in.readString();
    }

    public static final Creator<UpdateApplication> CREATOR = new Creator<UpdateApplication>() {
        @Override
        public UpdateApplication createFromParcel(Parcel in) {
            return new UpdateApplication(in);
        }

        @Override
        public UpdateApplication[] newArray(int size) {
            return new UpdateApplication[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeInt(versionCode);
        dest.writeString(versionInfo);
        dest.writeString(download);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }


}
