package com.example.mg_win.facebusiness.Services;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mg_win.facebusiness.Utils.UserInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by mg-Win on 27.08.2016.
 */
public class Register extends AsyncTask<Object, String, String> {

    private static String TAG = "Register";
    private static String SERVICE_IP_ADDRESS = "192.168.2.201";
    public RegisterResponse delegate = null;


    UserInfo userInfo = new UserInfo();

    @Override
    protected String doInBackground(Object... objects) {

        registerUser((UserInfo) objects[0]);

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        if (s != null) {
            delegate.processRegister(s);
        } else {
            delegate.nullDataReturnedFromRegister();
        }
    }

    private String registerUser(UserInfo userInfo) {

        String tmpPhone = "phone=" + userInfo.getmPhone();
        String tmpPassword = "password=" + userInfo.getmPassword();
        String tmpName = "name=" + userInfo.getmName();
        String tmpSurname = "surname=" + userInfo.getmSurname();
        String tmpMiddleName = "middlename=" + userInfo.getmMiddleName();
        String tmpEmail = "email=" + userInfo.getmEmail();
        String tmpCompany = "company=" + userInfo.getmCompany();
        String tmpTitle = "title=" + userInfo.getmTitle();
        String tmpImageId = "imageid=" + userInfo.getmImageId();

        String connectionUrl = "http://" +
                SERVICE_IP_ADDRESS +
                ":8084/FaceCardService/webresources/service/register?" +
                tmpPhone + "&" +
                tmpPassword + "&" +
                tmpName + "&" +
                tmpSurname + "&" +
                tmpMiddleName + "&" +
                tmpEmail + "&" +
                tmpCompany + "&" +
                tmpTitle + "&" +
                tmpImageId + "&";

        Log.d(TAG, "Connection URL: " + connectionUrl);


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(connectionUrl);

        try {
            //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse =  client.execute(post);

            HttpEntity httpEntity = httpResponse.getEntity();

            String json_string = EntityUtils.toString(httpEntity);

            Log.d(TAG, "Login Service Json String : " + json_string);
            Log.d(TAG, "Login Service Json String Length : " + json_string.length());

            // Check wheter executeUpdate is true or not!

            //TODO!! Handle registered or not!
            if (json_string.equalsIgnoreCase("true")) {
                // Successfully updated!
                return userInfo.getmPhone();
            } else {
                // Not updated!
                return null;
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
