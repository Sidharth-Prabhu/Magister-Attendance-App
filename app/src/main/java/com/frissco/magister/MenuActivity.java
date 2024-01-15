package com.frissco.magister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

public class MenuActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setToolBar();
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        ImageView menuBtn = findViewById(R.id.menu);
        menuBtn.setImageResource(R.drawable.ic_back);
    }
}