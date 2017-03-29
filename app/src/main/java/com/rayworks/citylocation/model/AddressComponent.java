package com.rayworks.citylocation.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sean on 3/29/17.
 */

public class AddressComponent {
    @SerializedName("long_name")
    String longName;

    @SerializedName("short_name")
    String shortName;

    List<String> types;

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public List<String> getTypes() {
        return types;
    }
}
