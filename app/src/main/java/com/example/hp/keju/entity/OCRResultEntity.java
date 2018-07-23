package com.example.hp.keju.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OCRResultEntity implements Parcelable{

    private long log_id;
    private int direction;
    private int words_result_num;
    private List<Result> words_result;


    protected OCRResultEntity(Parcel in) {
        log_id = in.readLong();
        direction = in.readInt();
        words_result_num = in.readInt();
        words_result = in.createTypedArrayList(Result.CREATOR);
    }

    public static final Creator<OCRResultEntity> CREATOR = new Creator<OCRResultEntity>() {
        @Override
        public OCRResultEntity createFromParcel(Parcel in) {
            return new OCRResultEntity(in);
        }

        @Override
        public OCRResultEntity[] newArray(int size) {
            return new OCRResultEntity[size];
        }
    };

    public long getLogId() {
        return log_id;
    }

    public void setLogId(long logId) {
        this.log_id = logId;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getWordsResultNum() {
        return words_result_num;
    }

    public void setWordsResultNum(int wordsResultNum) {
        this.words_result_num = words_result_num;
    }

    public List<String> getWordsResult() {
        List<String> result = new ArrayList<>();
        for (Result r : words_result){
            result.add(r.toString());
        }
        return result;
    }

    public void setWordsResult(List<String> wordsResult) {
        this.words_result = words_result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(log_id);
        dest.writeInt(direction);
        dest.writeInt(words_result_num);
        dest.writeTypedList(words_result);
    }

    public static class Result implements Parcelable{
        private String words;


        protected Result(Parcel in) {
            words = in.readString();
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel in) {
                return new Result(in);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(words);
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        @Override
        public String toString() {
            return words;
        }
    }
}
