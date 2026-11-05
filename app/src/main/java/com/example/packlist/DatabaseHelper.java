
package com.example.packlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "PackList.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "category TEXT, " +
                "packed INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    // ADD ITEM

    public void addItem(String name, String category) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);
        values.put("packed", 0);

        db.insert("items", null, values);
    }

    // GET ALL ITEMS

    public ArrayList<Item> getAllItems() {

        ArrayList<Item> items = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM items ORDER BY id DESC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                items.add(new Item(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3) == 1
                ));
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    // GET ALL CUSTOM CATEGORIES

    public ArrayList<String> getAllCategories() {

        ArrayList<String> categories = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT category FROM items ORDER BY category",
                null
        );

        try {
            while (cursor.moveToNext()) {

                categories.add(cursor.getString(0));
            }
        } finally {
            cursor.close();
        }

        return categories;
    }

    // UPDATE PACKED STATUS

    public void updatePacked(int id, boolean packed) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("packed", packed ? 1 : 0);

        db.update(
                "items",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    // EDIT ITEM

    public void updateItem(int id,
                           String name,
                           String category) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);

        db.update(
                "items",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    // DELETE SINGLE ITEM

    public void deleteItem(int id) {

        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                "items",
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    // DELETE PACKED ITEMS

    public void deletePackedItems() {

        SQLiteDatabase db = getWritableDatabase();

        db.delete("items", "packed=1", null);
    }

    // UNCHECK ALL ITEMS

    public void uncheckAll() {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("packed", 0);

        db.update("items", values, null, null);
    }

    // ITEM MODEL CLASS

    public static class Item {

        public int id;
        public String name;
        public String category;
        public boolean packed;

        public Item(int id,
                    String name,
                    String category,
                    boolean packed) {

            this.id = id;
            this.name = name;
            this.category = category;
            this.packed = packed;
        }
    }
}
