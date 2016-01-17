package com.example.yjr.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yjr on 2016/1/17.
 */
public class Photo {

    private static final String PHOTO_JSON_FILE = "photo_json_file";

    private String mFilename;

    public Photo(String filename) {
        mFilename = filename;
    }

    public Photo(JSONObject jsonObject) throws JSONException {
        mFilename = jsonObject.getString(PHOTO_JSON_FILE);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PHOTO_JSON_FILE, mFilename);
        return jsonObject;
    }

    public String getFilename() {
        return mFilename;
    }
}
