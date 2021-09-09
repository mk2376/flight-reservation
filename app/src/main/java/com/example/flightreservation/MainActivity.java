package com.example.flightreservation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    public static final int request_addUser_Code = 12;
    public static final int request_changeUser_Code = 13;
    public static final int request_leavePlane_Code = 14;
    public static final int request_returnPlane_Code = 15;
    public static final int request_cardPay_Code = 16;
    public static final int request_cardChange_Code = 17;

    Data data = new Data();
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.potniki);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, data);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);
    }

    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Izbira naslednjega koraka");
        alertDialogBuilder.setMessage("Kaj želite narediti z "+adapter.getItem(position).ime+" "+adapter.getItem(position).priimek+"?");
        alertDialogBuilder.setPositiveButton("Popravi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);

                Potnik person = adapter.getItem(position);

                intent.putStringArrayListExtra(AddUserActivity.end_code_changeUser,  new ArrayList<String>(
                        Arrays.asList(String.valueOf(position), person.spol, person.ime, person.priimek, person.telefonska, person.datum_rojstva, person.alergije))
                );
                startActivityForResult(intent, request_changeUser_Code);
            }
        });

        alertDialogBuilder.setNegativeButton("Zbriši", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Potnik "+adapter.getItem(position).ime+" "+adapter.getItem(position).priimek+"  je izbrisan.", Toast.LENGTH_LONG).show();
                data.potniki.remove(position);
                adapter.notifyDataSetChanged();
                koncnaCenaUpdate();
            }
        });

        alertDialogBuilder.setNeutralButton("Prekliči", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this,"You clicked cancel button",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void placaj(View view) {
        Intent intent = new Intent(this, VnosPlacila.class);

        if (data.placilo.ime != null) {
            intent.putStringArrayListExtra(VnosPlacila.end_code_changePay, new ArrayList<String>(
                    Arrays.asList(data.placilo.ime, data.placilo.priimek))
            );
        }

        startActivityForResult(intent, request_cardPay_Code);
    }

    public void addUser(View view) {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivityForResult(intent, request_addUser_Code);
    }

    public void addDestination(View view) {
        Button button = ((Button)view);
        String text = (String) button.getText();
        Log.d("me", text);

        if (text.equals("Prihod")) {
            if (data.odhodi_prihodi.odhod.datum == null) {
                Toast.makeText(MainActivity.this, "Najprej določite ODHOD!", Toast.LENGTH_LONG).show();
                Log.d("me", "Najprej določite ODHOD!");
                return;
            }
        }

        Intent intent = new Intent(this, FlyFromToActivity.class);
        intent.putExtra("button", text);
        if (text.equals("Odhod")) {
            Log.d("me", ""+data.odhodi_prihodi.odhod.datum);
            if (data.odhodi_prihodi.odhod.datum != null) {
                intent.putStringArrayListExtra(FlyFromToActivity.end_code_changeFlight, new ArrayList<String>(
                        Arrays.asList(data.odhodi_prihodi.odhod.od_lokacija, data.odhodi_prihodi.odhod.do_lokacija, data.odhodi_prihodi.odhod.datum, data.odhodi_prihodi.odhod.razred))
                );
                Log.d("me", "odhod set ");
            }
            startActivityForResult(intent, request_leavePlane_Code);
        } else if (text.equals("Prihod")) {
            if (data.odhodi_prihodi.prihod.datum != null) {
                intent.putStringArrayListExtra(FlyFromToActivity.end_code_changeFlight, new ArrayList<String>(
                        Arrays.asList(data.odhodi_prihodi.prihod.od_lokacija, data.odhodi_prihodi.prihod.do_lokacija, data.odhodi_prihodi.prihod.datum, data.odhodi_prihodi.prihod.razred))
                );
            } else if (data.odhodi_prihodi.odhod.datum != null) {
                intent.putStringArrayListExtra(FlyFromToActivity.end_code_addFlight, new ArrayList<String>(
                        Arrays.asList(data.odhodi_prihodi.odhod.do_lokacija, data.odhodi_prihodi.odhod.od_lokacija, data.odhodi_prihodi.odhod.datum, data.odhodi_prihodi.odhod.razred))
                );
                Log.d("me", "end_code_addFlight initiated");
            }
            startActivityForResult(intent, request_returnPlane_Code);
        }
    }

    public void ponastaviRezervacijo(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Potrditev");
        alertDialogBuilder.setMessage("Ali res želite ponastaviti podatke?\nLe teh potem ne bo možno povrniti.");
        alertDialogBuilder.setPositiveButton("Ponastavi", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this,"Vsi podatki so izbrisani.",Toast.LENGTH_LONG).show();

                completeReset();
            }
        });

        alertDialogBuilder.setNegativeButton("Prekliči", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this,"You clicked cancel button",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void potrdiRezervacijo(View view) {
        if (data.odhodi_prihodi.odhod.do_lokacija == null) {
                Toast.makeText(MainActivity.this, "ODHOD ni določen!", Toast.LENGTH_LONG).show();
                return;
        }

        if (data.potniki.size() == 0) {
            Toast.makeText(MainActivity.this, "Dodan ni bil noben potnik!", Toast.LENGTH_LONG).show();
            return;
        }

        if (data.placilo.st_kartice == null) {
            Toast.makeText(MainActivity.this, "Plačilo ni izpolnjeno!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Potrditev");
        alertDialogBuilder.setMessage("Ali res želite potrditi rezervacijo?\nNaročila po tem ne bo možno preklicati.");
        alertDialogBuilder.setPositiveButton("Potrdi", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this,"Rezervacija potrjena.",Toast.LENGTH_LONG).show();

                completeReset();

                Intent intent = new Intent(MainActivity.this, RezervacijaPotrjena.class);
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("Prekliči", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(MainActivity.this,"You clicked cancel button",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void completeReset() {
        data = new Data();
        adapter = new MyRecyclerViewAdapter(MainActivity.this, data);
        adapter.setClickListener(MainActivity.this::onItemClick);
        recyclerView.setAdapter(adapter);
        updateKarticaStatus();
        koncnaCenaUpdate();

        ((TextView) findViewById(R.id.odhod_textView)).setText("");
        ((TextView) findViewById(R.id.prihod_textView)).setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent sent_data) {
        super.onActivityResult(requestCode, resultCode, sent_data);

        if (resultCode == RESULT_OK) {
            if (requestCode == request_addUser_Code) {
                ArrayList<String> temp = sent_data.getStringArrayListExtra(AddUserActivity.end_code_addUser);

                Log.d("me", String.valueOf(temp));
                data.potniki.add(new Potnik(temp.get(0), temp.get(1), temp.get(2), temp.get(3), temp.get(4), temp.get(5)));
                adapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Potnik "+temp.get(1)+" "+temp.get(2)+" uspešno dodan.", Toast.LENGTH_LONG).show();
            }

            if (requestCode == request_cardPay_Code) {
                ArrayList<String> temp = sent_data.getStringArrayListExtra(VnosPlacila.end_code_addPay);

                Log.d("me", String.valueOf(temp));
                data.placilo.ime = temp.get(0);
                data.placilo.priimek = temp.get(1);
                data.placilo.st_kartice = temp.get(2);
                data.placilo.ccv = temp.get(3);
                adapter.notifyDataSetChanged();

                updateKarticaStatus();

                Toast.makeText(MainActivity.this, "Kartica sprejeta.", Toast.LENGTH_LONG).show();
            }

            if (requestCode == request_changeUser_Code) {
                ArrayList<String> temp = sent_data.getStringArrayListExtra(AddUserActivity.end_code_changeUser);

                Log.d("me", String.valueOf(temp));
                data.potniki.set(Integer.parseInt(temp.get(0)), new Potnik(temp.get(1), temp.get(2), temp.get(3), temp.get(4), temp.get(5), temp.get(6)));
                adapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Potnik "+temp.get(2)+" "+temp.get(3)+" uspešno spremenjen.", Toast.LENGTH_LONG).show();
            }

            if (requestCode == request_leavePlane_Code) {
                ArrayList<String> temp = sent_data.getStringArrayListExtra(FlyFromToActivity.end_code_addFlight);

                Log.d("me", String.valueOf(temp));
                data.odhodi_prihodi.odhod.od_lokacija = temp.get(0);
                data.odhodi_prihodi.odhod.do_lokacija = temp.get(1);
                data.odhodi_prihodi.odhod.datum = temp.get(2);
                data.odhodi_prihodi.odhod.razred = temp.get(3);

                ((TextView)findViewById(R.id.odhod_textView)).setText(data.odhodi_prihodi.odhod.od_lokacija.split("letališče ")[1]+"\nˇ\n"+data.odhodi_prihodi.odhod.do_lokacija.split("letališče ")[1]+"\n"+data.odhodi_prihodi.odhod.datum);

                Toast.makeText(MainActivity.this, "Destinacija "+temp.get(0)+" -> "+temp.get(1)+" uspešno spremenjena.", Toast.LENGTH_LONG).show();
            }

            if (requestCode == request_returnPlane_Code) {
                ArrayList<String> temp = sent_data.getStringArrayListExtra(FlyFromToActivity.end_code_addFlight);

                Log.d("me", String.valueOf(temp));
                data.odhodi_prihodi.prihod.od_lokacija = temp.get(0);
                data.odhodi_prihodi.prihod.do_lokacija = temp.get(1);
                data.odhodi_prihodi.prihod.datum = temp.get(2);
                data.odhodi_prihodi.prihod.razred = temp.get(3);

                ((TextView)findViewById(R.id.prihod_textView)).setText(data.odhodi_prihodi.prihod.od_lokacija.split("letališče ")[1]+"\nˇ\n"+data.odhodi_prihodi.prihod.do_lokacija.split("letališče ")[1]+"\n"+data.odhodi_prihodi.prihod.datum);

                Toast.makeText(MainActivity.this, "Povratek "+temp.get(0)+" -> "+temp.get(1)+" uspešno spremenjen.", Toast.LENGTH_LONG).show();
            }

            adapter.notifyDataSetChanged();
            koncnaCenaUpdate();
        }
    }

    public void updateKarticaStatus() {
        if (data.placilo.st_kartice == null) {
            ((TextView)findViewById(R.id.placilotextView)).setText("");
            return;
        }
        ((TextView)findViewById(R.id.placilotextView)).setText("kartica xxxx-xxxx-xxxx-"+data.placilo.st_kartice.replaceAll("[\\- ]?", "").substring(12) + " sprejeta");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void koncnaCenaUpdate() {
        TextView koncnaCena = findViewById(R.id.koncnaCena);
        String odhod = data.odhodi_prihodi.odhod.do_lokacija;
        String prihod = data.odhodi_prihodi.prihod.do_lokacija;

        if (data.potniki.size() == 0) {
            koncnaCena.setText(" - ");
            return;
        }

        if (odhod == null ) return;

        AtomicReference<Double> sum = new AtomicReference<>(0.00);
        data.potniki.forEach(element -> {
            sum.updateAndGet(
                    v -> v + element.getPrice(data));
        });
        koncnaCena.setText(String.format("%.2f", sum.get()));
    }
}