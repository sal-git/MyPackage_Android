package com.stephenlightcap.mypackage.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Germex on 3/29/2017.
 */

public class Tracks extends RealmObject {

    private String tracking;
    private String name;

    public Tracks() {

    }

    public Tracks(String name, String tracking) {
        this.tracking = tracking;
        this.name = name;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
