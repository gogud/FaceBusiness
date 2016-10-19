package com.example.mg_win.facebusiness.FaceAPI;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
/**
 * Created by mg-Win on 16.08.2016.
 */
public class FaceRecognition extends AsyncTask<Object, Boolean, FaceRecognition.FaceRecognitionResult[]> {
    private static String TAG = "FaceRecognition";

    public FaceRecognitionResponse delegate = null;
    private static String imageId = null;

    /*
    public class FaceRecognitionResultData<FaceRecognitionResult> {
        public final String resultCode;
        public final String methodName;
        public final FaceRecognitionResult result;

        public FaceRecognitionResultData(String resultCode, String methodName, FaceRecognitionResult result) {
            this.resultCode = resultCode;
            this.methodName = methodName;
            this.result = result;
        }
    }
    */

    // FaceAPI result class
    public static class FaceRecognitionResult {
        public String confidence;
        public String thumbnail;
        public int x1;
        public int x2;
        public int y1;
        public int y2;
        public String resultCode;
        public String methodName;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(FaceRecognitionResult[] results) {
        //super.onPostExecute(results);
        if (results != null ) {
            delegate.processFaceRecognition(results);
        } else {
            Log.d(TAG, "onPostExecute: Result is NULL!");
            delegate.nullDataReturned();
        }
    }

    @Override
    protected FaceRecognitionResult[] doInBackground(Object... objects) {

        FaceRecognitionResult[] faceRecognitionResults = null;

        if (objects[0].toString() == "detect") {
            faceRecognitionResults = detect((byte[]) objects[1]);
            if (faceRecognitionResults != null) {
                return faceRecognitionResults;
            }

        }  else if (objects[0].toString() == "identify") {
            try {
                faceRecognitionResults = identify((byte[]) objects[1]);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
            if (faceRecognitionResults != null) {
                return faceRecognitionResults;
            }

        } else {
            Log.d(TAG, "Unknown API function: " + objects[0].toString());
            return null;
        }


        return null;
    }


    // Detect Faces
    public static FaceRecognitionResult[] detect(byte[] imageArray) {
        Log.d(TAG, " detect faces");

        int faceCounts = 0;
        FaceRecognitionResult[] faceRecognitionResults = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://api.findface.pro/detect/");
        post.setHeader("Authorization", "Token bd25a609ed43f3ff9661435548144405");
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        entity.addPart("photo", new ByteArrayBody(imageArray, "image/jpeg", "photo"));
        post.setEntity(entity);

        try {
            HttpResponse response = httpClient.execute(post);

            if (response.getEntity().getContentLength() > 0) {
                String json_string = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(json_string);

                Log.d(TAG, "Detect Result: " + json_string);
                Log.d(TAG, "Result Lenght: " + jsonObject.getJSONArray("faces").length());

                faceCounts = jsonObject.getJSONArray("faces").length();
                if (faceCounts > 0) {

                    faceRecognitionResults = new FaceRecognitionResult[faceCounts];
                    for (int i = 0; i < faceCounts; i++) {
                        faceRecognitionResults[i] = new FaceRecognitionResult();

                        JSONArray faceArray = jsonObject.getJSONArray("faces");
                        faceRecognitionResults[i].x1 = Integer.parseInt(faceArray.getJSONObject(i).getString("x1"));
                        faceRecognitionResults[i].y1 = Integer.parseInt(faceArray.getJSONObject(i).getString("y1"));
                        faceRecognitionResults[i].x2 = Integer.parseInt(faceArray.getJSONObject(i).getString("x2"));
                        faceRecognitionResults[i].y2 = Integer.parseInt(faceArray.getJSONObject(i).getString("y2"));

                        faceRecognitionResults[0].methodName = "detect";

                    }
                    return faceRecognitionResults;
                } else {
                    /*
                    faceRecognitionResults = new FaceRecognitionResult[1];
                    faceRecognitionResults[0] = new FaceRecognitionResult();
                    faceRecognitionResults[0].resultCode = "NO_FACES";
                    */
                    return null;
                }

            }

        } catch (IOException e) {
            Log.d(TAG, "Cannot execute post request: " + e.getMessage());
        } catch (JSONException e) {
            Log.d(TAG, "Cannot create json object: " + e.getMessage());
        }

        return null;
    }

    // Enroll Face
    public static String enroll(byte[] imageArray) {
        Log.d(TAG, " enroll face");


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

                    imageId = jsonObject.getString("id");

                    return imageId;
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "Cannot execute post request: " + e.getMessage());
            return null;
        } catch (JSONException e) {
            Log.d(TAG, "Cannot create json object: " + e.getMessage());
            return null;
        }
        return null;
    }

    // Identify Face
    public static FaceRecognitionResult[] identify(byte[] imageArray) throws UnsupportedEncodingException {
        Log.d(TAG, " identify face");

        FaceRecognitionResult[] faceRecognitionResults = null;
        int faceCounts = 0;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://api.findface.pro/identify/");
        post.setHeader("Authorization", "Token bd25a609ed43f3ff9661435548144405");
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        entity.addPart("photo", new ByteArrayBody(imageArray, "image/jpeg", "photo.jpg"));

        entity.addPart("n", new StringBody("10"));

        post.setEntity(entity);

        try {
            HttpResponse response = httpClient.execute(post);

            if (response.getEntity().getContentLength() > 0) {
                String json_string = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(json_string);

                Log.d(TAG, "Identify Result: " + json_string);
                Log.d(TAG, "Result Lenght: " + jsonObject.getJSONArray("results").length());

                faceCounts = jsonObject.getJSONArray("results").length();
                if (faceCounts > 0) {
                    faceRecognitionResults = new FaceRecognitionResult[faceCounts];

                    for (int i = 0; i < faceCounts; i++) {
                        faceRecognitionResults[i] = new FaceRecognitionResult();
                        faceRecognitionResults[i].confidence = jsonObject.getJSONArray("results").getJSONObject(i).getString("confidence");
                        faceRecognitionResults[i].thumbnail = jsonObject.getJSONArray("results").getJSONObject(i).getJSONObject("face").getString("thumbnail");
                    }

                    faceRecognitionResults[0].methodName = "identify";
                    return faceRecognitionResults;
                } else {
                    /*
                    faceRecognitionResults = new FaceRecognitionResult[1];
                    faceRecognitionResults[0] = new FaceRecognitionResult();
                    faceRecognitionResults[0].resultCode = "NO_FACES";
                    */
                    return null;
                }

            }
        } catch (IOException e) {
            Log.d(TAG, "Cannot execute post request: " + e.getMessage());
        } catch (JSONException e) {
            Log.d(TAG, "Cannot create json object: " + e.getMessage());
        }
        return null;
    }

}
