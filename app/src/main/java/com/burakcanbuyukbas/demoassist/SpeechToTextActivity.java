package com.burakcanbuyukbas.demoassist;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;

import androidx.appcompat.app.AppCompatActivity;

public class SpeechToTextActivity extends AppCompatActivity {
    private Button btnCopy;
    private ImageButton btnSpeech;
    private TextView textView;
    private String text = "";
    private static final int ML_ASR_CAPTURE_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);



        btnCopy = findViewById(R.id.buttonCopy);
        btnSpeech = findViewById(R.id.imageButtonMic);
        textView = findViewById(R.id.textView);
        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startASR(v);
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyText(v);
            }
        });

    }

    private void startASR(View view){
        Intent intent = new Intent(this, MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, "en-US")
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
        startActivityForResult(intent, ML_ASR_CAPTURE_CODE);
        overridePendingTransition(R.anim.mlkit_asr_popup_slide_show, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ML_ASR_CAPTURE_CODE) {
            switch (resultCode) {
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                            text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                        }
                        if (text != null && !"".equals(text)) {
                            textView.setText(text);
                        }
                    }
                    break;
                case MLAsrCaptureConstants.ASR_FAILURE:
                    if (data != null) {
                        int errorCode;
                        Integer subErrorCode;
                        String message = "";

                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            message = message + errorCode;
                        }
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                            message = message + subErrorCode;
                        }

                        Toast toast = Toast.makeText(SpeechToTextActivity.this, "ERROR:" + message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    break;
                default:
                    Toast toast = Toast.makeText(SpeechToTextActivity.this, "ERROR...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
            }
        }
    }

    private void copyText(View view){
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", textView.getText());
            clipboard.setPrimaryClip(clip);
    }


}