package com.example.flightreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;

public class AddUserActivity extends AppCompatActivity {
    public final List<String> spol_array = Arrays.asList("moški", "ženska", "otrok", "drugo", "Spol");
    public static final String end_code_addUser = "addUser";
    public static final String end_code_changeUser = "changeUser";
    public static final Integer min_height = 0;
    public static final Integer max_height = 660;
    String alergije_string = "";
    String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ((EditText) findViewById(R.id.telefonska)).addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMAN);
        findViewsById();
        setDateTimeField();

        HintAdapter hintAdapter = new HintAdapter(this, android.R.layout.simple_list_item_1, spol_array);
        ((Spinner) findViewById(R.id.spol)).setAdapter(hintAdapter);
        // show hint
        ((Spinner) findViewById(R.id.spol)).setSelection(hintAdapter.getCount());

        final ListView list = findViewById(R.id.alergije_list);
        list.setEnabled(false);
        list.setVisibility(View.INVISIBLE);

        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = min_height;
        list.requestLayout();     // https://stackoverflow.com/questions/2963152/how-to-resize-a-custom-view-programmatically

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setItemsCanFocus(false);
        ((Switch) findViewById(R.id.alergije)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.setEnabled(isChecked);
                list.setVisibility((isChecked ? View.VISIBLE: View.INVISIBLE));
                if (isChecked)
                    params.height = max_height;
                else
                    params.height = min_height;
                list.requestLayout();
            }
        });

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("jajca");
        arrayList.add("mleko");
        arrayList.add("pšenica");
        arrayList.add("gluten");
        arrayList.add("oreški");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, arrayList);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray sp = null;
                sp = list.getCheckedItemPositions();

                alergije_string = "";
                for (int i=0; i<sp.size(); i++) {
                    if(sp.valueAt(i) == true)
                        alergije_string += arrayList.get(sp.keyAt(i))+", ";
                }

                alergije_string = alergije_string.replaceAll(", $", "");
                //Toast.makeText(AddUserActivity.this, alergije_string, Toast.LENGTH_SHORT).show();
            }
        });

        // if we are changing a user
        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra(end_code_changeUser)) {
            setTitle("Popravi potnika");
            ArrayList<String> temp = currentIntent.getStringArrayListExtra(AddUserActivity.end_code_changeUser);

            Log.d("me", String.valueOf(temp));
            position = temp.get(0);
            Potnik potnik = new Potnik(temp.get(1), temp.get(2), temp.get(3), temp.get(4), temp.get(5), temp.get(6));

            ((Spinner)findViewById(R.id.spol)).setSelection(spol_array.indexOf(potnik.spol));
            ((EditText)findViewById(R.id.ime)).setText(potnik.ime);
            ((EditText)findViewById(R.id.priimek)).setText(potnik.priimek);
            ((EditText)findViewById(R.id.telefonska)).setText(potnik.telefonska);
            ((EditText)findViewById(R.id.datum_rojstva)).setText(potnik.datum_rojstva);

            alergije_string = potnik.alergije;
            if (!potnik.alergije.equals("")) {
                ((Switch) findViewById(R.id.alergije)).setChecked(true);
                list.setVisibility(View.VISIBLE);
                list.setEnabled(true);
                params.height = max_height;
                list.requestLayout();

                if (potnik.alergije.contains(", ")) {
                    String[] temp2 = potnik.alergije.split(", ");
                    Log.d("me", String.valueOf(temp2));
                    for (String alergija: temp2)
                        list.setItemChecked(arrayList.indexOf(alergija), true);
                } else {
                    list.setItemChecked(arrayList.indexOf(potnik.alergije), true);
                }
            }
        }
    }


    public void confirmUser(View view) {
        String spol = ((Spinner)findViewById(R.id.spol)).getSelectedItem().toString();
        String ime = ((EditText)findViewById(R.id.ime)).getText().toString();
        String priimek = ((EditText)findViewById(R.id.priimek)).getText().toString();
        String telefonska = ((EditText)findViewById(R.id.telefonska)).getText().toString();
        String datum_rojstva = ((EditText)findViewById(R.id.datum_rojstva)).getText().toString();

        if (ime.equals("")) {
            findViewById(R.id.ime).requestFocus();
            return;
        }

        if (priimek.equals("")) {
            findViewById(R.id.priimek).requestFocus();
            return;
        }

        if (!telefonska.equals("")) {
            if (telefonska.length() > 21) {
                findViewById(R.id.telefonska).requestFocus();
                Toast.makeText(this, "Telefonska številka ni veljavna!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (datum_rojstva.equals("")) {
            findViewById(R.id.datum_rojstva).performClick();
            Toast.makeText(this, "Izberite datum rojstva!", Toast.LENGTH_LONG).show();
            return;
        }

        if (findViewById(R.id.alergije).isActivated()) {
            // Get items from recycle field, already implemented ^ at top
        }

        if (spol.equals("Spol")) {
            findViewById(R.id.spol).performClick();
            Toast.makeText(this, "Izberite spol!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent data= new Intent();

        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra(end_code_changeUser)) {
            data.putStringArrayListExtra(end_code_changeUser, new ArrayList<String>(
                    Arrays.asList(position, spol, ime, priimek, telefonska, datum_rojstva, alergije_string))
            );
        } else {
            data.putStringArrayListExtra(end_code_addUser, new ArrayList<String>(
                    Arrays.asList(spol, ime, priimek, telefonska, datum_rojstva, alergije_string))
            );
        }
        setResult(RESULT_OK, data);
        finish();
    }

    public void cancelUser(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }



    //UI References
    private EditText simpleDateEtxt;
    private DatePickerDialog simpleDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    private void findViewsById() {                                          // https://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
        simpleDateEtxt = (EditText) findViewById(R.id.datum_rojstva);
        simpleDateEtxt.setInputType(InputType.TYPE_NULL);
        simpleDateEtxt.setFocusable(false);
        simpleDateEtxt.setKeyListener(null);
    }

    private void setDateTimeField() {
        //simpleDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        simpleDatePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                simpleDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //following line to restrict future date selection
        long now = System.currentTimeMillis() - 1000;
        simpleDatePickerDialog.getDatePicker().setMaxDate(now);

        simpleDatePickerDialog.getDatePicker().setSpinnersShown(true);              // https://stackoverflow.com/questions/34723495/how-to-set-datepickermode-spinner-programmatically
        simpleDatePickerDialog.getDatePicker().setCalendarViewShown(false);
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    } */

    public void onClickDate(View view) {
        hideKeyboard((EditText) view);
        simpleDatePickerDialog.show();
    }

    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
}