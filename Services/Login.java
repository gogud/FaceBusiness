package com.example.mg_win.facebusiness.Services;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mg_win.facebusiness.Utils.UserInfo;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mg-Win on 27.08.2016.
 */
public class Login extends AsyncTask<Object, String, UserInfo> {

    private static int INSERTCONNECTIONTIMEOUT = 15;
    private static String TAG = "LoginService";
    private static String SERVICE_IP_ADDRESS = "192.168.2.201";
    public LoginResponse delegate = null;

    UserInfo userInfo = new UserInfo();

    @Override
    protected UserInfo doInBackground(Object... params) {

        if (params[0].toString() != null && params[1].toString() != null) {
            Log.d(TAG, "Login User Service: phone = " + params[0].toString() + " password = " + params[1].toString());
            userInfo = loginUser(params[0].toString(), params[1].toString());
        }

        return userInfo;
    }

    @Override
    protected void onPostExecute(UserInfo userInfo) {

        if (userInfo != null) {
            delegate.processLogin(userInfo);
        }
        else {
            delegate.nullDataReturned();
        }

    }

    private UserInfo loginUser(String phone, String password) {

        String connectionUrl = "http://" +
                SERVICE_IP_ADDRESS +
                ":8084/FaceCardService/webresources/service/login?phone=" + phone + "&password=" + password ;

        /*
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("phone", "5061477867"));
        nameValuePairs.add(new BasicNameValuePair("password", "mg751209"));

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, INSERTCONNECTIONTIMEOUT);
        */

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(connectionUrl);

        try {
            //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse =  client.execute(post);

            HttpEntity httpEntity = httpResponse.getEntity();

            String json_string = EntityUtils.toString(httpEntity);

            Log.d(TAG, "Login Service Json String : " + json_string);
            Log.d(TAG, "Login Service Json String Length : " + json_string.length());

            if (json_string.length() > 10) {

                JSONObject jsonObject = new JSONObject(json_string);


                userInfo.setmPhone(jsonObject.getString("userPhone"));
                userInfo.setmPassword(jsonObject.getString("userPassword"));
                userInfo.setmName(jsonObject.getString("userName"));
                userInfo.setmSurname(jsonObject.getString("userSurname"));
                userInfo.setmMiddleName(jsonObject.getString("userMiddleName"));
                userInfo.setmEmail(jsonObject.getString("userEmail"));
                userInfo.setmCompany(jsonObject.getString("userCompany"));
                userInfo.setmTitle(jsonObject.getString("userTitle"));
                userInfo.setmImageId(jsonObject.getString("userImageId"));

            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return userInfo;
    }
}
