package com.stephenlightcap.mypackage.model;

import android.graphics.Bitmap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Germex on 3/17/2017.
 */

public class Package extends RealmObject {

    @PrimaryKey
    private int primaryKey;
    private String fromAddress;
    private String toAddress;
    private String tracking;
    private byte[] barcode;

    public Package() {

    }

    public Package(int primaryKey, String fromAddress, String toAddress, String tracking, byte[] barcode) {
        this.primaryKey = primaryKey;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.tracking = tracking;
        this.barcode = barcode;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public byte[] getBarcode() {
        return barcode;
    }

    public void setBarcode(byte[] barcode) {
        this.barcode = barcode;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }
}
