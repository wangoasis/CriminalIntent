package com.example.yjr.criminalintent;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by yjr on 2016/1/10.
 */
public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context context, String filename) {

        mContext = context;
        mFilename = filename;
    }


    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {

        JSONArray jsonArray = new JSONArray();
        for(Crime c : crimes) {
            jsonArray.put(c.toJSON());
        }

        Writer writer = null;


        try {

            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jsonArray.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(writer != null)
                writer.close();
        }

    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {

        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;

        try {
            InputStream input = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for(int i = 0; i < jsonArray.length(); i++) {
                crimes.add(new Crime(jsonArray.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(reader!=null)
                reader.close();
        }

        return crimes;
    }

}
