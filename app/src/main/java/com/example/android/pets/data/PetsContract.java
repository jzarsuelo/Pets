package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Defines the schema for the application
 */

public final class PetsContract {

    /** Prevent creating instance of this class */
    private PetsContract() {}

    /** Inner class that defines the table structure of the pets table */
    public final class PetEntry implements BaseColumns {

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
