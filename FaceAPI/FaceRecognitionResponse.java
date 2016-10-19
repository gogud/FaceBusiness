package com.example.mg_win.facebusiness.FaceAPI;

/**
 * Created by mg-Win on 16.08.2016.
 */
public interface FaceRecognitionResponse {
    void processFaceRecognition(FaceRecognition.FaceRecognitionResult[] results);
    void nullDataReturned();
}

