package app.task.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sharewire.googlemapsclustering.Cluster;
import net.sharewire.googlemapsclustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import app.task.R;
import app.task.global.Constant;
import app.task.global.SampleClusterItem;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MapFragment extends Fragment {

    @BindView(R.id.mapView)
    MapView mMapView;

    // Vars
    HashMap<String, String> m_li;
    private GoogleMap googleMap;
    ClusterManager<SampleClusterItem> clusterManager;
    private static final LatLngBounds NETHERLANDS = new LatLngBounds(
            new LatLng(50.77083, 3.57361), new LatLng(53.35917, 7.10833));
    List<SampleClusterItem> clusterItems = new ArrayList<>();
    LatLngBounds countryList;
    String country = "";
    ArrayList<String> list = new ArrayList<>();
    double lat, lng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        loadJSONFromAsset();
        mMapView.onCreate(savedInstanceState);
        initMapView();
        return view;
    } // onCreateView

//============================================================================================

    private void initMapView() {
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
//========================= On Map Ready ===================================================================

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;
                clusterManager = new ClusterManager<>(getActivity(), googleMap);
                googleMap.setOnCameraIdleListener(clusterManager);
                clusterItems = new ArrayList<>();
                fetchCountries();

//============================= Event For Cluster ===============================================================

                clusterManager.setCallbacks(new ClusterManager.Callbacks<SampleClusterItem>() {
                    @Override
                    public boolean onClusterClick(Cluster<SampleClusterItem> cluster) {
                        Log.i(Constant.TAG, "onClusterClick");

                        return true;
                    }

                    @Override
                    public boolean onClusterItemClick(@NonNull SampleClusterItem clusterItem) {
                        Log.i(Constant.TAG, "onClusterItemClick --> " + clusterItem.getCountry());

                        SharedPreferences sharedPreferences =
                                getActivity().getSharedPreferences(Constant.MY_PREFS_NAME
                                        , Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorr = sharedPreferences.edit();

                        list.add(clusterItem.getCountry());
                        sharedPreferences.edit().putString("SAVED_ARRAY", new Gson().toJson(list)).apply();
                        editorr.apply();
                        Log.i(Constant.TAG, "SAVED_ARRAY --> " + list.size());


                        return true;
                    }
                });

//=========================== Marker Listner =================================================================

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        // change background when click
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                        Log.i(Constant.TAG, "onMarkerClick getPosition --> " + marker.getPosition());

                        double lat = marker.getPosition().latitude;
                        double lng = marker.getPosition().longitude;

// =================================== compare lat and lng if json file contain get country name from it ====================

                        for (int i = 0; i < clusterItems.size(); i++) {
                            Log.i(Constant.TAG + "countryList --> ", clusterItems.get(i).getLatitude() + "");

                            if (clusterItems.get(i).getLatitude() == lat && clusterItems.get(i).getLongitude() == lng) {

                                Snackbar.make(getView(), clusterItems.get(i).getCountry()
                                        + " "
                                        + getString(R.string.added), Snackbar.LENGTH_LONG).show();

                                SharedPreferences sharedPreferences =
                                        getActivity().getSharedPreferences(Constant.MY_PREFS_NAME
                                                , Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorr = sharedPreferences.edit();

                                list.add(clusterItems.get(i).getCountry());
                                sharedPreferences.edit().putString("SAVED_ARRAY", new Gson().toJson(list)).apply();
                                editorr.apply();
                                Log.i(Constant.TAG, "SAVED_ARRAY --> " + list.size());
                            }
                        }

                        return true;
                    }
                });
//============================================================================================

                // For showing a move to my location button
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getActivity(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Log.i(Constant.TAG, "Location is Off  --> ");

                }

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title(country)
                        .snippet(country));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(NETHERLANDS, 0));

            }
        });
    } // init Map view

//============================================================================================

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

//============================================================================================

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

//============================================================================================

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

//============================================================================================

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

//============================================================================================

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    } //loadJSONFromAsset

//============================================================================================

    private void fetchCountries() {

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("ref_country_codes");
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.i(Constant.TAG + "Details --> ", jo_inside.getString("country"));
                country = jo_inside.getString("country");
                lat = jo_inside.getDouble("latitude");
                lng = jo_inside.getDouble("longitude");

                Log.i(Constant.TAG + " country --> ", country);
                Log.i(Constant.TAG + " latitude --> ", lat + "");
                Log.i(Constant.TAG + " longitude --> ", lng + "");
                m_li = new HashMap<String, String>();
                m_li.put("country", country);

                formList.add(m_li);
                countryList = new LatLngBounds(new LatLng(lat, lng), new LatLng(lat, lng));
                clusterItems.add(new SampleClusterItem(country, new LatLng(lat, lng)));

            }


            Log.i(Constant.TAG + "clusterItems --> ", clusterItems.size() + "");
            clusterManager.setItems(clusterItems);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    } // fetchCountries

//============================================================================================

}
