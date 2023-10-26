package com.example.midterm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ImageListActivity extends AppCompatActivity {

    // View to display the list of images
    RecyclerView recyclerView;

    // Helper to interact with the SQLite database
    DatabaseHelper databaseHelper;

    // List to store the images retrieved from the database
    List<byte[]> imageList;

    // Adapter to bind the images to the RecyclerView
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        // Initializing the RecyclerView and its layout manager
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initializing the database helper
        databaseHelper = new DatabaseHelper(this);

        // Fetch images from database
        imageList = fetchImages();

        // Set adapter
        adapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(adapter);

        // Set a long click listener on each item in the RecyclerView
        adapter.setOnItemLongClickListener(new ImageAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                // Displaying a dialog to confirm deletion when an item is long pressed
                displayDeleteDialog(position);
            }
        });
    }

    // Method to show a dialog that asks the user if they want to delete an image
    private void displayDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the image from database
                        byte[] image = imageList.get(position);
                        deleteImageFromDatabase(image);

                        // Remove the image from the list and update the RecyclerView
                        imageList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Method to delete an image from the SQLite database
    private void deleteImageFromDatabase(byte[] image) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_IMAGE + "=?", new String[]{String.valueOf(image)});
    }

    // Method to retrieve all images from the SQLite database
    private List<byte[]> fetchImages() {
        List<byte[]> imageList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_IMAGE}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE);
            if (columnIndex != -1) {
                imageList.add(cursor.getBlob(columnIndex));
            } else {
                // Handle the case where the column doesn't exist.
                Toast.makeText(ImageListActivity.this, "Image column does not exist", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
        return imageList;
    }
}

