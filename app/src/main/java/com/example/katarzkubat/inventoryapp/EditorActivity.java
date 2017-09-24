package com.example.katarzkubat.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katarzkubat.inventoryapp.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int READ_REQUEST_CODE = 123;
    private static final int EXISTING_ITEM_LOADER = 0;
    private boolean ItemHasChanged = false;
    private Uri photoUri;
    private ImageView photo;
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText supplierMailEditText;
    private int supplier;
    private int quantity;
    private TextView currentQuantity;
    private String name;
    private Button plusButton;
    private Button minusButton;
    private Button orderItem;
    private Button addImage;
    private Uri iCurrentUri;
    public Spinner iSupplierSpinner;
    private int iSupplier = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_editor);

        Intent intent = getIntent();
        iCurrentUri = intent.getData();

        if (iCurrentUri == null) {
            setTitle("Add an item");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit an item");
        }

        currentQuantity = (TextView) findViewById(R.id.current_quantity);
        nameEditText = (EditText) findViewById(R.id.item_name);
        priceEditText = (EditText) findViewById(R.id.item_price);
        iSupplierSpinner = (Spinner) findViewById(R.id.supplier_spinner);
        photo = (ImageView) findViewById(R.id.item_image);
        if (iCurrentUri != null) {
            getSupportLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
        setupSpinner();
        orderItem();
        addImage();
    }

    private void setupSpinner() {

        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        iSupplierSpinner.setAdapter(supplierSpinnerAdapter);

        iSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {

                    if (selection.equals(getString(R.string.blue_velvet))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_BLUE_VELVET;
                    } else if (selection.equals(getString(R.string.lost_highway))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_LOST_HIGHWAY;
                    } else if (selection.equals(getString(R.string.twin_peaks))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_TWIN_PEAKS;
                    } else if (selection.equals(getString(R.string.dune))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_DUNE;
                    } else if (selection.equals(getString(R.string.elephant_man))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_THE_ELEPHANT_MAN;
                    } else if (selection.equals(getString(R.string.eraserhead))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_ERASERHEAD;
                    } else if (selection.equals(getString(R.string.wild_at_hearth))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_WILD_AT_HEARTH;
                    } else if (selection.equals(getString(R.string.fire_walk_with_me))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_FIRE_WALK_WITH_ME;
                    } else if (selection.equals(getString(R.string.mulholland_drive))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_MULHOLLAND_DRIVE;
                    } else if (selection.equals(getString(R.string.the_straight_story))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_THE_STRAIGHT_STORY;
                    } else if (selection.equals(getString(R.string.rabbits))) {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_RABBITS;
                    } else {
                        iSupplier = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                iSupplier = InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (iCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_clear_data);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save_data:
                saveItem();
                return true;

            case R.id.action_clear_data:
                showDeleteConfirmationDialog();
                return true;
        }

        if (!ItemHasChanged) {
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
            return true;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
        return true;
    }

    private void addImage() {
        final Button addImage = (Button) findViewById(R.id.add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }

        openImageBrowser();
    }

    private void openImageBrowser() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intentType));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.browseImage)), READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                photoUri = data.getData();
                ImageView imageView = (ImageView) findViewById(R.id.item_image);
                imageView.setImageURI(photoUri);
                imageView.invalidate();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageBrowser();
                }
        }
    }

    private void orderItem() {
        Button orderItem = (Button) findViewById(R.id.order_item);
        final Button orderSend = (Button) findViewById(R.id.order_send);
        orderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailView = (EditText) findViewById(R.id.supplier_mail);
                emailView.setVisibility(View.VISIBLE);
                orderSend.setVisibility(View.VISIBLE);
            }
        });
        orderSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailView = (EditText) findViewById(R.id.supplier_mail);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text");
                intent.setData(Uri.parse("mailto:" + emailView.getText()));
                intent.putExtra(Intent.EXTRA_SUBJECT, "New product order");

                String orderContent = "Please deliver:\n" + nameEditText.getText().toString().trim()
                        + "\nprice: " +  priceEditText.getText().toString().trim() + "$" + "\nquantity: " + quantity
                        + "\nBest regards";
                intent.putExtra(Intent.EXTRA_TEXT, orderContent);

                startActivity(intent);
            }
        });
    }
    private void saveItem() {

        String name = nameEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || quantity <= 0
                || TextUtils.isEmpty(price) || iSupplier == InventoryContract.InventoryEntry.SUPPLIER_UNKNOWN
                || Integer.parseInt(price) <= 0 || photoUri == null) {
            Toast.makeText(this, "Your data need to be properly completed", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, name);
        values.put(InventoryContract.InventoryEntry.COLUMN_CURRENT_QUANTITY, quantity);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, price);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER, iSupplier);
        values.put(InventoryContract.InventoryEntry.COLUMN_PHOTO, photoUri.toString());

        int rowsAffected = 0;
        if (iCurrentUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.unsuccessful_saving),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_saving),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
                startActivity(intent);
            }

        } else {
            rowsAffected = getContentResolver().update(iCurrentUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.failed_update),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_update),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_CURRENT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER,
                InventoryContract.InventoryEntry.COLUMN_PHOTO};

        return new CursorLoader(this,
                iCurrentUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_CURRENT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER);
            int photoColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PHOTO);

            String name = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int supplier = cursor.getInt(supplierColumnIndex);
            String photoUriString = cursor.getString(photoColumnIndex);

            nameEditText.setText(name);
            displayQuantity(quantity);
            priceEditText.setText(price);
            iSupplierSpinner.setSelection(supplier);
            if (!TextUtils.isEmpty(photoUriString) && !photoUriString.equals("null")) {
                photoUri = Uri.parse(photoUriString);
                photo.setImageURI(photoUri);
            }
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_clear);
        builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        int rowsDeleted = 0;

        if (iCurrentUri != null) {
            rowsDeleted = getContentResolver().delete(iCurrentUri, null, null);
        }
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_item_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!ItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public void increaseQuantity(View view) {
        quantity++;
        displayQuantity(quantity);
    }

    public void decreaseQuantity(View view) {
        if (quantity <= 0) {
            return;
        }
        quantity--;
        if (quantity >= 0) {
            displayQuantity(quantity);
        }
    }

    public void displayQuantity(int quantity) {
        currentQuantity.setText(String.valueOf(quantity));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        currentQuantity.setText("");
        iSupplierSpinner.setSelection(-1);
    }
}




