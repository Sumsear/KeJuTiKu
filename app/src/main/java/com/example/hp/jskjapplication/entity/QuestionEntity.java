package com.example.hp.jskjapplication.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;

import cn.bmob.v3.BmobObject;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class QuestionEntity extends BmobObject implements Parcelable{

    private String q;
    private String a;

    public QuestionEntity(){

    }

    protected QuestionEntity(Parcel in) {
        q = in.readString();
        a = in.readString();
    }

    @Generated(hash = 1054151109)
    public QuestionEntity(String q, String a) {
        this.q = q;
        this.a = a;
    }

    public static final Creator<QuestionEntity> CREATOR = new Creator<QuestionEntity>() {
        @Override
        public QuestionEntity createFromParcel(Parcel in) {
            return new QuestionEntity(in);
        }

        @Override
        public QuestionEntity[] newArray(int size) {
            return new QuestionEntity[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
        dest.writeString(a);
    }

    public String getQ() {
        return this.q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getA() {
        return this.a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
