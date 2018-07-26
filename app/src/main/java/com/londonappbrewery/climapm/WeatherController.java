package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {


    // Constants:
    final Integer REQUEST_CODE = 1200;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    // App ID to use OpenWeather data
    final String APP_ID = "6ecaf74dcec3ad6a714d34e403cd4cff";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);

        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeCityIntent = new Intent(WeatherController.this, ChangeCityController.class);
                finish();
                startActivity(changeCityIntent);

            }
        });
    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CLIMA", "ONRESUME CALLED");

        Intent cityNameIntent = getIntent();

        if(cityNameIntent.hasExtra("CITYNAME")){
            String cityName = cityNameIntent.getStringExtra("CITYNAME");
            getWeatherForNewCity(cityName);
        }else {
            getWeatherForCurrentLocation();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city){

        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);

        letsDoSomeNetworking(params);
    }

    public  void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){

            @Override
            public  void  onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("CLIMA", "Success response "+response);
                WeatherDataModel wDataModel = WeatherDataModel.fromJson(response);
                updateUI(wDataModel);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.d("CLIMA" , "Failed :"+e.getLocalizedMessage());
                Log.d("CLIMA", "statusCode "+statusCode);
                Toast.makeText(WeatherController.this, "ERROR", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("CLIMA", "onRequestPermissionsResult"+requestCode);
        if(requestCode == REQUEST_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("CLIMA", "onRequestPermissionsResult permission granted");
            } else {
                Log.d("CLIMA", "onRequestPermissionsResult permission NOT granted");
            }
        }
    }

    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        Log.d("CLIMA", "getWeatherForCurrentLocation");

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new ClimaLocationListener(this) ;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION },REQUEST_CODE);
            Toast.makeText(getApplicationContext(), "Not Enough Permission", Toast.LENGTH_LONG).show();

            Log.d("CLIMA", "TRUE");

            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                10000, 0, mLocationListener);
    }

    // TODO: Add updateUI() here:
        public void updateUI(WeatherDataModel weatherDataModel){
            mTemperatureLabel.setText(weatherDataModel.getmTemperature());
            mCityLabel.setText(weatherDataModel.getmCity());

            int imageResourceId = getResources().getIdentifier(weatherDataModel.getmIconName(),
                                                                        "drawable",getPackageName());

            mWeatherImage.setImageResource(imageResourceId);
        }


    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();

        if(mLocationManager != null)
            mLocationManager.removeUpdates(mLocationListener);
    }
}
