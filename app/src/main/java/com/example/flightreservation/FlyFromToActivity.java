package com.example.flightreservation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FlyFromToActivity extends AppCompatActivity {
    public final List<String> razred_array = Arrays.asList("prvi", "poslovni", "ekonomski", "Razred");
    public static final ArrayList<String> od_do_array = new ArrayList<>(Arrays.asList("Slovenija: letališče Jožeta Pučnika", "Hrvaška: letališče Franjo Tuđman ", "Italija: letališče Marco Polo", "Madžarska: letališče Ferenc Liszt", "Avstrija: letališče Schwechat"));
    public static final String end_code_addFlight = "addFlight";
    public static final String end_code_changeFlight = "changeFlight";
    public static final Integer min_height = 0;
    public static final Integer max_height = 50;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch povratni_switch;
    public String min_date = null;

    // Declare Variables
    private ListView list1;
    private ListViewAdapter adapter1;
    private SearchView editsearch1;

    private ListView list2;
    private ListViewAdapter adapter2;
    private SearchView editsearch2;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly_from_to);

        HintAdapter hintAdapter = new HintAdapter(this, android.R.layout.simple_list_item_1, razred_array);
        ((Spinner) findViewById(R.id.razred)).setAdapter(hintAdapter);
        // show hint
        ((Spinner) findViewById(R.id.razred)).setSelection(hintAdapter.getCount());

        // Locate the ListView in listview_main.xml
        list1 = (ListView) findViewById(R.id.list1);
        list2 = (ListView) findViewById(R.id.list2);

// Locate the EditText in listview_main.xml
        editsearch1 = (SearchView) findViewById(R.id.od_destination);
        editsearch2 = (SearchView) findViewById(R.id.do_destination);

        // Pass results to ListViewAdapter Class
        adapter1 = new ListViewAdapter(this, od_do_array, editsearch1, editsearch2);
        adapter2 = new ListViewAdapter(this, od_do_array, editsearch2, editsearch1);

        // Binds the Adapter to the ListView
        list1.setAdapter(adapter1);
        list2.setAdapter(adapter2);

        editsearch1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    //list.clearTextFilter();
                    adapter1.getFilter().filter("");
                } else {
                    //list.setFilterText(newText);
                    adapter1.getFilter().filter(newText);
                }
                return true;
            }
        });

        editsearch2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    //list.clearTextFilter();
                    adapter2.getFilter().filter("");
                } else {
                    //list.setFilterText(newText);
                    adapter2.getFilter().filter(newText);
                }
                return true;
            }
        });

        list1.setTextFilterEnabled(true);
        list2.setTextFilterEnabled(true);
        setupSearchView1();
        setupSearchView2();

        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("me", String.valueOf(((RelativeLayout)view).getChildAt(0)));
                //Log.d("me", String.valueOf(position));
                //Toast.makeText(FlyFromToActivity.this, "Working ", Toast.LENGTH_LONG).show();
                editsearch1.setQuery(((TextView) ((RelativeLayout)view).getChildAt(0)).getText().toString(), false);
                editsearch1.clearFocus();
            }
        });

        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("me", String.valueOf(((RelativeLayout)view).getChildAt(0)));
                //Log.d("me", String.valueOf(position));
                //Toast.makeText(FlyFromToActivity.this, "Working ", Toast.LENGTH_LONG).show();
                editsearch2.setQuery(((TextView) ((RelativeLayout)view).getChildAt(0)).getText().toString(), false);
                editsearch2.clearFocus();
            }
        });

        povratni_switch = (Switch) findViewById(R.id.izkljucno_povratni);

        Intent currentIntent = getIntent();
        ViewGroup.LayoutParams params = povratni_switch.getLayoutParams();
        if (currentIntent.getStringExtra("button").equals("Prihod")) {
            setTitle("Povratni let");

            if (currentIntent.hasExtra(end_code_addFlight)) {
                ArrayList<String> temp = currentIntent.getStringArrayListExtra(end_code_addFlight);

                Log.d("me", "Set end_code_addFlight: "+String.valueOf(temp));

                editsearch1.setQuery(temp.get(0), false);
                editsearch2.setQuery(temp.get(1), false);

                min_date = temp.get(2);
                ((Spinner)findViewById(R.id.razred)).setSelection(razred_array.indexOf(temp.get(3)));
            }

            Log.d("me", "listener");
            povratni_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    enableSearchView(editsearch1, !isChecked);
                    enableSearchView(editsearch2, !isChecked);
                    //((EditText)findViewById(R.id.datum)).setEnabled(!isChecked);
                    ((Spinner)findViewById(R.id.razred)).setEnabled(!isChecked);
                }
            });
            povratni_switch.setChecked(true);
            enableSearchView(editsearch1, false);
            enableSearchView(editsearch2, false);
            //((EditText)findViewById(R.id.datum)).setEnabled(false);
            ((Spinner)findViewById(R.id.razred)).setEnabled(false);
        } else {
            povratni_switch.setVisibility(View.INVISIBLE);
            povratni_switch.setEnabled(false);
            params.height = min_height;
            povratni_switch.requestLayout();
        }

        if (currentIntent.hasExtra(end_code_changeFlight)) {
            setTitle("Sprememba destinacije");

            if (currentIntent.getStringExtra("button").equals("Prihod")) {
                setTitle("Sprememba povratnega leta");
            }

            ArrayList<String> temp = currentIntent.getStringArrayListExtra(FlyFromToActivity.end_code_changeFlight);

            Log.d("me", "Set: "+String.valueOf(temp));

            editsearch1.setQuery(temp.get(0), false);
            editsearch2.setQuery(temp.get(1), false);

            ((EditText)findViewById(R.id.datum)).setText(temp.get(2));
            ((Spinner)findViewById(R.id.razred)).setSelection(razred_array.indexOf(temp.get(3)));
        }

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMAN);
        findViewsById();
        setDateTimeField();
    }

    private void enableSearchView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                enableSearchView(child, enabled);
            }
        }
    }

    private void setupSearchView1() {
        editsearch1.setIconifiedByDefault(false);
        //editsearch1.setSubmitButtonEnabled(true);
        editsearch1.setQueryHint("Iskanje");
    }

    private void setupSearchView2() {
        editsearch2.setIconifiedByDefault(false);
        //editsearch2.setSubmitButtonEnabled(true);
        editsearch2.setQueryHint("Iskanje");
    }




    public void confirmFlight(View view) {
        String od_flight = editsearch1.getQuery().toString();
        String do_flight = editsearch2.getQuery().toString();
        String datum = ((EditText)findViewById(R.id.datum)).getText().toString();
        String razred = ((Spinner)findViewById(R.id.razred)).getSelectedItem().toString();

        if (od_flight.equals("")) {
            editsearch1.requestFocus();
            return;
        } else {
            if (!od_do_array.contains(od_flight)) {
                editsearch1.requestFocus();
                editsearch1.setQuery("", false);
                adapter1.getFilter().filter("");
                Toast.makeText(this, "Izberite letališče med možnimi izbirami!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (do_flight.equals("")) {
            editsearch2.requestFocus();
            return;
        } else {
            if (!od_do_array.contains(do_flight)) {
                editsearch2.requestFocus();
                editsearch2.setQuery("", false);
                adapter2.getFilter().filter("");
                Toast.makeText(this, "Izberite letališče med možnimi izbirami!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (datum.equals("")) {
            findViewById(R.id.datum).performClick();
            Toast.makeText(this, "Izberite datum odhoda!", Toast.LENGTH_LONG).show();
            return;
        }

        if (razred.equals("Razred")) {
            findViewById(R.id.razred).performClick();
            Toast.makeText(this, "Izberite razred!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent data= new Intent();


        data.putStringArrayListExtra(end_code_addFlight, new ArrayList<String>(
                Arrays.asList(od_flight, do_flight, datum, razred))
        );

        setResult(RESULT_OK, data);
        finish();
    }

    public void cancelFlight(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }






    //UI References
    private EditText simpleDateEtxt;
    private DatePickerDialog simpleDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    private void findViewsById() {                                          // https://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
        simpleDateEtxt = (EditText) findViewById(R.id.datum);
        simpleDateEtxt.setInputType(InputType.TYPE_NULL);
        simpleDateEtxt.setFocusable(false);
        simpleDateEtxt.setKeyListener(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDateTimeField() {
        //simpleDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        simpleDatePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                simpleDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //following line to restrict future date selection
        long now;
        Log.d("me", ""+min_date);
        if (min_date == null)
            now = System.currentTimeMillis() - 1000;
        else {
            Log.d("me", "applaying min date");
            DateTimeFormatter dateFormatter2 = new DateTimeFormatterBuilder()
                    .appendPattern("dd/MM/yyyy[ HH:mm:ss]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
            LocalDateTime ldt =  LocalDateTime.parse(min_date, dateFormatter2);
            ZonedDateTime zdt = ldt.atZone(ZoneId.of("Europe/Paris"));
            now = zdt.toInstant().toEpochMilli();
        }
        simpleDatePickerDialog.getDatePicker().setMinDate(now);

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