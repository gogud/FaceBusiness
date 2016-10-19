package com.example.mg_win.facebusiness.FaceAPI;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by mg-Win on 1.09.2016.
 */
public class FaceEnroll extends AsyncTask<Object, Boolean, String> {

    private static String TAG = "FaceEnroll";
    public FaceEnrollResponse delegate = null;

    @Override
    protected void onPostExecute(String results) {

        if (results != null ) {
            delegate.processFaceEnroll(results);
        } else {
            Log.d(TAG, "onPostExecute: Result is NULL!");
            delegate.nullDataOnFaceEnroll();
        }
    }

    @Override
    protected String doInBackground(Object... objects) {

        String imgId = enroll((byte[]) objects[0]);

        if (imgId.toString().length() > 0) {
            return imgId;
        } else {
            return null;
        }
    }

    // Enroll Face
    public static String enroll(byte[] imageArray) {
        Log.d(TAG, " enroll face");

        String result = "";

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://api.findface.pro/face/");
        post.setHeader("Authorization", "Token bd25a609ed43f3ff9661435548144405");
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        entity.addPart("photo", new ByteArrayBody(imageArray, "image/jpeg", "photo.jpg"));
        post.setEntity(entity);

        try {
            HttpResponse response = httpClient.execute(post);

            if (response.getEntity().getContentLength() > 0) {
                String json_string = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(json_string);


                Log.d(TAG, "Enroll Result: " + json_string);

                if (jsonObject.getString("id").length() > 0) {

                    result = jsonObject.getString("id");

                    return result;
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "Cannot execute post request: " + e.getMessage());
            return "";
        } catch (JSONException e) {
            Log.d(TAG, "Cannot create json object: " + e.getMessage());
            return "";
        }
        return "";
    }
}
