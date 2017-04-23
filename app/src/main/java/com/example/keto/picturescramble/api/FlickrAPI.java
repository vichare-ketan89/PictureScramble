package com.example.keto.picturescramble.api;

import com.example.keto.picturescramble.model.FlickrModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 */

public interface FlickrAPI {

    @GET("photos_public.gne")
    Call<FlickrModel> getImagesList(@Query("format") String format);
}
