package com.frissco.magister;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SheetListActivity extends AppCompatActivity {
    private ListView sheetList;
    private ArrayAdapter adapter;
    Toolbar toolbar;
    private ArrayList<String> listItems = new ArrayList();
    private long cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_list);
        setToolBar();
//        getSupportActionBar().hide();

        toolbar = findViewById(R.id.main_toolbar);

        cid = getIntent().getLongExtra("cid",-1);
        loadListItems();
        sheetList = findViewById(R.id.sheetList);
        adapter = new ArrayAdapter(this,R.layout.sheet_list,R.id.date_list_item,listItems);
        sheetList.setAdapter(adapter);

        sheetList.setOnItemClickListener(((parent, view, position, id) -> openSheetActivity(position)));
    }
    private void openSheetActivity(int position) {
        long [] idArray = getIntent().getLongArrayExtra("idArray");
        int [] rollArray = getIntent().getIntArrayExtra("rollArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        Intent intent = new Intent(this,SheetActivity.class);
        intent.putExtra("idArray",idArray);
        intent.putExtra("rollArray",rollArray);
        intent.putExtra("nameArray",nameArray);
        intent.putExtra("month",listItems.get(position));
        startActivity(intent);
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        TextView title = findViewById(R.id.title_toolbar);
        ImageView menuBtn = findViewById(R.id.menu);
        menuBtn.setVisibility(View.GONE);
        title.setText("Choose an attendance sheet");
    }

    private void loadListItems() {
        Cursor cursor = new DBHelper(this).getDistinctMonths(cid);

        while (cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_KEY));//23.02.2022
            listItems.add(date.substring(3));
        }
    }
}