package com.example.yjr.criminalintent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by yjr on 2015/3/17.
 */
public class Crime {

    private static final String JSON_CRIME_UUID = "JSON_CRIME_UUID";
    private static final String JSON_CRIME_TITLE = "JSON_CRIME_TITLE";
    private static final String JSON_CRIME_DATE = "JSON_CRIME_DATE";
    private static final String JSON_CRIME_ISSOLVED = "JSON_CRIME_ISSOLVED";

    public Crime() {
        ID = UUID.randomUUID();
        date = new Date();
    }

    public Crime(JSONObject jsonObject) throws JSONException {
        ID = UUID.fromString(jsonObject.getString(JSON_CRIME_UUID));
        title = jsonObject.getString(JSON_CRIME_TITLE);
        date = new Date(jsonObject.getLong(JSON_CRIME_DATE));
        isSolved = jsonObject.getBoolean(JSON_CRIME_ISSOLVED);
    }


    public JSONObject toJSON() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_CRIME_UUID, ID.toString());
        jsonObject.put(JSON_CRIME_TITLE, title);
        jsonObject.put(JSON_CRIME_ISSOLVED, isSolved);
        jsonObject.put(JSON_CRIME_DATE, date.getTime());

        return jsonObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getID() {
        return ID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean isChecked) {
        this.isSolved = isChecked;
    }

    @Override
    public String toString() {
        return this.title;
    }

    private String title;
    private UUID ID;
    private Date date;
    private boolean isSolved;
}
