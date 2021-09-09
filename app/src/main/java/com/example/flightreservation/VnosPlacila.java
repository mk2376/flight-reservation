package com.example.flightreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class VnosPlacila extends AppCompatActivity {
    public static final String end_code_addPay = "addPay";
    public static final String end_code_changePay = "changePay";
    private EditText ime;
    private EditText priimek;
    private EditText kartica;
    private EditText ccv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnos_placila);

        setTitle("Zavarovan bančni terminal");

        ime = findViewById(R.id.imeImetnikaKartice);
        priimek = findViewById(R.id.piimekImetnikaKartice);
        kartica = findViewById(R.id.st_kartice);
        ccv = findViewById(R.id.ccv);

        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra(end_code_changePay)) {
            setTitle("Popravljanje vnosa plačila");
            ArrayList<String> temp = currentIntent.getStringArrayListExtra(end_code_changePay);

            Log.d("me", String.valueOf(temp));

            ime.setText(temp.get(0));
            priimek.setText(temp.get(1));
        }
    }

    private Boolean isInvalid(String text, String text2) {
        boolean kartica = ! text.matches("\\d{4}[\\- ]?\\d{4}[\\- ]?\\d{4}[\\- ]?\\d{4}[\\- ]?");
        boolean ccv = ! text2.matches("\\d{3}");
        //System.out.println("kartica: "+ kartica);
        //System.out.println("ccv: "+ ccv);

        boolean together = !(!kartica && !ccv);
        //System.out.println("together: "+together);

        return together;
    }


    public void confirmPay(View view) {
        if (ime.getText().toString().equals("")) {
            ime.requestFocus();
            Toast.makeText(this, "Polje ne sme biti prazno!", Toast.LENGTH_LONG).show();
            return;
        }

        if (priimek.getText().toString().equals("")) {
            priimek.requestFocus();
            Toast.makeText(this, "Polje ne sme biti prazno!", Toast.LENGTH_LONG).show();
            return;
        }

        if (isInvalid(kartica.getText().toString(), ccv.getText().toString())) {
            Toast.makeText(this, "Plačilo ni sprejeto, preverite št. kartice in ccv kodo", Toast.LENGTH_LONG).show();
            return;
        }

        Intent data= new Intent();


        data.putStringArrayListExtra(end_code_addPay, new ArrayList<String>(
                Arrays.asList(ime.getText().toString(), priimek.getText().toString(), kartica.getText().toString(), ccv.getText().toString()))
        );

        setResult(RESULT_OK, data);
        finish();
    }

    public void cancelPay(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}