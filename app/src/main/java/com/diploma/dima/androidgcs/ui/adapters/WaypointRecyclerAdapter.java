package com.diploma.dima.androidgcs.ui.adapters;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.models.WayPointType;
import com.diploma.dima.androidgcs.models.Waypoint;
import com.diploma.dima.androidgcs.ui.dialogs.EditWaypointDialog;
import com.diploma.dima.androidgcs.ui.interfaces.IPointAction;
import com.diploma.dima.androidgcs.ui.interfaces.IResultAction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaypointRecyclerAdapter extends RecyclerView.Adapter<WaypointRecyclerAdapter.ViewHolder> {
    private long mapWayId;
    private IResultAction action;

    public WaypointRecyclerAdapter(long mapWayId, IResultAction action) {
        this.mapWayId = mapWayId;
        this.action = action;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.waypoint_recycler_item, parent, false);
        WaypointRecyclerAdapter.ViewHolder vh = new WaypointRecyclerAdapter.ViewHolder(v);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.waypoint_x_coordinate)
        TextView x_waypoint;
        @BindView(R.id.waypoint_y_coordinate)
        TextView y_waypoint;
        @BindView(R.id.waypoint_height)
        TextView height_waypoint;
        @BindView(R.id.waypoint_type)
        Spinner spinner;
        @BindView(R.id.edit_waypoint)
        ImageButton edit;
        @BindView(R.id.delete_waypoint)
        ImageButton delete;

        final AppCompatActivity activity;
        ArrayAdapter<WayPointType> adapter;
        SharedPreferences prefs;

        ViewHolder(final View v) {
            super(v);
            ButterKnife.bind(this, v);

            adapter = new ArrayAdapter<>(v.getContext(), R.layout.support_simple_spinner_dropdown_item, WayPointType.values());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            activity = (AppCompatActivity) v.getContext();
            prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        List<Waypoint> waypoints = MapWay.findById(MapWay.class, mapWayId).getWaypoints();
        Waypoint waypoint = waypoints.get(holder.getAdapterPosition());
        holder.spinner.setSelection(holder.adapter.getPosition(waypoint.getWayPointType()));

        holder.x_waypoint.setText(Double.toString(waypoint.getX()));
        holder.y_waypoint.setText(Double.toString(waypoint.getY()));
        holder.height_waypoint.setText(Double.toString(waypoint.getHeight()));

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<Waypoint> waypoints = MapWay.findById(MapWay.class, mapWayId).getWaypoints();
                waypoints.get(holder.getAdapterPosition()).setWayPointType((WayPointType) adapterView.getSelectedItem());
                if(waypoints.get(holder.getAdapterPosition()).getWayPointType() == WayPointType.TakeOff){
                    float takeOffAngle = Float.parseFloat(holder.prefs.getString("takeoff_angle", "30"));
                    waypoints.get(holder.getAdapterPosition()).setTakeOffAngle(takeOffAngle);
                }
                waypoints.get(holder.getAdapterPosition()).save();
                action.doAction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(R.string.delete_waypoint_dialog_title)
                        .setMessage(R.string.delete_waypoint_dialog_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MapWay.findById(MapWay.class, mapWayId).getWaypoints().get(holder.getAdapterPosition()).delete();
                                notifyItemRemoved(holder.getAdapterPosition());
                                action.doAction();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<Waypoint> wayPoints = MapWay.findById(MapWay.class, mapWayId).getWaypoints();
                final Waypoint wayPoint = wayPoints.get(holder.getAdapterPosition());
                EditWaypointDialog editWaypointDialog = EditWaypointDialog.newInstance(wayPoint, new IPointAction() {
                    @Override
                    public void done(double x, double y, double height) {
                        wayPoint.setX((float) x);
                        wayPoint.setY((float) y);
                        wayPoint.setHeight((float) height);
                        wayPoint.save();
                        notifyItemChanged(wayPoints.indexOf(wayPoint));
                        action.doAction();
                    }
                });
                editWaypointDialog.show(holder.activity.getFragmentManager(), "Edit");
            }
        });
    }

    @Override
    public int getItemCount() {
        return MapWay.findById(MapWay.class, mapWayId).getWaypoints().size();
    }
}
