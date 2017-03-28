package com.diploma.dima.androidgcs.ui.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;
import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.ui.adapters.MapWayRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.ways_list)
    RecyclerView waysRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.bind(this);
        waysRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        waysRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new MapWayRecyclerAdapter(((GcsApplication) getApplication()).getVehicles());
        waysRecycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actionbar_items, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                int id = data.getIntExtra("adapterPosition", -1);
                mAdapter.notifyItemChanged(id);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drones_connections:
                Intent intent = new Intent(this, DronesActivity.class);
                startActivity(intent);
                return true;

            case R.id.add_new_way:
                MapWay mapWay = new MapWay();
                mapWay.save();
                mAdapter.notifyItemInserted(MapWay.listAll(MapWay.class).size() - 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
