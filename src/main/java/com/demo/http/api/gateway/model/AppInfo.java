package com.demo.http.api.gateway.model;
/**
* app info model
* 
* @author wang guobo 王国波
*/
public class AppInfo {
    String appKey;
    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
