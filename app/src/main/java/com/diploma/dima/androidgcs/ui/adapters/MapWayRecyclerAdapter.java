package com.diploma.dima.androidgcs.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;
import com.diploma.dima.androidgcs.mavconnection.gcs.exceptions.VehicleBusyException;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.ActionWithMessage;
import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.models.Waypoint;
import com.diploma.dima.androidgcs.ui.activities.MapActivity;
import com.diploma.dima.androidgcs.utils.DialogBuilders;
import com.diploma.dima.androidgcs.utils.WaypointsConverter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapWayRecyclerAdapter extends RecyclerView.Adapter<MapWayRecyclerAdapter.ViewHolder> {
    private View view;
    private Handler mHandler;
    private List<Vehicle> vehicles;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.map_way_title)
        TextView mapWayTitle;
        @BindView(R.id.map_way_map)
        ImageButton mapImageButton;
        @BindView(R.id.map_way_delete)
        ImageButton mapWayDelete;
        @BindView(R.id.map_way_info)
        ImageButton mapWayInfo;
        @BindView(R.id.map_way_send)
        ImageButton mapWaySend;

        ViewHolder(final View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public MapWayRecyclerAdapter(List<Vehicle> vehicles) {
        mHandler = new Handler();
        this.vehicles = vehicles;
    }

    @Override
    public MapWayRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_way_recycler_item, parent, false);
        return new MapWayRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final List<MapWay> mapWays = MapWay.listAll(MapWay.class);

        holder.mapWayTitle.setText(mapWays.get(holder.getAdapterPosition()).getTitle());
        Bitmap logo = mapWays.get(holder.getAdapterPosition()).getLogo(view.getContext());
        if (logo != null) {
            holder.mapImageButton.setImageBitmap(logo);
        } else {
            holder.mapImageButton.setImageBitmap(null);
        }
        holder.mapImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Intent intent = new Intent(activity, MapActivity.class);
                long id = MapWay.listAll(MapWay.class).get((holder.getAdapterPosition())).getId();
                intent.putExtra("mapWayId", id);
                intent.putExtra("adapterPosition", holder.getAdapterPosition());
                activity.startActivityForResult(intent, 10);
            }
        });

        holder.mapWayDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(R.string.delete_map_way_dialog_title)
                        .setMessage(R.string.delete_map_way_dialog_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MapWay.listAll(MapWay.class).get((holder.getAdapterPosition())).delete();
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_dialer)
                        .show();
            }
        });

        holder.mapWayInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Info")
                        .setMessage(MapWay.listAll(MapWay.class).get(holder.getAdapterPosition()).getCreationDate())
                        .setIcon(android.R.drawable.ic_dialog_dialer)
                        .show();
            }
        });

        holder.mapWaySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (vehicles.size() > 0) {
                    DialogBuilders.createAlertDialogWithSpinner((Activity) view.getContext(), vehicles, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog thisDialog = (AlertDialog) dialog;
                            Spinner spinner = (Spinner) thisDialog.findViewById(R.id.drone_spinner);
                            Vehicle vehicle = (Vehicle) spinner.getSelectedItem();
                            if (vehicle != null) {
                                sendPoints(view.getContext(), vehicle, mapWays.get(holder.getAdapterPosition()).getWaypoints());
                            }
                        }
                    }).show();
                } else {
                    Toast.makeText(view.getContext(), R.string.please_connect, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPoints(final Context context, Vehicle vehicle, List<Waypoint> waypoints) {
        try {
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

    @Override
    public int getItemCount() {
        return MapWay.listAll(MapWay.class).size();
    }
}
