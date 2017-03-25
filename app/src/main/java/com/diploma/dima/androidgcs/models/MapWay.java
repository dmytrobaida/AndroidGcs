package com.diploma.dima.androidgcs.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.diploma.dima.androidgcs.utils.BitmapWorker;
import com.diploma.dima.androidgcs.utils.Utility;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MapWay extends SugarRecord {
    String title;
    String creationDate;
    String logoPath;

    public MapWay() {
        title = "New way";
        creationDate = getCurrentDate();
    }

    public MapWay(Context context, String title, Bitmap logo) {
        creationDate = getCurrentDate();
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

    public List<Waypoint> getWaypoints() {
        return Waypoint.find(Waypoint.class, "map_way = ?", getId().toString());
    }

    @Nullable
    public Bitmap getLogo(Context context) {
        return BitmapWorker.loadBitmap(context, logoPath);
    }

    public void setLogo(Context context, Bitmap logo) {
        if (logo != null) {
            logoPath = String.format("%s%s", title, Utility.random(10));
            BitmapWorker.saveFile(context, logo, logoPath);
        }
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
