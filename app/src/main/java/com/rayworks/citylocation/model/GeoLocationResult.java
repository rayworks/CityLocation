package com.rayworks.citylocation.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sean on 3/29/17.
 */

public class GeoLocationResult {
    List<String> types;

    @SerializedName("place_id")
    String placeId;

    @SerializedName("formatted_address")
    String formattedAddress;

    @SerializedName("address_components")
    List<AddressComponent> addressComponents;

    private static final String ADMIN_KEY = "administrative_area_level_1";

    @Nullable
    public String findCity() {
        if (addressComponents == null)
            return null;

        Set<String> typeSet = new HashSet<>();
        for (AddressComponent ac : addressComponents) {
            typeSet.clear();
            typeSet.addAll(ac.getTypes());

            if (typeSet.contains(ADMIN_KEY)) {
                return ac.getShortName();
            }
        }

        return null;
    }

}
