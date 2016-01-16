package com.example.yjr.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

public class CrimeLab {

    private static final String JSON_CRIME_FILE = "crimes.json";

    private static CrimeLab sCrimeLab;
    private Context context;
    private ArrayList<Crime> crimes;
    private CriminalIntentJSONSerializer criminalIntentJSONSerializer;

    private CrimeLab(Context context) {
        this.context = context;
        criminalIntentJSONSerializer = new CriminalIntentJSONSerializer(context, JSON_CRIME_FILE);

        try {
            crimes = criminalIntentJSONSerializer.loadCrimes();
        } catch (Exception e) {
            crimes = new ArrayList<>();
        }
    }

    public ArrayList<Crime> getCrimes() {
        return crimes;
    }

    public static CrimeLab get(Context context) {

        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }
        return sCrimeLab;
    }

    public Crime getCrime(UUID ID) {

        for (Crime crime : crimes) {
            if (crime.getID().equals(ID)) {
                return crime;
            }
        }
        return null;
    }

    public void addCrime(Crime c) {
        crimes.add(c);
    }

    public boolean saveCrimes() {
        try {
            criminalIntentJSONSerializer.saveCrimes(crimes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteCrime(Crime c) {
        crimes.remove(c);
    }

}
