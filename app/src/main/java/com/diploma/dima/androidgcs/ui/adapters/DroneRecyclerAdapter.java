package com.diploma.dima.androidgcs.ui.adapters;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.mavconnection.gcs.Gcs;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.Action;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DroneRecyclerAdapter extends RecyclerView.Adapter<DroneRecyclerAdapter.ViewHolder> {
    private View view;
    private Handler mHandler;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.drone_name)
        TextView droneName;
        @BindView(R.id.drone_position)
        TextView dronePosition;
        @BindView(R.id.drone_axis)
        TextView droneAxis;
        @BindView(R.id.drone_battery)
        TextView droneBattery;
        @BindView(R.id.disconnect_drone)
        Button disconnectDrone;
        @BindView(R.id.drone_mode)
        TextView droneMode;

        ViewHolder(final View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    private List<Vehicle> vehicles;
    private Gcs gcs;

    public DroneRecyclerAdapter(Gcs gcs, List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        this.gcs = gcs;
        mHandler = new Handler();
    }

    @Override
    public DroneRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drone_recycler_item, parent, false);
        return new DroneRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DroneRecyclerAdapter.ViewHolder holder, int position) {
        final Vehicle vehicle = vehicles.get(holder.getAdapterPosition());
        holder.droneName.setText(vehicle.toString());
        holder.droneMode.setText(vehicle.getVehicleParameters().getMode());
        holder.droneBattery.setText(String.format(view.getResources().getString(R.string.battery_str),
                vehicle.getVehicleParameters().getBatteryRemaining(),
                vehicle.getVehicleParameters().getBatteryVoltage(),
                vehicle.getVehicleParameters().getBatteryCurrent()));

        holder.droneAxis.setText(String.format(view.getResources().getString(R.string.ang_pos_str),
                vehicle.getVehicleParameters().getYaw(),
                vehicle.getVehicleParameters().getPitch(),
                vehicle.getVehicleParameters().getRoll()));

        holder.dronePosition.setText(String.format(view.getResources().getString(R.string.pos_str),
                vehicle.getVehicleParameters().getLat(),
                vehicle.getVehicleParameters().getLng(),
                vehicle.getVehicleParameters().getAlt()));

        holder.disconnectDrone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = vehicles.indexOf(vehicle);
                vehicles.remove(vehicle);
                gcs.disconnectVehicle(vehicle);
                notifyItemRemoved(index);
            }
        });

        vehicle.setOnHeartbeatHandler(new Action() {
            @Override
            public void handle() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }
}
