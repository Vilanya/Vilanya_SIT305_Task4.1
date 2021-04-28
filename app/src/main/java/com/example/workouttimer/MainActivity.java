package com.example.workouttimer;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Chronometer chronometer;
    boolean isRunning;
    long pausedTime;

    TextView workoutSummary;
    EditText workoutType;
    SharedPreferences sharedPreferences;

    String PREV_SUMMARY = "PREV_SUMMARY";
    String SUMMARY = "SUMMARY";
    String PAUSE = "PAUSE";
    String BASE = "BASE";
    String TIMER_RUNNING = "TIMER_RUNNING";


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (!isRunning) {
                    chronometer.setBase(SystemClock.elapsedRealtime() - pausedTime);
                    chronometer.start();
                    isRunning = true;
                }
                break;
            case R.id.pause:
                if (isRunning) {
                    chronometer.stop();
                    pausedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                    isRunning = false;
                }
                break;
            case R.id.stop:
                CharSequence time = chronometer.getText();
                if (workoutType.getText().toString().equals("")) {
                    workoutSummary.setText("You spent " + time.toString() + " on your workout last time.");
                }
                else {
                    workoutSummary.setText("You spent " + time.toString() + " on " + workoutType.getText().toString() + " last time.");
                }
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                pausedTime = 0;
                isRunning = false;


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREV_SUMMARY, workoutSummary.getText().toString());
                editor.apply();
                break;

        }
    }

        private void loadSharedPreferences() {
            String text = sharedPreferences.getString(PREV_SUMMARY, "prev_summary");
            workoutSummary.setText(text);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            chronometer = (Chronometer)findViewById(R.id.timerChronometer);
            workoutSummary = findViewById(R.id.summary);
            workoutType = findViewById(R.id.inputWorkout);
            pausedTime = SystemClock.elapsedRealtime() - chronometer.getBase();

            sharedPreferences = getSharedPreferences("com.example.workouttimer", MODE_PRIVATE);
            loadSharedPreferences();

            if (savedInstanceState != null) {
                workoutSummary.setText(savedInstanceState.getString(SUMMARY));
                pausedTime = savedInstanceState.getLong(PAUSE);
                chronometer.setBase(SystemClock.elapsedRealtime() + savedInstanceState.getLong(BASE));
                if (savedInstanceState.getBoolean(TIMER_RUNNING)) {
                    chronometer.start();
                    isRunning = true;
                }
                else {
                    chronometer.setBase(SystemClock.elapsedRealtime() - pausedTime);
                }
            }
            else {
                isRunning = false;
            }
        }

        @Override
        protected void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(SUMMARY, workoutSummary.getText().toString());
            outState.putLong(BASE, chronometer.getBase() - SystemClock.elapsedRealtime());
            outState.putBoolean(TIMER_RUNNING, isRunning);
            outState.putLong(PAUSE, pausedTime);
        }


}
