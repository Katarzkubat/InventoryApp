package com.example.katarzkubat.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.katarzkubat.inventoryapp.data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter {
    private CatalogActivity activity = new CatalogActivity();

    public InventoryCursorAdapter(CatalogActivity context, Cursor cursor) {
        super(context, cursor, 0);
        activity = context;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {

        final long id;
        final int quantity;

        TextView textName = (TextView) view.findViewById(R.id.item_name);
        TextView textQuantity = (TextView) view.findViewById(R.id.current_quantity);
        TextView textPrice = (TextView) view.findViewById(R.id.item_price);
        TextView textSupplier = (TextView) view.findViewById(R.id.item_supplier);
        ImageView itemPhoto = (ImageView) view.findViewById(R.id.item_image);
        Button clickButton = (Button) view.findViewById(R.id.sale_item);

        id = cursor.getLong(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_CURRENT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        int supplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER);
        int photoColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PHOTO);

        String name = cursor.getString(nameColumnIndex);
        quantity = cursor.getInt(quantityColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String supplier = cursor.getString(supplierColumnIndex);
        String photoUriString = cursor.getString(photoColumnIndex);

        Uri photoUri = Uri.parse(photoUriString);
        textName.setText(name);
        textQuantity.setText(String.valueOf(quantity));
        textPrice.setText(String.valueOf(price));
        String supplierText = "";
        if (TextUtils.isEmpty(supplier)) {
            supplierText = context.getString(R.string.empty_supplier);
        } else {
            supplierText = (context.getResources().getStringArray(R.array.array_supplier_options))[Integer.parseInt(supplier)];
        }
        textSupplier.setText(supplierText);
        if(photoUri != null) {
            itemPhoto.setImageURI(photoUri);
        }

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.saleButtonClicked(id, quantity);
            }
        });
    }

}

