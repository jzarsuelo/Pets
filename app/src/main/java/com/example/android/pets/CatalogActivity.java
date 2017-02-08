/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetsContract.PetEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new PetDbHelper(this);

        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertData() {
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(PetEntry.TABLE_NAME, null, values);

        Log.d(LOG_TAG, "Inserted data _id: " + id);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        PetDbHelper mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(PetEntry.TABLE_NAME, null, null, null, null, null, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);

            StringBuilder stringBuilder = new StringBuilder("Number of rows in pets database table: ")
                    .append(cursor.getCount())
                    .append("\n\n")
                    .append(PetEntry._ID)
                    .append(" - ")
                    .append(PetEntry.COLUMN_PET_NAME)
                    .append(" - ")
                    .append(PetEntry.COLUMN_PET_BREED)
                    .append(" - ")
                    .append(PetEntry.COLUMN_PET_GENDER)
                    .append(" - ")
                    .append(PetEntry.COLUMN_PET_WEIGHT)
                    .append("\n\n");

            boolean continueReadingFromCursor = cursor.moveToFirst();
            if ( continueReadingFromCursor ) {

                final int idIndex = cursor.getColumnIndex(PetEntry._ID);
                final int nameIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
                final int breedIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
                final int genderIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
                final int weightIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

                while (continueReadingFromCursor) {

                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String breed = cursor.getString(breedIndex);
                    int gender = cursor.getInt(genderIndex);
                    int weight = cursor.getInt(weightIndex);

                    stringBuilder.append(id)
                            .append(" - ")
                            .append(name)
                            .append(" - ")
                            .append(breed)
                            .append(" - ")
                            .append(gender)
                            .append(" - ")
                            .append(weight)
                            .append("\n");

                    continueReadingFromCursor = cursor.moveToNext();
                }
            }


            displayView.setText(stringBuilder.toString());

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
