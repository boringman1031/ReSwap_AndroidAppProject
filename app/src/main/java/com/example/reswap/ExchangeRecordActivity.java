package com.example.reswap;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ExchangeRecordAdapter.ExchangeRecord> recordList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_record);
        setTitle("交換紀錄");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewRecords);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getAllExchangeRequests();

        while (cursor.moveToNext()) {
            String product = cursor.getString(cursor.getColumnIndexOrThrow("ProductName"));
            String receiver = cursor.getString(cursor.getColumnIndexOrThrow("Receiver"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("Address"));
            String contact = cursor.getString(cursor.getColumnIndexOrThrow("Contact"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("Timestamp"));
            recordList.add(new ExchangeRecordAdapter.ExchangeRecord(product, receiver, address, contact, time));
        }

        cursor.close();
        recyclerView.setAdapter(new ExchangeRecordAdapter(this, recordList));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

