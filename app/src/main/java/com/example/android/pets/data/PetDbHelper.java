package com.example.android.pets.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Created by cloudemployee on 08/02/2017.
 */

public final class PetDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = PetDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pets.db";

    private static final String CREATE_PETS_TABLE = "CREATE TABLE " + PetEntry.TABLE_NAME + " (" +
            PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PetEntry.COLUMN_PET_NAME + " TEXT," +
            PetEntry.COLUMN_PET_BREED + " TEXT," +
            PetEntry.COLUMN_PET_GENDER + " INTEGER," +
            PetEntry.COLUMN_PET_WEIGHT + " INTEGER);";

    private static final String DROP_PETS_TABLE = "DROP TABLE " + PetEntry.TABLE_NAME + ";";

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_PETS_TABLE);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "There's a problem in creating the " + PetEntry.TABLE_NAME + " table.");
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_PETS_TABLE);
            onCreate(db);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "There's a problem in dropping the " + PetEntry.TABLE_NAME + " table.");
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
    }
}
