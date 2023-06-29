package com.example.womensafetyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class PanicSettingActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText messageEditText;
    private SeekBar delaySeekBar;
    private Switch recordingSwitch;
    private TextView delayValueText;
    private static final String PREF_NAME = "setting";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_setting);

        // Initialize SharedPreferences with the specified name
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        messageEditText = findViewById(R.id.messageEditText);
        delaySeekBar = findViewById(R.id.delaySeekBar);
        recordingSwitch = findViewById(R.id.recordingSwitch);
        delayValueText = findViewById(R.id.delayValueText);

        // Retrieve stored values from SharedPreferences
        String storedMessage = sharedPreferences.getString("message", "");
        int storedDelay = sharedPreferences.getInt("delay", 0);
        boolean storedRecording = sharedPreferences.getBoolean("recording", false);

        // Update UI with stored values
        messageEditText.setText(storedMessage);
        delaySeekBar.setProgress(storedDelay);
        delayValueText.setText(String.valueOf(storedDelay));
        recordingSwitch.setChecked(storedRecording);

        // Set default message if no message is set
        if (storedMessage.isEmpty()) {
            messageEditText.setText("Hey, your relative is in danger!");
        }

        // Set delay values for the seek bar
        final int[] delayValues = {30, 60, 90, 120};

        // Set maximum progress to match the number of delay values
        delaySeekBar.setMax(delayValues.length - 1);

        // Set listener for delay seek bar
        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update delay value text
                delayValueText.setText(String.valueOf(delayValues[progress]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Store delay value in SharedPreferences
                int progress = seekBar.getProgress();
                int delayValue = delayValues[progress];
                sharedPreferences.edit().putInt("delay", delayValue).apply();
            }
        });

        // Set listener for recording switch
        recordingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Store recording value in SharedPreferences
                sharedPreferences.edit().putBoolean("recording", isChecked).apply();
            }
        });

        // Set listener for messageEditText
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the message in SharedPreferences
                String message = s.toString();
                sharedPreferences.edit().putString("message", message).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }
}
