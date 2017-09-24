package com.example.katarzkubat.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.katarzkubat.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    public static class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String TABLE_NAME = "items";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_CURRENT_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_SUPPLIER = "supplier";

        public static final int SUPPLIER_BLUE_VELVET = 1;
        public static final int SUPPLIER_LOST_HIGHWAY = 2;
        public static final int SUPPLIER_TWIN_PEAKS = 3;
        public static final int SUPPLIER_DUNE = 4;
        public static final int SUPPLIER_THE_ELEPHANT_MAN = 5;
        public static final int SUPPLIER_ERASERHEAD = 6;
        public static final int SUPPLIER_WILD_AT_HEARTH = 7;
        public static final int SUPPLIER_FIRE_WALK_WITH_ME = 8;
        public static final int SUPPLIER_MULHOLLAND_DRIVE = 9;
        public static final int SUPPLIER_THE_STRAIGHT_STORY = 10;
        public static final int SUPPLIER_RABBITS = 11;

        public static final int SUPPLIER_UNKNOWN = 0;


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static boolean isValidSupplier(Integer supplier) {

            if (supplier != SUPPLIER_UNKNOWN) {
                return true;
            }
            return false;
        }
    }
}

