package com.frissco.magister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class SheetActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton csv_maker;
    private LinearLayout linear;
    private Bitmap bitmap;
    public TableLayout txt;
    private SQLiteDatabase db;

    private Workbook workbook;

    private static final int CREATE_FILE_REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
//        getSupportActionBar().hide();

        txt=findViewById(R.id.tableLayout);
        linear=findViewById(R.id.lineard);
        csv_maker=findViewById(R.id.createPdf);
        showTable();
    }

    private void showTable() {
        DBHelper dbHelper = new DBHelper(this);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        long[] idArray = getIntent().getLongArrayExtra("idArray");
        int[] rollArray = getIntent().getIntArrayExtra("rollArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        String month = getIntent().getStringExtra("month");

        int DAY_IN_MONTH = getDayInMonth(month);

        int rowSize = idArray.length +1;
        TableRow[] rows = new TableRow[rowSize];
        TextView[] roll_tvs = new TextView[rowSize];
        TextView[] name_tvs = new TextView[rowSize];
        TextView[][] status_tvs = new TextView[rowSize][DAY_IN_MONTH + 1];

        for (int i = 0; i < rowSize; i++) {
            roll_tvs[i] = new TextView(this);
            name_tvs[i] = new TextView(this);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j] = new TextView(this);
            }
        }

        roll_tvs[0].setText("Roll");
        roll_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
        name_tvs[0].setText("Name");
        name_tvs[0].setTypeface(name_tvs[0].getTypeface(), Typeface.BOLD);
        for (int i = 1; i < DAY_IN_MONTH; i++) {
            status_tvs[0][i].setText(String.valueOf(i));
            status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
        }

        for (int i = 1; i < rowSize; i++) {
            roll_tvs[i].setText(String.valueOf(rollArray[i-1]));
            name_tvs[i].setText(nameArray[i-1]);

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if(day.length()==1) day = "0"+day;
                String date = day+"."+month;
                String status = dbHelper.getStatus(idArray[i-1],date);
                status_tvs[i][j].setText(status);
            }
        }

        for (int i = 0; i < rowSize; i++) {
            rows[i] = new TableRow(this);

            if (i % 2 == 0)
                rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
            else rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));


            roll_tvs[i].setPadding(16,16,16,16);
            name_tvs[i].setPadding(16,16,16,16);

            rows[i].addView(roll_tvs[i]);
            rows[i].addView(name_tvs[i]);

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j].setPadding(16,16,16,16);
                rows[i].addView(status_tvs[i][j]);
            }

            tableLayout.addView(rows[i]);
        }
        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);

        csv_maker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportToExcel();
            }
        });
    }

//    private void exportToExcel() {
//        // Create a new workbook
//        Workbook workbook = new HSSFWorkbook(); // For .xls format
//        // Workbook workbook = new XSSFWorkbook(); // For .xlsx format
//
//        // Create a sheet
//        Sheet sheet = workbook.createSheet("Attendance");
//
//        // Get the number of rows and columns in the table layout
//        int rowCount = txt.getChildCount();
//        int columnCount = ((TableRow) txt.getChildAt(0)).getChildCount();
//
//        // Iterate through the table layout to populate the sheet
//        for (int i = 0; i < rowCount; i++) {
//            TableRow row = (TableRow) txt.getChildAt(i);
//            Row sheetRow = sheet.createRow(i);
//
//            for (int j = 0; j < columnCount; j++) {
//                View view = row.getChildAt(j);
//                Cell cell = sheetRow.createCell(j);
//
//                if (view instanceof TextView) {
//                    TextView textView = (TextView) view;
//                    cell.setCellValue(textView.getText().toString());
//                }
//            }
//        }
//
//        // Create a temporary file to store the workbook
//        try {
//            File file = new File(getCacheDir(), "attendance.xls"); // For .xls format
//            // File file = new File(getCacheDir(), "attendance.xlsx"); // For .xlsx format
//            FileOutputStream fos = new FileOutputStream(file);
//            workbook.write(fos);
//            fos.close();
//
//            // Generate a content URI using FileProvider
//            Uri contentUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
//
//            // Create an intent to share the file
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("application/vnd.ms-excel"); // For .xls format
//            // shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // For .xlsx format
//            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission to receiving app
//            startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error exporting Excel file", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void exportToExcel() {
        // Create a new workbook
        workbook = new HSSFWorkbook(); // For .xls format
        // Workbook workbook = new XSSFWorkbook(); // For .xlsx format

        // Create a sheet
        Sheet sheet = workbook.createSheet("Attendance");

        // Get the number of rows and columns in the table layout
        int rowCount = txt.getChildCount();
        int columnCount = ((TableRow) txt.getChildAt(0)).getChildCount();

        // Iterate through the table layout to populate the sheet
        for (int i = 0; i < rowCount; i++) {
            TableRow row = (TableRow) txt.getChildAt(i);
            Row sheetRow = sheet.createRow(i);

            for (int j = 0; j < columnCount; j++) {
                View view = row.getChildAt(j);
                Cell cell = sheetRow.createCell(j);

                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    cell.setCellValue(textView.getText().toString());
                }
            }
        }

        // Create an intent to create a new document
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.ms-excel"); // For .xls format
        // intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // For .xlsx format
        intent.putExtra(Intent.EXTRA_TITLE, "attendance.xls"); // Default file name

        // Start the activity to create the document
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    workbook.write(outputStream);
                    outputStream.close();
                    Toast.makeText(this, "Excel file saved to " + uri.toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error exporting Excel file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }




    private int getDayInMonth(String month) {
        int monthIndex = Integer.valueOf(month.substring(0,1));
        int year = Integer.valueOf(month.substring(4));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,monthIndex);
        calendar.set(Calendar.YEAR,year);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}