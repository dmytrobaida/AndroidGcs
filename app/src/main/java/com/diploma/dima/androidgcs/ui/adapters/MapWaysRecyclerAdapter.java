package com.diploma.dima.androidgcs.ui.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.ui.activities.MapActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapWaysRecyclerAdapter extends RecyclerView.Adapter<MapWaysRecyclerAdapter.ViewHolder> {
    private ArrayList<MapWay> mapWays;
    private View view;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.map_way_title)
        TextView mapWayTitle;
        @BindView(R.id.map_way_map)
        ImageButton mapImageButton;
        @BindView(R.id.map_way_delete)
        ImageButton mapWayDelete;

        ViewHolder(final View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public MapWaysRecyclerAdapter(ArrayList<MapWay> mapWays) {
        this.mapWays = mapWays;
    }

    @Override
    public MapWaysRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_way_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mapWayTitle.setText(mapWays.get(position).getTitle());
        holder.mapImageButton.setImageBitmap(mapWays.get(position).getLogo(view.getContext()));

        holder.mapImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Intent intent = new Intent(activity, MapActivity.class);
                intent.putExtra("id", position);
                intent.putExtra("MapWay", mapWays.get(position));
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
                                Toast.makeText(view.getContext(), "Delete " + position, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_dialer)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mapWays.size();
    }
}
