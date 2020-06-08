package com.burakcanbuyukbas.demoassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.burakcanbuyukbas.demoassist.models.WeatherInterface;
import com.burakcanbuyukbas.demoassist.models.WeatherReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WeatherActivity extends AppCompatActivity {

    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "e2583eeca787e02f781922ded5eb0fcf";
    public static String city = "warsaw";

    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        cityField = findViewById(R.id.city_field);
        updatedField = findViewById(R.id.updated_field);
        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        weatherIcon = findViewById(R.id.weather_icon);
        //weatherIcon.setTypeface(weatherFont);

        weatherFont = ResourcesCompat.getFont(this, R.font.weather);
        weatherIcon.setTypeface(weatherFont);

        getCurrentData();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
    void getCurrentData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherInterface service = retrofit.create(WeatherInterface.class);
        Call<WeatherReport> call = service.getCurrentWeatherData(city, AppId);
        call.enqueue(new Callback<WeatherReport>() {
            @Override
            public void onResponse(@NonNull Call<WeatherReport> call, @NonNull Response<WeatherReport> response) {
                if (response.code() == 200) {
                    WeatherReport weatherResponse = response.body();
                    assert weatherResponse != null;


                    cityField.setText("Warsaw - PL");


                    detailsField.setText(weatherResponse.weather.get(0).description + "\n" + "Humidity: " + weatherResponse.main.humidity + "%" +
                            "\n" + "Pressure: " + weatherResponse.main.pressure + " hPa");


                    currentTemperatureField.setText(String.format("%.2f", (double)weatherResponse.main.temp- 273.15)  + " ℃");

                    DateFormat df = DateFormat.getDateTimeInstance();
                    String updatedOn = df.format(new Date((long)weatherResponse.dt*1000));
                    updatedField.setText("Last update: " + updatedOn);

                    setWeatherIcon(weatherResponse.weather.get(0).id,
                            (long)weatherResponse.sys.sunrise* 1000,
                            (long)weatherResponse.sys.sunset* 1000);


                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherReport> call, @NonNull Throwable t) {
                detailsField.setText(t.getMessage());
            }
        });


    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = this.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = this.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = this.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = this.getString(R.string.weather_rainy);
            }
        }
        weatherIcon.setText(icon);
    }
}