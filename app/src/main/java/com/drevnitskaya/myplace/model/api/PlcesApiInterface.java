package com.drevnitskaya.myplace.model.api;

import com.drevnitskaya.myplace.model.entities.PlaceResponce;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by air on 01.07.17.
 */

public interface PlcesApiInterface {

    @GET("nearbysearch/json")
    Observable<PlaceResponce> getNearestPlaces(@Query("location") String location, @Query("radius") int radius, @Query("key") String key);
}
