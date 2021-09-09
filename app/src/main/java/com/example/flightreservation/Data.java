package com.example.flightreservation;

import android.os.Build;
import android.os.ParcelUuid;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Data {
    public class odhod {
        public String od_lokacija = null;
        public String do_lokacija = null;
        public String datum = null;
        public String razred = null;
    }

    public class prihod {
        public String od_lokacija = null;
        public String do_lokacija= null;
        public String datum = null;
        public String razred = null;

    }

    public class odhodi_prihodi {
        odhod odhod = new odhod();
        prihod prihod = new prihod();
    }

    public class placilo {
        public String ime = null;
        public String priimek = null;
        public String st_kartice = null;
        public String ccv = null;
    }

    public List<Potnik> potniki = new ArrayList<Potnik>();
    public odhodi_prihodi odhodi_prihodi = new odhodi_prihodi();
    public placilo placilo = new placilo();

    Data() {
    }
}


class Potnik {
    public String spol = null;
    public String ime = null;
    public String priimek = null;
    public String telefonska = null;
    public String datum_rojstva = null;
    public String alergije = null;

    Potnik(String spol, String ime, String priimek, String telefonska, String datum_rojstva, String alergije) {
        this.spol = spol;
        this.ime = ime;
        this.priimek = priimek;
        this.telefonska = telefonska;
        this.datum_rojstva = datum_rojstva;
        this.alergije = alergije;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Double getPrice(Data data) {
        String odhod = data.odhodi_prihodi.odhod.do_lokacija;
        String prihod = data.odhodi_prihodi.prihod.do_lokacija;
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMAN);
        DateTimeFormatter dateFormatter2 = new DateTimeFormatterBuilder()
                .appendPattern("dd/MM/yyyy[ HH:mm:ss]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        if (odhod == null && prihod == null) return null;

        Double prePrice;

        switch (FlyFromToActivity.od_do_array.indexOf(odhod)) {
            case 0:
                prePrice = 554.34;
                break;
            case 1:
                prePrice = 453.23;
                break;
            case 2:
                prePrice = 653.34;
                break;
            case 3:
                prePrice = 233.33;
                break;
            case 4:
                prePrice = 408.98;
                break;
            default:
                prePrice = 450.00;
        }

        if (prihod != null) {
            switch (FlyFromToActivity.od_do_array.indexOf(prihod)) {
                case 0:
                    prePrice += 554.34;
                    break;
                case 1:
                    prePrice += 453.23;
                    break;
                case 2:
                    prePrice += 653.34;
                    break;
                case 3:
                    prePrice += 233.33;
                    break;
                case 4:
                    prePrice += 408.98;
                    break;
                default:
                    prePrice += 450;
            }
        }

        LocalDate born = LocalDate.parse(datum_rojstva, dateFormatter2);
        LocalDate date = LocalDate.now();

        Period p = Period.between(born, date);
        int old = p.getYears();

        if (old <= 2)
            return 0.00;

        if (old <= 12)
            return Math.round(prePrice/2*100.0)/100.0;

        return prePrice;
    }
}