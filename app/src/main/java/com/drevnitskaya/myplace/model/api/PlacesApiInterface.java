package com.drevnitskaya.myplace.model.api;

import com.drevnitskaya.myplace.model.entities.PlaceDetailsResponse;
import com.drevnitskaya.myplace.model.entities.PlaceResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by air on 01.07.17.
 */

public interface PlacesApiInterface {

    @GET("nearbysearch/json")
    Observable<PlaceResponse> getNearestPlaces(@Query("location") String location,
                                               @Query("radius") int radius, @Query("key") String key);

    @GET("details/json")
    Observable<PlaceDetailsResponse> getPlaceDetails(@Query("placeid") String placeId, @Query("key") String key);
}
