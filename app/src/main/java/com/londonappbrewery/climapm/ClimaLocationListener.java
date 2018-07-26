package com.londonappbrewery.climapm;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ClimaLocationListener implements LocationListener {

    final String APP_ID = "6ecaf74dcec3ad6a714d34e403cd4cff";
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    WeatherController weatherController;

    public ClimaLocationListener(WeatherController appContext){
        this.weatherController = appContext;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("CLIMA", location.toString());
        String longitude = String.valueOf(location.getLongitude());
        String latitude = String.valueOf(location.getLatitude());

        Log.d("CLIMA", longitude +"_"+ latitude);

        RequestParams params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon",longitude);
        params.put("appid",APP_ID);
        letsDoSomeNetworking(params);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("CLIMA", provider);
    }

    public  void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){

            @Override
            public  void  onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("CLIMA", "Success response "+response);
                WeatherDataModel wDataModel = WeatherDataModel.fromJson(response);
                weatherController.updateUI(wDataModel);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.d("CLIMA" , "Failed :"+e.getLocalizedMessage());
                Log.d("CLIMA", "statusCode "+statusCode);
                Toast.makeText(weatherController.getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
            }

        });
    }
}