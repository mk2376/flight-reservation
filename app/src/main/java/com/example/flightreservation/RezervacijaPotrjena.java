package com.example.flightreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class RezervacijaPotrjena extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezervacija_potrjena);
    }

    public void novaRezervacija(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void izhod(View view) {
        finish();
        moveTaskToBack(true);
        System.exit(1);
    }

}