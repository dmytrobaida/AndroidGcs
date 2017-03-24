package com.diploma.dima.androidgcs.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.ui.dialogs.EditRouteDialog;
import com.diploma.dima.androidgcs.ui.interfaces.IAction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaypointRecyclerAdapter extends RecyclerView.Adapter<WaypointRecyclerAdapter.ViewHolder> {
    private String[] mDataset;

    public WaypointRecyclerAdapter(String[] dataset) {
        mDataset = dataset;
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

        ViewHolder(final View v) {
            super(v);
            ButterKnife.bind(this, v);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.waypoint_types,
                    R.layout.support_simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            activity = (AppCompatActivity) v.getContext();
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String[] arr = mDataset[position].split(" ");

        holder.x_waypoint.setText(arr[0]);
        holder.y_waypoint.setText(arr[1]);
        holder.height_waypoint.setText(arr[2]);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(R.string.delete_waypoint_dialog_title)
                        .setMessage(R.string.delete_waypoint_dialog_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(view.getContext(), "Delete " + position, Toast.LENGTH_SHORT).show();
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
                EditRouteDialog editRouteDialog = EditRouteDialog.newInstance(new IAction() {
                    @Override
                    public void done(float x, float y, float height) {
                       // holder.x_waypoint.setText(String.format("%s %s %s", x, y, height));
                    }
                });
                editRouteDialog.show(holder.activity.getFragmentManager(), "Edit");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
