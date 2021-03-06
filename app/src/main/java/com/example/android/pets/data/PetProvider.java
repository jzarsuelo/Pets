package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.android.pets.data.PetContract.PetEntry;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * {@link ContentProvider} for Pets app
 */

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper mPetDbHelper;


    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // Add URI patterns (CONTENT AUTHORITY and PATH)
        // to its corresponding code (PETS and PET_ID) for pets table
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        mPetDbHelper = new PetDbHelper( getContext() );
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mPetDbHelper.getReadableDatabase();

        Cursor cursor = null;
        int matchCode = sUriMatcher.match(uri);
        switch (matchCode) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = db.query(PetEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetEntry._ID+"=?";
                selectionArgs = new String [] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = db.query(PetEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int matchCode = sUriMatcher.match(uri);

        switch (matchCode) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + matchCode);

        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int matchCode = sUriMatcher.match(uri);

        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        if (breed == null) {
            throw new IllegalArgumentException("Pet requires a breed");
        }

        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if ( gender == null || !isGenderValid(gender) ) {
            throw new IllegalArgumentException("Pet requires a valid gender.");
        }

        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight == null) {
            throw new IllegalArgumentException("Pet requires a valid weight.");
        }

        switch (matchCode) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    /** Validate if the input value for gender is valid */
    private boolean isGenderValid(Integer gender) {
        switch (gender) {
            case PetEntry.GENDER_MALE:
            case PetEntry.GENDER_FEMALE:
            case PetEntry.GENDER_UNKNOWN:
                return true;
            default:
                return false;
        }

    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        SQLiteDatabase db = mPetDbHelper.getWritableDatabase();

        long id = db.insert(PetEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mPetDbHelper.getWritableDatabase();

        final int matchCode = sUriMatcher.match(uri);
        int affectedRow = 0;

        switch (matchCode) {
            case PETS:
                affectedRow = db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                selection = PetEntry._ID + "=?";

                long id = ContentUris.parseId(uri);
                selectionArgs = new String[] {String.valueOf(id)};

                affectedRow =  db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        if (affectedRow > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRow;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int matchCode = sUriMatcher.match(uri);
        switch (matchCode) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";

                long id = ContentUris.parseId(uri);
                selectionArgs = new String[] { String.valueOf(id) };

                return updatePet(uri, values, selection, selectionArgs);
        }
        return 0;
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // name
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // gender
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if ( gender == null || !isGenderValid(gender) ) {
                throw new IllegalArgumentException("Pet requires a valid gender.");
            }
        }

        // weight
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight == null || weight <= 0) {
                throw new IllegalArgumentException("Pet requires a valid weight.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = mPetDbHelper.getWritableDatabase();
        int affectedRow = db.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);

        if (affectedRow > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRow;
    }
}
