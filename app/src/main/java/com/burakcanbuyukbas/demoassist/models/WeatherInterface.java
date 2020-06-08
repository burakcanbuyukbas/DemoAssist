package com.burakcanbuyukbas.demoassist.models;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherInterface {
    @GET("data/2.5/weather")
    Call<WeatherReport> getCurrentWeatherData(@Query("q") String query, @Query("appid") String app_id);
}
