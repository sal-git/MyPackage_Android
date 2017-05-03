package com.stephenlightcap.mypackage;

import android.app.Application;

import org.json.JSONObject;

/**
 * Created by Germex on 5/2/2017.
 */
public class MyApplication extends Application {

    private String name;
    //Didn't get the chance to do trackings
    private String[] trackings;
    private String password;
    private JSONObject globalObjectFromUser;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public JSONObject getGlobalObjectFromUser() {
        return globalObjectFromUser;
    }

    public void setGlobalObjectFromUser(JSONObject globalObjectFromUser) {
        this.globalObjectFromUser = globalObjectFromUser;
    }
}
