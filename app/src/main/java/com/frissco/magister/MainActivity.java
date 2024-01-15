package com.frissco.magister;

import static com.frissco.magister.DBHelper.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Currency;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classitems = new ArrayList<>();
    Toolbar toolbar;
    DBHelper dbHelper;
    Button logoutBtn;
    FloatingActionButton mChooseBtn;
    ImageView mImageView;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private ImageView timeTable;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();
        //Floating Button Code
        fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(v -> showDialog());

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            // You can access media files
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        }

        timeTable = findViewById(R.id.timeTable);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String savedImageUri = sharedPreferences.getString("imageUri", null);
        if (savedImageUri != null) {
            timeTable.setImageURI(Uri.parse(savedImageUri));
        }

        dbHelper = new DBHelper(this);

        loadData();

        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.main_toolbar);
//        toolbar.inflateMenu(R.menu.main_menu);
//        toolbar.setOnMenuItemClickListener(menuItem->onMenuItemClick(menuItem));
        mChooseBtn = findViewById(R.id.changeTimeTable);
        mImageView = findViewById(R.id.timeTable);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this,classitems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));
        setToolBar();

        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
            }
        });

        mChooseBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearImage();
                return true;
            }
        });
    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            mImageView.setImageURI(selectedImage);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                mImageView.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }

            Uri timeTableUri = data.getData();
            timeTable.setImageURI(timeTableUri);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("imageUri", timeTableUri.toString());
            editor.apply();
        }
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        ImageButton menuBtn = findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        Cursor cursor = dbHelper.getClassTable();

        classitems.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_ID));
            String className = cursor.getString(cursor.getColumnIndex(CLASS_NAME_KEY));
            String subjectName = cursor.getString(cursor.getColumnIndex(SUBJECT_NAME_KEY));

            classitems.add(new ClassItem(id,className,subjectName));
        }
    }

    private void gotoItemActivity(int position) {
        Intent intent = new Intent(this,StudentActivity.class);

        intent.putExtra("className", classitems.get(position).getClassName());
        intent.putExtra("subjectName",classitems.get(position).getSubjectName());
        intent.putExtra("position",position);
        intent.putExtra("cid",classitems.get(position).getCid());
        startActivity(intent);
    }

    private void showDialog(){
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className,subjectName)->addClass(className,subjectName));
    }

    private void addClass(String className,String subjectName) {
        long cid = dbHelper.addClass(className,subjectName);
        ClassItem classItem = new ClassItem(cid,className,subjectName);
        classitems.add(classItem);
        classAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(int position) {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_UPDATE_DIALOG);
        dialog.setListener((className,subjectName)->updateClass(position,className,subjectName));
    }

    public void updateClass(int position, String className, String subjectName) {
        if (-1== dbHelper.updateClass(classitems.get(position).getCid(),className,subjectName));
        classitems.get(position).setClassName(className);
        classitems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);
    }

    private void deleteClass(int position) {
        dbHelper.deleteClass(classitems.get(position).getCid());
        classitems.remove(position);
        classAdapter.notifyItemRemoved(position);
    }

    private void clearImage() {
        // Clear the ImageView
        timeTable.setImageResource(android.R.color.transparent);

        // Remove the image URI from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("imageUri");
        editor.apply();

        Toast.makeText(this, "Image cleared", Toast.LENGTH_SHORT).show();
    }
}