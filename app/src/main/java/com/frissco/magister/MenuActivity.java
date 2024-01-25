package com.frissco.magister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class MenuActivity extends AppCompatActivity {

    Toolbar toolbar;
    ConstraintLayout getSourceCode, reportBug, suggestFeature, privacyPolicy, license;
    WebView webView;
    Button btnBackup;
    Button btnRestore;
    private DBHelper dbHelper;
    private static final int REQUEST_SELECT_FILE = 1001;
    private static final int PICK_BACKUP_FILE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        dbHelper = new DBHelper(this);
        setToolBar();
        getSourceCode = findViewById(R.id.getSourceCode);
        reportBug = findViewById(R.id.reportBug);
        suggestFeature = findViewById(R.id.suggestFeature);
        privacyPolicy  = findViewById(R.id.privacyPolicy);
        license = findViewById(R.id.license);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLicense();
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomTab();
            }
        });

        suggestFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:frisscocreativelabs@gmail.com"));
                startActivity(intent);
            }
        });
        reportBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:frisscocreativelabs@gmail.com"));
                startActivity(intent);
            }
        });
        getSourceCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Cyber-Zypher/Magister-Attendance-App.git"));
                startActivity(intent);
            }
        });
    }
    private void openCustomTab() {
        String url = "https://sites.google.com/view/magister-privacy-policy/";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.accent));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
    private void openLicense() {
        String url = "https://www.gnu.org/licenses/gpl-3.0.en.html#license-text";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.accent));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
    public void onBackupClick(View view) {
        try {
            dbHelper.backupAndShareDatabase(this); // Call backupAndShareDatabase method on the dbHelper instance
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Backup", "Error backing up database: " + e.getMessage());
        }
    }


    public void onRestoreClick(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_BACKUP_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_BACKUP_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();
            DBHelper dbHelper = new DBHelper(this);
            dbHelper.restoreDatabaseFromBackupFile(this, selectedFileUri);
        }
    }

//    // Method to perform the backup operation
//    private void performBackup() {
//        try {
//            DBHelper dbHelper = new DBHelper(this); // Replace 'this' with your activity or fragment context
//            File backupFile = dbHelper.backupDatabase(this);
//            if (backupFile != null) {
//                Uri contentUri = FileProvider.getUriForFile(this, "com.frissco.magister.provider", backupFile); // Replace 'com.example.fileprovider' with your FileProvider authority
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("application/octet-stream");
//                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(Intent.createChooser(shareIntent, "Share Database Backup"));
//            } else {
//                Toast.makeText(this, "Failed to create database backup", Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to create database backup", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void setToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        ImageView menuBtn = findViewById(R.id.menu);
        TextView title = findViewById(R.id.title_toolbar);
        title.setText("About");
        menuBtn.setImageResource(R.drawable.ic_back);
        menuBtn.setOnClickListener(v -> onBackPressed());
    }
}