package com.example.zotee.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zotee.R;

public class VibratorNotification extends AppCompatActivity {
    String message;
    TextView displayText;
    Button btnStopVibrator;
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.zotee.R.layout.activity_vibrator_notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(100000);
        }

        displayText = findViewById(R.id.txtDisplay);
        btnStopVibrator = findViewById(R.id.btnTurnOff);

        displayText.setText(message);

        btnStopVibrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.cancel();
                finish();
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("Message");
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        vibrator.cancel();  // cancel for example here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vibrator.cancel();   // or cancel here
    }
}