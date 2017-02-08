package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Defines the schema for the application
 */

public final class PetsContract {

    /** Prevent creating instance of this class */
    private PetsContract() {}

    /** Inner class that defines the table structure of the pets table */
    public final class PetsEntry implements BaseColumns {

        /** Prevent creating instance of this class */
        private PetsEntry() {}

        public static final String TABLE_NAME = "pets";

        public static final String COL_NAME = "name";

        public static final String COL_BREED = "breed";

        public static final String COL_GENDER = "gender";

        public static final String COL_WEIGHT = "weight";

        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

    }
}
