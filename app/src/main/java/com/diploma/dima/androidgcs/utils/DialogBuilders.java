package com.diploma.dima.androidgcs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;

import java.util.List;

public class DialogBuilders {
    public static AlertDialog createAlertDialogWithSpinner(Activity activity, List<Vehicle> vehicles, DialogInterface.OnClickListener onClickListener) {
        ArrayAdapter<Vehicle> dronesArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, vehicles);
        View droneDialog = activity.getLayoutInflater().inflate(R.layout.choose_drone_dialog, null);
        Spinner spinner = (Spinner) droneDialog.findViewById(R.id.drone_spinner);
        spinner.setAdapter(dronesArrayAdapter);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setView(droneDialog);
        alertBuilder.setPositiveButton(android.R.string.yes, onClickListener);
        return alertBuilder.create();
    }
}
