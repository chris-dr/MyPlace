package com.drevnitskaya.myplace.model.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by air on 01.07.17.
 */

public class PlacesApiRequest {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    private static PlacesApiRequest kooperApiRequest;
    private PlacesApiInterface api;

    public static PlacesApiRequest getInstance() {
        if (kooperApiRequest == null) {
            kooperApiRequest = new PlacesApiRequest();
        }
        return kooperApiRequest;
    }

    private PlacesApiRequest() {
        Retrofit retrofit = getRetrofit();
        api = retrofit.create(PlacesApiInterface.class);
    }


    public PlacesApiInterface getApi() {
        return api;
    }


    private Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder().addInterceptor(interceptor);
        client.readTimeout(5 * 60, TimeUnit.SECONDS);
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.header("Content-Type", "application/json");
                Request request = requestBuilder
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .client(client.build())
                .build();

        return retrofit;
    }
}
