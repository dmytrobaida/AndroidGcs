package com.diploma.dima.androidgcs.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DroneRecyclerAdapter extends RecyclerView.Adapter<DroneRecyclerAdapter.ViewHolder> {
    private View view;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.drone_name)
        TextView droneName;

        ViewHolder(final View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    private List<Vehicle> vehicles;

    public DroneRecyclerAdapter(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public DroneRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drone_recycler_item, parent, false);
        return new DroneRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DroneRecyclerAdapter.ViewHolder holder, int position) {
        holder.droneName.setText(vehicles.get(holder.getAdapterPosition()).toString());
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }
}
