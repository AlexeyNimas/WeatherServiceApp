package com.skaffman.weatherapp;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ApiTest {
    private final Api api = ApiFactory.createApi();

    @Test
    public void testCurrentWeather() throws IOException {
        Call<CurrentWeather> call = api.getCurrentWeather(
                35,
                139,
                Constants.API_KEY,
                Constants.DEFAULT_UNITS
        );

        Response<CurrentWeather> response = call.execute();
        Assert.assertTrue(response.isSuccessful());
        CurrentWeather body = response.body();
        Assert.assertNotNull(body);
        String cityName = body.getCityName();

        Assert.assertNotNull(cityName);
        Assert.assertTrue(cityName.length() > 0);
    }
}
