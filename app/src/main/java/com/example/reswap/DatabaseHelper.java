package com.example.reswap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reswap.db";
    private static final int DATABASE_VERSION = 2; // ✅ 升級版本以觸發 onUpgrade

    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_EXCHANGE = "ExchangeRequests";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ✅ 商品表格
        String createProductTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                "Name TEXT PRIMARY KEY, " +
                "Category TEXT, " +
                "Description TEXT, " +
                "ImagePath TEXT, " +
                "IsFavorite INTEGER DEFAULT 0)";
        db.execSQL(createProductTable);

        // ✅ 交換紀錄表格
        String createExchangeTable = "CREATE TABLE " + TABLE_EXCHANGE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ProductName TEXT, " +
                "Receiver TEXT, " +
                "Address TEXT, " +
                "Contact TEXT, " +
                "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createExchangeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ❗升級時刪除舊表再建立新表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCHANGE);
        onCreate(db);
    }

    // 📦 新增商品
    public boolean insertProduct(String name, String category, String desc, String imagePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Category", category);
        values.put("Description", desc);
        values.put("ImagePath", imagePath);
        long result = db.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    public Cursor getAllProducts() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }

    // ❤️ 收藏機制
    public boolean isFavorite(String name) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT IsFavorite FROM " + TABLE_PRODUCTS + " WHERE Name = ?",
                new String[]{name}
        );
        boolean result = false;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0) == 1;
        }
        cursor.close();
        return result;
    }

    public void addFavorite(String name) {
        getWritableDatabase().execSQL("UPDATE " + TABLE_PRODUCTS + " SET IsFavorite = 1 WHERE Name = ?", new Object[]{name});
    }

    public void removeFavorite(String name) {
        getWritableDatabase().execSQL("UPDATE " + TABLE_PRODUCTS + " SET IsFavorite = 0 WHERE Name = ?", new Object[]{name});
    }

    public Cursor getOnlyFavoriteProducts() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE IsFavorite = 1", null);
    }

    // 🔁 插入交換紀錄
    public boolean insertExchangeRequest(String productName, String receiver, String address, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ProductName", productName);
        values.put("Receiver", receiver);
        values.put("Address", address);
        values.put("Contact", contact);
        long result = db.insert(TABLE_EXCHANGE, null, values);
        return result != -1;
    }

    // 📄 取得所有交換紀錄
    public Cursor getAllExchangeRequests() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_EXCHANGE + " ORDER BY Timestamp DESC", null
        );
    }
}
