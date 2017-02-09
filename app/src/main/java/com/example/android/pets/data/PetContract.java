package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the schema for the application
 */

public final class PetContract {

    /** Prevent creating instance of this class */
    private PetContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PETS = "pets";

    /** Inner class that defines the table structure of the pets table */
    public static final class PetEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        /** Prevent creating instance of this class */
        private PetEntry() {}

        public static final String TABLE_NAME = "pets";

        public static final String COLUMN_PET_NAME = "name";

        public static final String COLUMN_PET_BREED = "breed";

        public static final String COLUMN_PET_GENDER = "gender";

        public static final String COLUMN_PET_WEIGHT = "weight";

        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

    }
}
