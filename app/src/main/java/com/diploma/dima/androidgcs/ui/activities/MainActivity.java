package com.diploma.dima.androidgcs.ui.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.ui.adapters.MapWaysRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.ways_list)
    RecyclerView waysRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<MapWay> mapWays = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.bind(this);
        waysRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        waysRecycler.setLayoutManager(mLayoutManager);

        mapWays.add(new MapWay(this, "Way 1", BitmapFactory.decodeResource(getResources(), R.drawable.route_map)));
        mapWays.add(new MapWay(this, "Way 2", BitmapFactory.decodeResource(getResources(), R.drawable.chart)));
        mAdapter = new MapWaysRecyclerAdapter(mapWays);
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
                int id = data.getIntExtra("id", -1);
                if (id > -1) {
                    MapWay mapWay = (MapWay) data.getSerializableExtra("MapWay");
                    mapWays.set(id, mapWay);
                    mAdapter.notifyItemChanged(id);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        switch (item.getItemId()) {
            case R.id.connect_to_drone:
                Intent intent = new Intent(this, DroneActivity.class);
                startActivity(intent);
                return true;

            case R.id.add_new_way:
                mapWays.add(new MapWay(this, "Way " + (mapWays.size() + 1), BitmapFactory.decodeResource(getResources(), R.drawable.chart)));
                mAdapter.notifyItemInserted(mapWays.size() + 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
