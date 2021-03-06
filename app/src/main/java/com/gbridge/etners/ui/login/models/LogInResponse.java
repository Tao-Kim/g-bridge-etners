package com.gbridge.etners.ui.login.models;

import com.google.gson.annotations.SerializedName;

public class LogInResponse {


    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("isAdmin")
    private Boolean isAdmin;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

}