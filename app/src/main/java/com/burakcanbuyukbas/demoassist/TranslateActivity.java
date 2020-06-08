package com.burakcanbuyukbas.demoassist;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.langdetect.MLDetectedLang;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetector;
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetectorSetting;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class TranslateActivity extends AppCompatActivity {
    private static final String TAG = "TranslatorActivity";
    private static final String[] SOURCE_LANGUAGE_CODE = new String[]{"Auto",
            "ZH", "EN", "FR", "TH", "JA", "DE", "RU", "ES",
            "AR", "TR", "PT", "IT"};
    private static final String[] DEST_LANGUAGE_CODE = new String[]{
            "ZH", "EN", "FR", "TH", "JA", "DE", "RU", "ES",
            "AR", "TR", "PT", "IT"};
    private static final List<String> SP_SOURCE_LIST_EN = new ArrayList<>(Arrays.asList("Auto",
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian"));
    private static final List<String> SP_DEST_LIST_EN = new ArrayList<>(Arrays.asList(
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian"));
    private static final List<String> CODE_LIST = new ArrayList<>(Arrays.asList(
            "zh", "en", "fr", "th", "ja", "de", "ru", "es",
            "ar", "tr", "pt", "it", "ro"));
    private static final List<String> LANGUAGE_LIST = new ArrayList<>(Arrays.asList(
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian", "Romanian"));

    private Spinner spSourceType;
    private Spinner spDestType;
    private EditText etInputString;
    private TextView tvOutputString;
    private Button btrTranslator;
    private Button btrIdentification;
    private TextView tvInputLen;
    private TextView tvOutputLen;

    private String srcLanguage = "Auto";
    private String dstLanguage = "EN";
    public static final String EN = "en";

    private View.OnClickListener listener;

    private ArrayAdapter<String> spSourceAdapter;
    private ArrayAdapter<String> spDestAdapter;

    private MLRemoteTranslateSetting mlRemoteTranslateSetting;
    private MLRemoteTranslator mlRemoteTranslator;
    private MLRemoteLangDetectorSetting mlRemoteLangDetectorSetting;
    private MLRemoteLangDetector mlRemoteLangDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_translate);


        this.createComponent();
        this.createSpinner();
        this.bindEventListener();
    }

    private void createSpinner() {
        this.spSourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, TranslateActivity.SP_SOURCE_LIST_EN);
        this.spDestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, TranslateActivity.SP_DEST_LIST_EN);


        this.spSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spSourceType.setAdapter(this.spSourceAdapter);

        this.spDestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spDestType.setAdapter(this.spDestAdapter);

        this.spSourceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TranslateActivity.this.srcLanguage = TranslateActivity.SOURCE_LANGUAGE_CODE[position];
                Log.i(TranslateActivity.TAG, "srcLanguage: " + TranslateActivity.this.srcLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        this.spDestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TranslateActivity.this.dstLanguage = TranslateActivity.DEST_LANGUAGE_CODE[position];
                Log.i(TranslateActivity.TAG, "dstLanguage: " + TranslateActivity.this.dstLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateSourceLanguage(String code) {
        int count = this.spSourceAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (this.getLanguageName(code).equalsIgnoreCase(this.spSourceAdapter.getItem(i))) {
                this.spSourceType.setSelection(i, true);
                return;
            }
        }
        this.spSourceType.setSelection(0, true);
    }

    private void createComponent() {
        this.etInputString = this.findViewById(R.id.et_input);
        this.tvOutputString = this.findViewById(R.id.tv_output);
        this.btrTranslator = this.findViewById(R.id.btn_translator);
        this.btrIdentification = this.findViewById(R.id.btn_identification);
        this.tvInputLen = this.findViewById(R.id.tv_src_len);
        this.tvOutputLen = this.findViewById(R.id.tv_dst_len);
        this.spSourceType = this.findViewById(R.id.spSourceType);
        this.spDestType = this.findViewById(R.id.spDestType);
        this.updateLength(this.tvInputLen, this.etInputString.getText().length());
    }

    private void bindEventListener() {
        this.etInputString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                TranslateActivity.this.updateLength(TranslateActivity.this.tvInputLen, str.length());
                TranslateActivity.this.autoUpdateSourceLanguage();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        this.listener = new MyListener();
        this.btrTranslator.setOnClickListener(this.listener);
        this.btrIdentification.setOnClickListener(this.listener);
    }


    private void updateLength(TextView view, int length) {
        view.setText(String.format(Locale.ENGLISH, "%d words", length));
    }

    /**
     * Update output text content.
     *
     * @param text Source text.
     */
    private void updateOutputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(TranslateActivity.TAG, "updateOutputText: text is empty");
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TranslateActivity.this.tvOutputString.setText(text);
                TranslateActivity.this.updateLength(TranslateActivity.this.tvOutputLen, text.length());
            }
        });
    }

    private String getInputText() {
        return this.etInputString.getText().toString();
    }

    private String getSourceType() {
        return this.srcLanguage;
    }

    private String getDestType() {
        return this.dstLanguage;
    }

    public class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_translator:
                    TranslateActivity.this.doTranslate();
                    break;
                case R.id.btn_identification:
                    TranslateActivity.this.doLanguageRecognition();
                    break;
                default:
                    break;
            }
        }
    }

    private void doTranslate() {
        // Translating, get data, and update output boxes.
        String sourceText = this.getInputText();
        String sourceLang = this.getSourceType();
        String targetLang = this.getDestType();

        this.mlRemoteTranslateSetting = new MLRemoteTranslateSetting.Factory()
                .setSourceLangCode(sourceLang)
                .setTargetLangCode(targetLang)
                .create();
        this.mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(this.mlRemoteTranslateSetting);
        Task<String> task = this.mlRemoteTranslator.asyncTranslate(sourceText);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                TranslateActivity.this.updateOutputText(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                TranslateActivity.this.updateOutputText(e.getMessage());
            }
        });

        this.autoUpdateSourceLanguage();
    }

    private void autoUpdateSourceLanguage() {
        this.mlRemoteLangDetectorSetting = new MLRemoteLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        this.mlRemoteLangDetector = MLLangDetectorFactory.getInstance().getRemoteLangDetector(this.mlRemoteLangDetectorSetting);
        Task<List<MLDetectedLang>> probabilityDetectTask = this.mlRemoteLangDetector.probabilityDetect(this.getInputText());
        probabilityDetectTask.addOnSuccessListener(new OnSuccessListener<List<MLDetectedLang>>() {
            @Override
            public void onSuccess(List<MLDetectedLang> result) {
                MLDetectedLang recognizedLang = result.get(0);
                String langCode = recognizedLang.getLangCode();
                TranslateActivity.this.updateSourceLanguage(langCode);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private void doLanguageRecognition() {
        this.mlRemoteLangDetectorSetting = new MLRemoteLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        this.mlRemoteLangDetector = MLLangDetectorFactory.getInstance().getRemoteLangDetector(this.mlRemoteLangDetectorSetting);
        Task<List<MLDetectedLang>> probabilityDetectTask = this.mlRemoteLangDetector.probabilityDetect(this.getInputText());
        probabilityDetectTask.addOnSuccessListener(new OnSuccessListener<List<MLDetectedLang>>() {
            @Override
            public void onSuccess(List<MLDetectedLang> result) {
                StringBuilder sb = new StringBuilder();
                for (MLDetectedLang recognizedLang : result) {
                    String langCode = recognizedLang.getLangCode();
                    float probability = recognizedLang.getProbability();
                    sb.append("Language=" + TranslateActivity.this.getEnLanguageName(langCode) + "(" + langCode + "), score=" + probability);
                    sb.append(".");
                }
                TranslateActivity.this.updateOutputText(sb.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                TranslateActivity.this.updateOutputText(e.getMessage());
            }
        });
        this.mlRemoteLangDetector.stop();
    }

    private String getLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < TranslateActivity.SOURCE_LANGUAGE_CODE.length; i++) {
            if (code.equalsIgnoreCase(TranslateActivity.SOURCE_LANGUAGE_CODE[i])) {
                index = i;
                break;
            }
        }
        return this.spSourceAdapter.getItem(index);
    }

    private String getEnLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < TranslateActivity.CODE_LIST.size(); i++) {
            if (code.equalsIgnoreCase(TranslateActivity.CODE_LIST.get(i))) {
                index = i;
                return TranslateActivity.LANGUAGE_LIST.get(index);
            }
        }
        return code;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mlRemoteTranslator != null) {
            this.mlRemoteTranslator.stop();
        }
        if (this.mlRemoteLangDetector != null) {
            this.mlRemoteLangDetector.stop();
        }
    }
}