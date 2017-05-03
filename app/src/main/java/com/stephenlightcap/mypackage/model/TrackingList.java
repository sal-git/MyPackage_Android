package com.stephenlightcap.mypackage.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Germex on 4/20/2017.
 */

public class TrackingList implements Parcelable {

    private String message;

    public TrackingList() {
    }

    public TrackingList(String message) {
        this.message = message;
    }

    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private TrackingList(Parcel in) {
        this.message = in.readString();
    }

    public static final Parcelable.Creator<TrackingList> CREATOR = new Parcelable.Creator<TrackingList>() {

        @Override
        public TrackingList createFromParcel(Parcel source) {
            return new TrackingList(source);
        }

        @Override
        public TrackingList[] newArray(int size) {
            return new TrackingList[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getMessage(){
        return message;
    }

}
