package com.example.midterm;

// Importing necessary libraries and modules
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {

    // UI elements declarations
    private Button captureButton;
    private Button saveButton;
    private Button viewButton;
    private ImageView imgCapture;

    // ActivityResultLauncher to handle the result of capturing an image
    private final ActivityResultLauncher<Intent> imageCaptureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() !=null) {
                        Bitmap bp = null;
                        Bundle extras = result.getData().getExtras();
                        if(extras !=null){
                            bp= (Bitmap) extras.get("data");
                        }
                        imgCapture.setImageBitmap(bp);
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    //method to convert ImageView to byte array
    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    // method to insert image into the database
    public void insertImage(byte[] image) {
        try {
            SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IMAGE, image);
            db.insert(DatabaseHelper.TABLE_NAME, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing UI elements
        imgCapture = findViewById(R.id.imageView);
        captureButton = findViewById(R.id.captureButton);
        saveButton = findViewById(R.id.saveButton);
        viewButton = findViewById(R.id.viewButton);

        //onClick listener to start capturing an image
        captureButton.setOnClickListener(v -> {
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            imageCaptureLauncher.launch(cInt);

        });

        //onClick listener to save the captured image to the database
        saveButton.setOnClickListener(v -> {
            byte[] image = imageViewToByte(imgCapture);
            insertImage(image);
            Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
        });

        //onClick listener to start ImageListActivity
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageListActivity.class);
                startActivity(intent);
            }
        });


    }


}

