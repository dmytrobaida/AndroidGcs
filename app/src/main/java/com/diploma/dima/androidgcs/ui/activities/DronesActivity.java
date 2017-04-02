package com.diploma.dima.androidgcs.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.diploma.dima.androidgcs.GcsApplication;
import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.mavconnection.gcs.Gcs;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;
import com.diploma.dima.androidgcs.mavconnection.gcs.exceptions.GcsNotListeningException;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.Action;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.ConnectionHandler;
import com.diploma.dima.androidgcs.mavconnection.gcs.network.IpPortAddress;
import com.diploma.dima.androidgcs.ui.adapters.DroneRecyclerAdapter;
import com.diploma.dima.androidgcs.ui.dialogs.DroneConnectionDialog;
import com.diploma.dima.androidgcs.ui.interfaces.IDroneAction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DronesActivity extends AppCompatActivity {
    private Gcs gcs;
    private List<Vehicle> vehicles;

    @BindView(R.id.drone_recycler)
    RecyclerView droneRecycler;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    private Handler mHandler;
    final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drones_activity_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        mHandler = new Handler();
        GcsApplication application = (GcsApplication) getApplication();
        gcs = application.getGroundControlStation();
        vehicles = application.getVehicles();

        mLayoutManager = new LinearLayoutManager(this);
        droneRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new DroneRecyclerAdapter(gcs, vehicles);
        droneRecycler.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drones_activity_actionbar_items, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.connect_to_drone:
                connectToDrone();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void connectToDrone() {
        DroneConnectionDialog.newInstance(new IDroneAction() {
            @Override
            public void done(IpPortAddress address) {
                try {
                    gcs.connectToVehicle(address, new ConnectionHandler() {
                        @Override
                        public void success(final Vehicle vehicle) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vehicles.add(vehicle);
                                    mAdapter.notifyItemInserted(vehicles.indexOf(vehicle));
                                }
                            });
                        }

                        @Override
                        public void failure(Vehicle vehicle) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.fail_con, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (GcsNotListeningException e) {
                    e.printStackTrace();
                }
            }
        }).show(getFragmentManager(), "Drone");
    }
}
