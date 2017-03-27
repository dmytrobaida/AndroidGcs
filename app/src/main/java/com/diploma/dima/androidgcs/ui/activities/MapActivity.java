package com.diploma.dima.androidgcs.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_layout);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                return true;

            case R.id.download_waypoints:
                final ArrayAdapter<Vehicle> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicles);
                final Spinner sp = new Spinner(this);
                sp.setId(R.id.dialogSpinner);
                sp.setLayoutParams(new LinearLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                sp.setAdapter(adp);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setView(sp);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog thisDialog = (AlertDialog) dialog;
                        Spinner spinner = (Spinner) thisDialog.findViewById(R.id.dialogSpinner);
                        Vehicle vehicle = (Vehicle) spinner.getSelectedItem();
                        try {
                            vehicle.receivePoints(new ActionWithMessage<List<msg_mission_item>>() {
                                @Override
                                public void handle(List<msg_mission_item> message) {
                                    Log.d("Points", message.toString());
                                }
                            });
                        } catch (VehicleBusyException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertBuilder.create().show();

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

            default:
                return super.onOptionsItemSelected(item);
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
        Waypoint waypoint = new Waypoint((float) latLng.latitude, (float) latLng.longitude, 10, mapWay, WayPointType.WayPoint);
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
