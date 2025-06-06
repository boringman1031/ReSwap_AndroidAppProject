package com.example.reswap;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper dbHelper;
    List<ProductAdapter.Product> favoriteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        favoriteList = new ArrayList<>();

        // ✅ 改用 SQL 篩選收藏資料
        Cursor cursor = dbHelper.getOnlyFavoriteProducts(); // ← 新增的函式

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("Category"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("Description"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("ImagePath"));

                favoriteList.add(new ProductAdapter.Product(name, category, desc, imagePath));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ProductAdapter adapter = new ProductAdapter(favoriteList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
