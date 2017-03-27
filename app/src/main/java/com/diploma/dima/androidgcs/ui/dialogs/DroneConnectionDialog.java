package com.diploma.dima.androidgcs.ui.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.mavconnection.gcs.network.IpPortAddress;
import com.diploma.dima.androidgcs.ui.interfaces.IDroneAction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DroneConnectionDialog extends DialogFragment {
    @BindView(R.id.drone_address)
    EditText droneAddress;
    @BindView(R.id.drone_port)
    EditText dronePort;
    @BindView(R.id.submit_drone)
    Button submitDrone;

    IDroneAction onDone;

    public static DroneConnectionDialog newInstance(IDroneAction onDone) {
        DroneConnectionDialog droneConnectionDialog = new DroneConnectionDialog();
        droneConnectionDialog.onDone = onDone;
        return droneConnectionDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drone_connection_dialog, null);
        ButterKnife.bind(this, view);
        submitDrone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int port = Integer.parseInt(dronePort.getText().toString());
                    IpPortAddress ipPortAddress = new IpPortAddress(droneAddress.getText().toString(), port);
                    onDone.done(ipPortAddress);
                    dismiss();
                } catch (Exception ex) {
                    submitDrone.setError(getString(R.string.drone_connection_dialog_message));
                }
            }
        });

        return view;
    }
}
