package com.example.nodak_;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Kakaomap extends AppCompatActivity {


    private ImageButton mapBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.go_kakaomap);
        mapBtn = (ImageButton)findViewById(R.id.kakaomapButton);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("geo:38.899533,-77.036476");
                Intent it = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);
                /*Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("com.google.android.apps.maps"));
                startActivity(i);*/
            }
        });
    }

}
