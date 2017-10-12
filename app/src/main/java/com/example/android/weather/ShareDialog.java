package com.example.android.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class ShareDialog extends DialogFragment implements View.OnClickListener{

    View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =  inflater.inflate(R.layout.weather_share_dialog, container, false);

        ImageButton email = (ImageButton) rootview.findViewById(R.id.btn_email);
        email.setOnClickListener(this);

        Button dismiss = (Button) rootview.findViewById(R.id.btn_dismiss);
        dismiss.setOnClickListener(this);

        return rootview;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_email:

                dismiss();
                break;
            case  R.id.btn_dismiss:
                dismiss();
                break;
        }
    }

}
