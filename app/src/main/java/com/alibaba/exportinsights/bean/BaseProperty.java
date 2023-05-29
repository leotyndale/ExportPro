package com.alibaba.exportinsights.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseProperty implements Parcelable {
    public String name;
    public String value;

    public BaseProperty() {

    }

    protected BaseProperty(Parcel in) {
        name = in.readString();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseProperty> CREATOR = new Creator<BaseProperty>() {
        @Override
        public BaseProperty createFromParcel(Parcel in) {
            return new BaseProperty(in);
        }

        @Override
        public BaseProperty[] newArray(int size) {
            return new BaseProperty[size];
        }
    };
}
