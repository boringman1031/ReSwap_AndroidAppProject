package com.example.reswap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    TextView tvName, tvCategory, tvDesc;
    Button btnRequestExchange;
    ImageView ivDetailImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnRequestExchange = findViewById(R.id.btnRequestExchange);
        ivDetailImage = findViewById(R.id.ivDetailImage);

        // 接收資料
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String category = intent.getStringExtra("category");
        String desc = intent.getStringExtra("desc");
        String imagePath = intent.getStringExtra("image");

        // 🟡 判斷是否為 URI 路徑並交由 Glide 處理
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(imagePath))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .centerCrop()
                    .into(ivDetailImage);
        } else {
            ivDetailImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        tvName.setText(name);
        tvCategory.setText(category);
        tvDesc.setText(desc);

        btnRequestExchange.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_exchange_request, null);

            EditText etReceiver = dialogView.findViewById(R.id.etReceiver);
            EditText etAddress = dialogView.findViewById(R.id.etAddress);
            EditText etContact = dialogView.findViewById(R.id.etContact);

            new AlertDialog.Builder(this)
                    .setTitle("填寫交換資訊")
                    .setView(dialogView)
                    .setPositiveButton("送出", (dialog, which) -> {
                        String receiver = etReceiver.getText().toString().trim();
                        String address = etAddress.getText().toString().trim();
                        String contact = etContact.getText().toString().trim();

                        if (receiver.isEmpty() || address.isEmpty() || contact.isEmpty()) {
                            Toast.makeText(this, "請完整填寫資訊！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DatabaseHelper db = new DatabaseHelper(this);
                        db.insertExchangeRequest(name, receiver, address, contact);
                        Toast.makeText(this, "交換申請已送出！", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 關閉當前頁面返回
        return true;
    }
}
