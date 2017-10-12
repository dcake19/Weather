package com.example.android.weather.ui.forecast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.android.weather.R;

@SuppressLint("ValidFragment")
public class ShareDialog extends DialogFragment implements View.OnClickListener{

  //  View rootview;
    private String mSubject;
    private String mBody;

    public ShareDialog(String subject, String body) {
        mSubject = subject;
        mBody = body;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.weather_share_dialog, container, false);

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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + mSubject + "&body=" + mBody);
                intent.setData(data);
                startActivity(intent);
                dismiss();
                break;
            case  R.id.btn_dismiss:
                dismiss();
                break;
        }
    }

}
