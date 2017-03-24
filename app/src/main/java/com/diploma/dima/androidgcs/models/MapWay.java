package com.diploma.dima.androidgcs.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.diploma.dima.androidgcs.utils.BitmapWorker;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapWay implements Serializable {
    private String title;
    private ArrayList<Waypoint> waypoints;
    private String creationDate;
    private String logoPath;

    public MapWay(Context context, String title, Bitmap logo) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        creationDate = dateFormat.format(date);
        this.title = title;
        setLogo(context, logo);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Bitmap getLogo(Context context) {
        return BitmapWorker.loadBitmap(context, logoPath);
    }

    public void setLogo(Context context, Bitmap logo) {
        if (logo != null) {
            logoPath = String.format("%s%d", title, hashCode());
            BitmapWorker.saveFile(context, logo, logoPath);
        }
    }
}
