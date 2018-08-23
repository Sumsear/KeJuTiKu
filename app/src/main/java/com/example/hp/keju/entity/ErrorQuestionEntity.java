package com.example.hp.keju.entity;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;

public class ErrorQuestionEntity extends BmobObject implements Parcelable{


    private String q;

    public ErrorQuestionEntity() {
    }

    public ErrorQuestionEntity(String q) {
        this.q = q;
    }

    protected ErrorQuestionEntity(Parcel in) {
        q = in.readString();
    }

    public static final Creator<ErrorQuestionEntity> CREATOR = new Creator<ErrorQuestionEntity>() {
        @Override
        public ErrorQuestionEntity createFromParcel(Parcel in) {
            return new ErrorQuestionEntity(in);
        }

        @Override
        public ErrorQuestionEntity[] newArray(int size) {
            return new ErrorQuestionEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }
}
