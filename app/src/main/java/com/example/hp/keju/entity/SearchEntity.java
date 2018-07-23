package com.example.hp.keju.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SearchEntity implements Parcelable{

    private String status;
    private List<QuestionEntity> list;

    public SearchEntity() {
    }

    protected SearchEntity(Parcel in) {
        status = in.readString();
        list = in.createTypedArrayList(QuestionEntity.CREATOR);
    }

    public static final Creator<SearchEntity> CREATOR = new Creator<SearchEntity>() {
        @Override
        public SearchEntity createFromParcel(Parcel in) {
            return new SearchEntity(in);
        }

        @Override
        public SearchEntity[] newArray(int size) {
            return new SearchEntity[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<QuestionEntity> getList() {
        return list;
    }

    public void setList(List<QuestionEntity> list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeTypedList(list);
    }
}
