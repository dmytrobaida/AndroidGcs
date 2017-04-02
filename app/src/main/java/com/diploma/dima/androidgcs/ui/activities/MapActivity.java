package com.diploma.dima.androidgcs.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import com.diploma.dima.androidgcs.GcsApplication;
import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_item;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;
import com.diploma.dima.androidgcs.mavconnection.gcs.exceptions.VehicleBusyException;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.ActionWithMessage;
import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.models.WayPointType;
import com.diploma.dima.androidgcs.models.Waypoint;
import com.diploma.dima.androidgcs.ui.adapters.WaypointRecyclerAdapter;
import com.diploma.dima.androidgcs.ui.interfaces.IResultAction;
import com.diploma.dima.androidgcs.utils.DialogBuilders;
import com.diploma.dima.androidgcs.utils.LandPointGenerator;
import com.diploma.dima.androidgcs.utils.WaypointsConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    GoogleMap googleMap;
    PolylineOptions lineOptions;

    @BindView(R.id.way_points_recycler)
    RecyclerView waypointsRecycler;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    MapWay mapWay;
    int adapterPosition;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    ArrayList<Marker> markers = new ArrayList<>();

    private List<Vehicle> vehicles;
    private Handler mHandler;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_layout);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mHandler = new Handler();

        GcsApplication application = (GcsApplication) getApplication();
        vehicles = application.getVehicles();

        long mapwayId = getIntent().getLongExtra("mapWayId", -1);
        adapterPosition = getIntent().getIntExtra("adapterPosition", -1);
        mapWay = MapWay.findById(MapWay.class, mapwayId);
        mLayoutManager = new LinearLayoutManager(this);
        waypointsRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new WaypointRecyclerAdapter(mapWay.getId(), new IResultAction() {
            @Override
            public void doAction() {
                onMapReady(googleMap);
            }
        });
        waypointsRecycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_activity_actionbar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.upload_waypoints:
                if (vehicles.size() > 0) {
                    DialogBuilders.createAlertDialogWithSpinner(this, vehicles, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog thisDialog = (AlertDialog) dialog;
                            Spinner spinner = (Spinner) thisDialog.findViewById(R.id.drone_spinner);
                            Vehicle vehicle = (Vehicle) spinner.getSelectedItem();
                            if (vehicle != null) {
                                sendPoints(vehicle, mapWay.getWaypoints());
                            }
                        }
                    }).show();
                } else {
                    Toast.makeText(this, R.string.please_connect, Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.download_waypoints:
                if (vehicles.size() > 0) {
                    DialogBuilders.createAlertDialogWithSpinner(this, vehicles, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog thisDialog = (AlertDialog) dialog;
                            Spinner spinner = (Spinner) thisDialog.findViewById(R.id.drone_spinner);
                            Vehicle vehicle = (Vehicle) spinner.getSelectedItem();
                            if (vehicle != null) {
                                receivePoints(vehicle);
                            }
                        }
                    }).show();
                } else {
                    Toast.makeText(this, R.string.please_connect, Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.snapshot_waypoints:
                if (mapWay.getWaypoints().size() > 1) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
                }
                googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        mapWay.setLogo(context, bitmap);
                        mapWay.save();
                        Intent intent = new Intent();
                        intent.putExtra("adapterPosition", adapterPosition);
                        setResult(RESULT_OK, intent);
                    }
                });
                return true;

            case R.id.generate_points:
                if (mapWay.getWaypoints().size() > 1) {
                    LandPointGenerator.generate(mapWay.getWaypoints(), mapWay, 0.1f);
                    mAdapter.notifyDataSetChanged();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendPoints(Vehicle vehicle, List<Waypoint> waypoints) {
        try {
            final Context context = this;
            vehicle.sendPoints(WaypointsConverter.convert(waypoints), new ActionWithMessage<String>() {
                @Override
                public void handle(String message) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (VehicleBusyException e) {
            e.printStackTrace();
        }
    }

    private void receivePoints(Vehicle vehicle) {
        for (final Waypoint waypoint : mapWay.getWaypoints()) {
            waypoint.delete();
        }
        mAdapter.notifyDataSetChanged();

        try {
            vehicle.receivePoints(new ActionWithMessage<List<msg_mission_item>>() {
                @Override
                public void handle(List<msg_mission_item> message) {
                    for (final Waypoint waypoint : WaypointsConverter.convertBack(message, mapWay)) {
                        waypoint.save();
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        } catch (VehicleBusyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.clear();
        lineOptions = new PolylineOptions().color(Color.BLUE);
        markers.clear();
        googleMap = map;
        builder = new LatLngBounds.Builder();

        for (Waypoint waypoint : mapWay.getWaypoints()) {
            LatLng latLng = waypoint.getlatLng();
            builder.include(latLng);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);

            switch (waypoint.getWayPointType()) {
                case Land:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_land)).draggable(true);
                    break;

                case TakeOff:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_takeoff)).draggable(true);
                    break;

                case WayPoint:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fly)).draggable(true);
                    break;
            }

            lineOptions.add(latLng);
            markers.add(googleMap.addMarker(markerOptions));
        }

        map.addPolyline(lineOptions);
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        builder.include(latLng);
        float defHeight = Float.parseFloat(prefs.getString("default_height", "10"));
        Waypoint waypoint = new Waypoint((float) latLng.latitude, (float) latLng.longitude, defHeight, mapWay, WayPointType.WayPoint);
        waypoint.save();
        mAdapter.notifyItemInserted(mapWay.getWaypoints().size() - 1);

        lineOptions.add(latLng);
        googleMap.addPolyline(lineOptions);
        Marker newMarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fly)).draggable(true));
        markers.add(newMarker);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        int position = markers.indexOf(marker);
        Waypoint waypoint = mapWay.getWaypoints().get(position);
        waypoint.setX((float) marker.getPosition().latitude);
        waypoint.setY((float) marker.getPosition().longitude);
        waypoint.save();
        mAdapter.notifyItemChanged(position);
        onMapReady(googleMap);
    }
}
