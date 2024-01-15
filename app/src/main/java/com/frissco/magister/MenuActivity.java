package com.frissco.magister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

public class MenuActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout getSourceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setToolBar();
        getSourceCode = findViewById(R.id.getSourceCode);
        getSourceCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/Cyber-Zypher/Magister-Attendance-App";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        ImageView menuBtn = findViewById(R.id.menu);
        menuBtn.setImageResource(R.drawable.ic_back);
    }
}