package com.example.mg_win.facebusiness.Utils;

/**
 * Created by mg-Win on 29.08.2016.
 */
public class FormatStrings {

    public String formatPhone(String phone) {

        String tmp = phone.replaceAll("[^0-9]", "");

        return tmp;
    }
}
