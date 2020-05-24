package com.ksb.sahyog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ksb.sahyog.R;

public class ChooserActivity extends AppCompatActivity {

    Button needy,helper;
    int spinnerval=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        getSupportActionBar().hide();

        needy = findViewById(R.id.needy);
        helper = findViewById(R.id.helper);
        needy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(chooserActivity.this, "Needy Screen", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(ChooserActivity.this, Login.class);
                i.putExtra("spinnerval",1);
                startActivity(i);

            }
        });
        helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(chooserActivity.this, "Helper Screen", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(ChooserActivity.this, Login.class);
                i.putExtra("spinnerval",2);
                startActivity(i);
            }
        });
    }
}