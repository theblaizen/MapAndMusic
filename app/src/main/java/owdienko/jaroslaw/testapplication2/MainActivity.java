package owdienko.jaroslaw.testapplication2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import owdienko.jaroslaw.testapplication2.Data.ArrayData;
import owdienko.jaroslaw.testapplication2.Data.GMapMarkersObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private EditText mainActivityUserInput;
    private Button mainActivityButtonSearch;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Fragment panel;
    private FragmentTransaction transaction;
    private static int position_of = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUIElements();

        mainActivityButtonSearch.setOnClickListener(MAButtonSearchListener());
        mainActivityButtonSearch.setOnLongClickListener(MAButtonSearchLongListener());
    }


    private void initUIElements() {
        initToolbar();

        mainActivityUserInput =
                (EditText) findViewById(R.id.main_activity_user_input);
        mainActivityButtonSearch =
                (Button) findViewById(R.id.main_activity_btn_search);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.appbar_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
    }

    private View.OnClickListener MAButtonSearchListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ?
                                null : getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                mMap.clear();
                ArrayData.getInstance().clearAllData();
                new ParseTask().execute();
            }
        };
    }

    private View.OnLongClickListener MAButtonSearchLongListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mainActivityUserInput.setText("");
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                return true;
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, "Can't find style. Error: ", e);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        positionOfTheMap();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int) (marker.getTag());
                marker.showInfoWindow();
                //todo player implementation
                removeMusicPanel();
                position_of = position;
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.main_activity_placeholder, new MusicPlayerFragment(), String.valueOf(position)).commitNow();
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                removeMusicPanel();
            }
        });
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;
        BufferedReader reader;
        String resultJson;
        String userInput;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            urlConnection = null;
            reader = null;
            resultJson = "";
            if (!mainActivityUserInput.getText().toString().isEmpty())
                userInput = mainActivityUserInput.getText().toString();
            else
                Toast.makeText(MainActivity.this, "You do not input anything!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (userInput != null) {
                try {
                    URL url = new URL(Constants.MAIN_ACTIVITY_JSON_API_URL
                            + "name_startsWith=" + userInput
                            + Constants.MAIN_ACTIVITY_JSON_API_KEY);

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    resultJson = buffer.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                String returning = resultJson;
                Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, resultJson);
                if (resultJson != null) {
                    JSONObject dataJsonObj = null;

                    try {
                        dataJsonObj = new JSONObject(resultJson);
                        JSONArray geonames = dataJsonObj.getJSONArray("geonames");

                        for (int i = 0; i < geonames.length(); i++) {
                            JSONObject object = geonames.getJSONObject(i);

                            if (!dataJsonObj.isNull("geonames"))
                                ArrayData.getInstance().addItemToArray(new GMapMarkersObject(
                                        object.getDouble("lat"),
                                        object.getDouble("lng"),
                                        object.getString("countryCode"),
                                        object.getString("toponymName"),
                                        object.getString("name")));
//                        Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, "lat: " + object.getDouble("lat"));
//                        Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, "lng: " + object.getDouble("lng"));
//                        Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, "countryCode: " + object.getString("countryCode"));
//                        Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, "toponymName: " + object.getString("countryName"));
//                        Log.d(Constants.MAIN_ACTIVITY_DEBUG_TAG, "name: " + object.getString("name"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return returning;
                }
            }
            return "err 3";
        }


        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(resultJson);
                JSONArray geonames = dataJsonObj.getJSONArray("geonames");
                int totalResultsCount = dataJsonObj.getInt("totalResultsCount");
                Toast.makeText(MainActivity.this, "Total Results Count:" + totalResultsCount, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < ArrayData.getInstance().getArraySize(); j++) {
                GMapMarkersObject marker = ArrayData.getInstance().getItemByPosition(j);
                LatLng sydney = new LatLng(marker.getLat(), marker.getLng());
                Marker mark = mMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title(marker.getName())
                        .snippet(marker.getToponymName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                );
                mark.setTag(j);
            }
            positionOfTheMap();
        }


    }

    private void removeMusicPanel() {
        panel = getSupportFragmentManager().findFragmentByTag(String.valueOf(position_of));
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        if (panel != null) {
            tran.remove(panel);
            tran.commitNow();
            tran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            panel = null;
        }
    }

    private void positionOfTheMap() {
        CameraUpdate update = CameraUpdateFactory.zoomTo(4);
        mMap.moveCamera(update);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(50.45466, 30.5238)));
    }
}
