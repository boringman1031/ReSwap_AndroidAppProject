package com.example.reswap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity {

    EditText etName, etCategory, etDescription;
    Button btnAdd, btnSelectImage;
    ImageView ivProductImage;
    Uri selectedImageUri = null;

    DatabaseHelper dbHelper;
    private static final int REQUEST_IMAGE_PICK = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        btnAdd = findViewById(R.id.btnAdd);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivProductImage = findViewById(R.id.ivProductImage);

        dbHelper = new DatabaseHelper(this);

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String category = etCategory.getText().toString();
            String description = etDescription.getText().toString();
            String imagePath = selectedImageUri != null ? selectedImageUri.toString() : "";

            if (name.isEmpty() || category.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.insertProduct(name, category, description, imagePath);
            if (success) {
                Toast.makeText(this, "新增成功！", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "新增失敗！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    // 建立檔案名稱與儲存目錄
                    String filename = "img_" + System.currentTimeMillis() + ".jpg";
                    File file = new File(getFilesDir(), filename);

                    try (InputStream inputStream = getContentResolver().openInputStream(uri);
                         OutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                    }

                    selectedImageUri = Uri.fromFile(file);
                    ivProductImage.setImageURI(selectedImageUri);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "圖片載入失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
