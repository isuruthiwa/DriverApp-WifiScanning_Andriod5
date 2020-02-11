package com.example.driverapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class LocalDatabase extends AppCompatActivity {
    DatabaseHelper myDb;
    DatabaseTable1 table1Db;
    //Set <SQLiteOpenHelper> dataBaseSet = new HashSet<>();
    EditText editLONGI1,editLONGI2,editLATI1,editLATI2,editAreaId,editHeading;
    //EditText editROADSIGNT1, editLONGIT1, editLATIT1, editHEADINGT1, editTABLEIDT1;
    Button btnAddData;
    Button btnviewAll;
    Button btnDelete;
    Button btnviewUpdate;

    Button btnAddDataS;
    Button btnviewAllS;
    Button btnDeleteS;
    Button btnviewUpdateS;
    private Menu menu;
    Button btnLogout;

    String sSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_database);
        myDb = new DatabaseHelper(this);
        table1Db = new DatabaseTable1(this);

        editHeading = (EditText)findViewById(R.id.editHeading);     //editHeading
        editLONGI1 = (EditText)findViewById(R.id.editText_LONGI1);  //editAreaid
        editLONGI2 = (EditText)findViewById(R.id.editText_LONGI2);  //editRoadSign
        editLATI1 = (EditText)findViewById(R.id.editText_LATI1);    //editTable1Longi
        editAreaId = (EditText)findViewById(R.id.editText_id);      //editTable1Id
        editLATI2 = (EditText)findViewById(R.id.editText_lati2) ;   //editHeading
        btnAddData = (Button)findViewById(R.id.button_add);
        btnviewAll = (Button)findViewById(R.id.button_viewAll);
        btnviewUpdate= (Button)findViewById(R.id.button_update);
        btnDelete= (Button)findViewById(R.id.button_delete);
        btnLogout = (Button)findViewById(R.id.logingout);

        btnAddDataS = (Button)findViewById(R.id.add_sign);
        btnviewAllS = (Button)findViewById(R.id.view_sign);
        btnviewUpdateS= (Button)findViewById(R.id.update_sign);
        btnDeleteS= (Button)findViewById(R.id.delete_sign);

        AddData();
        viewAll();
        UpdateData();
        DeleteData();

        AddDataS();
        viewAllS();
        UpdateDataS();
        DeleteDataS();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalDatabase.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    // deleting table contents
    public void DeleteDataS() { // Table 1
        btnDeleteS.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRows = table1Db.deleteData(editAreaId.getText().toString());
                        if(deletedRows > 0)
                            Toast.makeText(LocalDatabase.this,"Data DeletedS",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(LocalDatabase.this,"Data not DeletedS",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public void DeleteData() { // Area Table
        btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRows = myDb.deleteData(editAreaId.getText().toString());
                        if(deletedRows > 0)
                            Toast.makeText(LocalDatabase.this,"Data Deleted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(LocalDatabase.this,"Data not Deleted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    // Updating table content
    public void UpdateDataS() { //Table 1
        btnviewUpdateS.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            boolean isUpdate = table1Db.updateData(editLONGI1.getText().toString(),
                                    editAreaId.getText().toString(),
                                    editLONGI2.getText().toString(),
                                    editLATI1.getText().toString(),
                                    editLATI2.getText().toString(),
                                    editHeading.getText().toString());
                            if(isUpdate == true)
                                Toast.makeText(LocalDatabase.this,"Data UpdateS",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LocalDatabase.this,"Data not UpdatedS",Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(LocalDatabase.this,"SOMETHING GOING WRONG",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
    public void UpdateData() {  // Area Table
        btnviewUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            boolean isUpdate = myDb.updateData(editAreaId.getText().toString(),
                                    editLONGI1.getText().toString(),
                                    editLONGI2.getText().toString(),
                                    editLATI1.getText().toString(),
                                    editLATI2.getText().toString());
                            if(isUpdate == true)
                                Toast.makeText(LocalDatabase.this,"Data Update",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LocalDatabase.this,"Data not Updated",Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    // Adding table data
    public  void AddDataS() {  //Table 1
        btnAddDataS.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = table1Db.insertData(
                                editAreaId.getText().toString(),
                                editLONGI2.getText().toString(),
                                editLATI1.getText().toString(),
                                editLATI2.getText().toString(),
                                editHeading.getText().toString());//****************************************************************
                        if(isInserted == true)
                            Toast.makeText(LocalDatabase.this,"Data InsertedS",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(LocalDatabase.this,"Data not InsertedS",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public  void AddData() {  //Area Table
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(editAreaId.getText().toString(),
                                editLONGI1.getText().toString(),
                                editLONGI2.getText().toString(),
                                editLATI1.getText().toString(),
                                editLATI2.getText().toString());
                        if(isInserted == true)
                            Toast.makeText(LocalDatabase.this,"Data Inserted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(LocalDatabase.this,"Data not Inserted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    // Viewing Table data
    public void viewAllS() {  // Table 1
        btnviewAllS.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = table1Db.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("TABLE_ID :"+ res.getString(0)+"\n");
                            buffer.append("AREA_ID :"+ res.getString(1)+"\n");
                            buffer.append("ROAD SIGN :"+ res.getString(2)+"\n");
                            buffer.append("LONGI :"+ res.getString(3)+"\n");
                            buffer.append("LATI :"+ res.getString(4)+"\n");
                            buffer.append("HEADING :"+ res.getString(5)+"\n\n");
                        }

                        // Show all data
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }
    public void viewAll() {  //Area Table
        btnviewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("AREA_ID :"+ res.getString(0)+"\n");
                            buffer.append("LONGI1 :"+ res.getString(1)+"\n");
                            buffer.append("LONGI2 :"+ res.getString(2)+"\n");
                            buffer.append("LATI1 :"+ res.getString(3)+"\n");
                            buffer.append("LATI2 :"+ res.getString(4)+"\n\n");
                        }

                        // Show all data
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }




}
