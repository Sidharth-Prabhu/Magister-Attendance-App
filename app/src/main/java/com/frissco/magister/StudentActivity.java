package com.frissco.magister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    LinearLayout main_layout;
    Toolbar toolbar;
    private String className;
    private String subjectName;
    private int position;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StudentItem> studentItems = new ArrayList<>();
    private DBHelper dbHelper;
    private long cid;
    private MyCalender calender;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
//        getSupportActionBar().hide();

        calender = new MyCalender();

        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position",-1);
        cid = intent.getLongExtra("cid",-1);

        setToolBar();
        loadData();
        recyclerView = findViewById(R.id.student_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StudentAdapter(this,studentItems);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> changeStatus(position));
        loadStatusData();
    }

    private void loadData() {
        Cursor cursor = dbHelper.getStudentTable(cid);
        Log.i("1234567890","loadData: "+cid);
        studentItems.clear();
        while (cursor.moveToNext()){
            long sid = cursor.getLong(cursor.getColumnIndex(DBHelper.S_ID));
            int roll = cursor.getInt(cursor.getColumnIndex(DBHelper.STUDENT_ROLL_KEY));
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.STUDENT_NAME_KEY));
            studentItems.add(new StudentItem(sid,roll,name));
        }
        cursor.close();
    }

    private void changeStatus(int position) {
        String status = studentItems.get(position).getStatus();

        if(status.equals("P")) status = "A";
        else status = "P";

        studentItems.get(position).setStatus(status);
        adapter.notifyItemChanged(position);
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);
        save.setOnClickListener(v->saveStatus());

        title.setText(className);
        subtitle.setText(subjectName+" | "+calender.getDate());
        back.setOnClickListener(v -> onBackPressed());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem->onMenuItemClick(menuItem));
    }

    private void saveStatus(){
        for(StudentItem studentItem : studentItems){
            String status = studentItem.getStatus();
            if(status!="P") status = "A";
            long value = dbHelper.addStatus(studentItem.getSid(),cid,calender.getDate(),status);

            if(value==-1)dbHelper.updateStatus(studentItem.getSid(),calender.getDate(),status);
            Toast.makeText(this, "Attendence Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStatusData(){
        for(StudentItem studentItem : studentItems){
            String status = dbHelper.getStatus(studentItem.getSid(),calender.getDate());
            if(status!=null) studentItem.setStatus(status);
            else studentItem.setStatus("");
        }
        adapter.notifyDataSetChanged();
    }

    private Boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId()==R.id.add_student){
            showAddStudentDialog();
        }
        else if(menuItem.getItemId()==R.id.show_Calender){
            showCalender();
        }else if(menuItem.getItemId()==R.id.show_attendence_sheet){
            openSheetList();
        }
        return true;
    }

    private void openSheetList() {
        long[] idArray = new long[studentItems.size()];
        String[] nameArray = new String[studentItems.size()];
        for (int i =0 ; i<idArray.length;i++)
            idArray[i] = studentItems.get(i).getSid();
        int[] rollArray = new int[studentItems.size()];
        for (int i =0 ; i<rollArray.length;i++)
            rollArray[i] = studentItems.get(i).getRoll();
        for (int i =0 ; i<nameArray.length;i++)
            nameArray[i] = studentItems.get(i).getName();

        Intent intent = new Intent(this, SheetListActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("idArray",idArray);
        intent.putExtra("rollArray",rollArray);
        intent.putExtra("nameArray",nameArray);
        startActivity(intent);
    }

    private void showCalender() {

        calender.show(getSupportFragmentManager(),"");
        calender.setOnCalenderOkClickListener(this::onCalenderOkClicked);
    }

    private void onCalenderOkClicked(int year, int month, int day) {
        calender.setDate(year,month,day);
        subtitle.setText(subjectName+" | "+calender.getDate());
        loadStatusData();
    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_ADD_DIALOG);
        dialog.setListener((roll,name)->addStudent(roll,name));
    }

    private void addStudent(String rollString, String name) {
        int roll = Integer.parseInt(rollString);
        long sid = dbHelper.addStudent(cid, roll, name);
        StudentItem studentItem = new StudentItem(sid, roll, name);

        // Add a space between the roll number and the name
        String rollAndName = roll + " " + name;
        studentItem.setRollAndName(rollAndName);

        studentItems.add(studentItem);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showUpdateStudentDialog(item.getGroupId());
                break;
            case 1:
                deleteStudent(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateStudentDialog(int position) {
        MyDialog dialog = new MyDialog(studentItems.get(position).getRoll(),studentItems.get(position).getName());
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_UPDATE_DIALOG);
        dialog.setListener((roll_string,name)->updateStudent(position,name));
    }

    private void updateStudent(int position, String name) {
        dbHelper.updateStudent(studentItems.get(position).getSid(),name);
        studentItems.get(position).setName(name);
        adapter.notifyItemChanged(position);
    }

    private void deleteStudent(int groupId) {
        dbHelper.deleteStudent(studentItems.get(position).getSid());
        studentItems.remove(position);
        adapter.notifyItemRemoved(position);
    }
}