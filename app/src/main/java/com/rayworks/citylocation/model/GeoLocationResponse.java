package com.rayworks.citylocation.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sean on 3/29/17.
 */

public class GeoLocationResponse {
    private List<GeoLocationResult> results;

    private String status;

    @SerializedName("error_message")
    private String error;

    public List<GeoLocationResult> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public String getCityName() {
        if (hasError()) { // error occurred.
            return null;
        }
        if (results != null) {
            for (GeoLocationResult result : results) {
                String city = result.findCity();
                if (!TextUtils.isEmpty(city)) {
                    return city;
                }
            }
        }
        return null;
    }

    private boolean hasError() {
        return !TextUtils.isEmpty(error);
    }
}
