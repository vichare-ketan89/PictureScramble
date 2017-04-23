package com.example.keto.picturescramble.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class FlickrResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);

        String responseString = response.body().string();

        //Log.d("Interceptor", responseString);
        responseString = responseString.substring(15,responseString.length()-1);
        ResponseBody responseBody = ResponseBody.create(response.body().contentType(), responseString);

        return response.newBuilder().body(responseBody).build();
    }
}
