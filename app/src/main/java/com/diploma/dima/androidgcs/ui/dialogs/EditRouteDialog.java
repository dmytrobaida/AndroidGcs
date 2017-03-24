package com.diploma.dima.androidgcs.ui.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.diploma.dima.androidgcs.R;
import com.diploma.dima.androidgcs.ui.interfaces.IAction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditRouteDialog extends DialogFragment {
    @BindView(R.id.edit_waypoint_dialog_button)
    Button submitEdit;
    @BindView(R.id.new_x_coordinate)
    EditText xCoordinate;
    @BindView(R.id.new_y_coordinate)
    EditText yCoordinate;
    @BindView(R.id.new_height)
    EditText height;
    IAction onDoneAction;

    public static EditRouteDialog newInstance(IAction onDone) {
        EditRouteDialog editRouteDialog = new EditRouteDialog();
        editRouteDialog.onDoneAction = onDone;

//        Bundle args = new Bundle();
//        args.putInt("num", num);
//        editRouteDialog.setArguments(args);

        return editRouteDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_waypoint_dialog, null);
        ButterKnife.bind(this, view);
        submitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float xc =  Float.parseFloat(xCoordinate.getText().toString());
                    float yc = Float.parseFloat(yCoordinate.getText().toString());
                    float h =  Float.parseFloat(height.getText().toString());
                    onDoneAction.done(xc,yc,h);
                    dismiss();
                }catch (NumberFormatException ex){
                    submitEdit.setError(getString(R.string.edit_route_dialog_error_message));
                }
            }
        });

        return view;
    }

}
