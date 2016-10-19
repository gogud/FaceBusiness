package com.example.mg_win.facebusiness.Services;

import com.example.mg_win.facebusiness.Utils.UserInfo;

/**
 * Created by mg-Win on 27.08.2016.
 */
public interface LoginResponse {
    void processLogin(UserInfo userInfo);
    void nullDataReturned();
}
