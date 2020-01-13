package com.example.timerrotationn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private int seconds = 0;
    private boolean running;
    private double volume = 0;
    private final double diameter = 0.2;
    private final double densityAir = 1.2;
    private final double densityAlcohol = 780;
    private final double phi = 0.8;
    private final double standardGravity = 9.81;


    private boolean wasRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pobranie wartosci z parametru savedInstanceState, jezeli takowy istnieje
        if (savedInstanceState != null){

            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            volume = savedInstanceState.getDouble("volume");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        runTimer();
    }


    public void onClickStart(View view) {


        final Button stopBtn = (Button) findViewById(R.id.stop_button);
        final Button resetBtn = (Button) findViewById(R.id.reset_button);
        final EditText hValue = (EditText) findViewById(R.id.hValue);

        if(TextUtils.isEmpty(hValue.getText().toString())) {
            Toast.makeText(this, "Please enter the value",
                    Toast.LENGTH_SHORT).show();
            stopBtn.setEnabled(false);

        }else {
            running = true;
            stopBtn.setEnabled(true);
            resetBtn.setEnabled(true);
        }
    }

    public void onClickStop(View view) {

        final Button stopBtn = (Button) findViewById(R.id.stop_button);
        running = false;
        if (seconds > 0)
        seconds--;
        stopBtn.setEnabled(false);
    }

    public void onClickReset(View view) {

        final Button resetBtn = (Button) findViewById(R.id.reset_button);
        final Button stopBtn = (Button) findViewById(R.id.stop_button);
        running = false;
        seconds = 0;
        resetBtn.setEnabled(false);
        stopBtn.setEnabled(false);

        final TextView volumeValue = (TextView) findViewById(R.id.volumeValue);
        volumeValue.setText("0.0m^3");
    }

    // Przesloniona metoda onSaveInstanceState zapisuje stan wartosci pol seconds, running i wasRunning, dzieki czemu nie wyzerowuje
    // sie czas przy obrocie telefonu lub wyjscia z aplikacji (ale nie jej wylaczenia)
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putDouble("volume", volume);

        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    // Przeslonione metody onStop i onStart dzieki polu wasRunning powoduja, ze stoper zatrzymuje sie, gdy aplikacja traci focus (wychodzimy z niej, ale nie zamykamy)
    // i wznawia, gdy aplikacja wroci na pierwszy plan
    @Override
    protected void onStop() {

        super.onStop();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (wasRunning) {
            running = true;
        }
    }

    private void runTimer() {

        final TextView timeView = (TextView) findViewById(R.id.time_view);
        final EditText hValue = (EditText) findViewById(R.id.hValue);


            final Handler handler = new Handler();

            handler.post(new Runnable() {
                @Override
                public void run() {

                    int hours = seconds/36000;
                    int minutes = (seconds%36000)/600;
                    int secs = (seconds%600)/10;
                    int milisecs = seconds%10;


                    String time = String.format(Locale.getDefault(), "%d:%02d:%02d:%d",hours,minutes,secs,milisecs);
                    timeView.setText(time);



                    if(running){
                        if(TextUtils.isEmpty(hValue.getText().toString())){
                            seconds = 0;
                            volumeUpdater();
                        }
                        else {
                            volumeUpdater();
                            seconds++;
                        }
                    }

                    handler.postDelayed(this,100);

                }
            });

    }

    private void volumeUpdater(){

        final TextView volumeValue = (TextView) findViewById(R.id.volumeValue);
        final EditText hValue = (EditText) findViewById(R.id.hValue);

        if(TextUtils.isEmpty(hValue.getText().toString()) == false){

            double h = Double.parseDouble(hValue.getText().toString());
            double volume = (phi*Math.PI*Math.pow(diameter,2))/4.*Math.sqrt((2.*standardGravity*h*(densityAlcohol-densityAir))/densityAir);

            double volumeInTime = (volume*seconds)/10.;
            BigDecimal bd = new BigDecimal(volumeInTime).setScale(3, RoundingMode.HALF_UP);
            double newVolumeInTime = bd.doubleValue();
            String newVolumeInTimeString = newVolumeInTime + "m^3";

            volumeValue.setText(newVolumeInTimeString);
        }

    }
}
