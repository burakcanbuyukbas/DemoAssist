package com.burakcanbuyukbas.demoassist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PushDemoLog";
    private Button btnToken;
    private ImageButton btnSpeech;
    private ImageButton btnNews;
    private ImageButton btnTranslate;
    private ImageButton btnWeather;
    private ImageButton btnAboutMe;
    private String pushtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnToken = findViewById(R.id.button_getToken);
        btnToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken();
            }
        });


        btnSpeech = findViewById(R.id.imageButton_Speech2Text);
        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText();
            }
        });

        btnNews = findViewById(R.id.imageButtonNews);
        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNews();
            }
        });

        btnTranslate = findViewById(R.id.imageButtonTranslate);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });

        btnWeather = findViewById(R.id.imageButtonWeather);
        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forecast();
            }
        });

        btnAboutMe = findViewById(R.id.imageButtonAboutMe);
        btnAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCV();
            }
        });
    }

    private void getToken() {
        Log.i(TAG, "get token: begin");

        // get token
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    pushtoken = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(pushtoken)) {
                        Log.i(TAG, "get token:" + pushtoken);
                        showLog(pushtoken);

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("token", pushtoken);
                        clipboard.setPrimaryClip(clip);

                    }
                } catch (Exception e) {
                    Log.i(TAG,"getToken failed, " + e);

                }
            }
        }.start();
    }



    private void showLog(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View tvView = findViewById(R.id.textViewToken);
                if (tvView instanceof TextView) {
                    ((TextView) tvView).setText(log);
                    Toast.makeText(MainActivity.this, pushtoken, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void speechToText(){
        Intent s2tIntent = new Intent(this, SpeechToTextActivity.class);
        startActivity(s2tIntent);
    }

    private void goToNews(){
        Intent newsIntent = new Intent(this, NewsActivity.class);
        startActivity(newsIntent);
    }

    private void translate(){
        Intent translateIntent = new Intent(this, TranslateActivity.class);
        startActivity(translateIntent);
    }

    private void forecast(){
        Intent weatherIntent = new Intent(this, WeatherActivity.class);
        startActivity(weatherIntent);
    }
    private void displayCV(){
        Intent aboutIntent = new Intent(this, AboutMeActivity.class);
        startActivity(aboutIntent);
    }

}


