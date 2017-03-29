package com.rayworks.citylocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.rayworks.citylocation.model.GeoLocationResponse;
import com.rayworks.citylocation.model.GeoLocationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;

    Executor executor = Executors.newSingleThreadExecutor();

    Handler handler = new Handler(Looper.getMainLooper());

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            System.out.println("Location : " + location);

            locationManager.removeUpdates(this);

            System.out.println(String.format(">>> long %f, lat:%f", location.getLongitude(), location.getLatitude()));
            System.out.println(String.format("deGeoLocation enabled ? %b", Geocoder.isPresent()));

            final Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());

            queryOnGoogleMapApi(location.getLatitude(), location.getLongitude());

            getAddressFromGeoLocation(location, gc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            showToastMsg(String.format("Provider Enabled %s", provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
            showToastMsg(String.format("Provider Disabled %s", provider));
        }
    };

    private void getAddressFromGeoLocation(final Location location, final Geocoder gc) {
        // Network lookup involved, put the logic off the UI thread.
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Address> addresses = null;
                try {
                    addresses = gc.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);

                    if (addresses != null) {
                        for (int i = 0; i < addresses.size(); i++) {
                            Address address = addresses.get(i);

                            final String s = String.format("Address CountryName= %s, Admin Area %s",
                                    address.getCountryName(), address.getSubAdminArea());
                            System.out.println(">>> " + s);

                            showLocationInfo(s);
                        }
                    } else {
                        System.err.println("null addresses found.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showLocationInfo(final String s) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void queryOnGoogleMapApi(double lat, double lng) {
        String urlFmt = "https://maps.google.com/maps/api/geocode/json?latlng=%f,%f&sensor=true";

        AsyncHttpGet req = new AsyncHttpGet(Uri.parse(String.format(urlFmt, lat, lng)));
        req.setTimeout(8000);
        req.setHeader("Accept-Language", "zh-CN");

        AsyncHttpClient.getDefaultInstance().executeJSONObject(
                req,
                new AsyncHttpClient.JSONObjectCallback() {
                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse source, JSONObject result) {
                        if (e != null) {
                            e.printStackTrace();

                            showToastMsg(e.getClass().getName());
                            return;
                        }

                        if (result != null) {
                            String resp = result.toString();
                            GeoLocationResponse response = new Gson().fromJson(resp, GeoLocationResponse.class);

                            String cityName = response.getCityName();
                            if (cityName != null) {
                                showToastMsg("Located via map api : city " + cityName);
                            }
                            System.out.println(">>> Result: " + resp);
                        } else {
                            showToastMsg("null resp found");
                        }
                    }

                    @Override
                    public void onConnect(AsyncHttpResponse response) {
                        super.onConnect(response);
                    }

                    @Override
                    public void onProgress(AsyncHttpResponse response, long downloaded, long total) {
                        super.onProgress(response, downloaded, total);
                    }
                });
    }

    private void showToastMsg(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                locate();
            }
        });
    }

    private void locate() {
        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PermissionChecker.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, listener);
        } else {
            requestPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0xFF);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0xFF && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        locationManager.removeUpdates(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
