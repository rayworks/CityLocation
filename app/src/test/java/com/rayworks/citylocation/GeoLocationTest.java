package com.rayworks.citylocation;

import com.google.gson.Gson;
import com.rayworks.citylocation.model.GeoLocationResult;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Sean on 3/29/17.
 */

public class GeoLocationTest {
    @Test
    public void testParsingResults() {
        String raw = "{\n" +
                "         \"address_components\" : [\n" +
                "            {\n" +
                "               \"long_name\" : \"1709\",\n" +
                "               \"short_name\" : \"1709\",\n" +
                "               \"types\" : [ \"street_number\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"新闸路\",\n" +
                "               \"short_name\" : \"新闸路\",\n" +
                "               \"types\" : [ \"route\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"静安区\",\n" +
                "               \"short_name\" : \"静安区\",\n" +
                "               \"types\" : [ \"political\", \"sublocality\", \"sublocality_level_1\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"上海市\",\n" +
                "               \"short_name\" : \"上海市\",\n" +
                "               \"types\" : [ \"locality\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"上海市\",\n" +
                "               \"short_name\" : \"上海市\",\n" +
                "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"中国\",\n" +
                "               \"short_name\" : \"CN\",\n" +
                "               \"types\" : [ \"country\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"200042\",\n" +
                "               \"short_name\" : \"200042\",\n" +
                "               \"types\" : [ \"postal_code\" ]\n" +
                "            }\n" +
                "         ],\n" +
                "         \"formatted_address\" : \"中国上海市静安区新闸路1709号 邮政编码: 200042\",\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 31.228733,\n" +
                "               \"lng\" : 121.44537\n" +
                "            },\n" +
                "            \"location_type\" : \"ROOFTOP\",\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 31.2300819802915,\n" +
                "                  \"lng\" : 121.4467189802915\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 31.2273840197085,\n" +
                "                  \"lng\" : 121.4440210197085\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"place_id\" : \"ChIJQS4KWf1vsjURj1DjJ4Hc_DE\",\n" +
                "         \"types\" : [ \"street_address\" ]\n" +
                "      }";

        Gson gson = new Gson();
        GeoLocationResult result = gson.fromJson(raw, GeoLocationResult.class);
        Assert.assertEquals("上海市", result.findCity());
    }
}
