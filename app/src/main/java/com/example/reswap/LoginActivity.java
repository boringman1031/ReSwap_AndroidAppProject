package com.example.reswap;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount, etPassword;
    private CheckBox cbShowPassword;
    private TextView tvStatus;
    private Button btnLogin;

    private String[] accounts = {"Simon", "Tony", "Marry"};
    private String[] passwords = {"111", "222", "333"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        cbShowPassword = findViewById(R.id.cbShowPassword);
        tvStatus = findViewById(R.id.tvStatus);
        btnLogin = findViewById(R.id.btnLogin);

        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        btnLogin.setOnClickListener(v -> {
            String account = etAccount.getText().toString();
            String password = etPassword.getText().toString();
            boolean valid = false;

            for (int i = 0; i < accounts.length; i++) {
                if (account.equals(accounts[i]) && password.equals(passwords[i])) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                tvStatus.setText("Login Success");
                startActivity(new Intent(LoginActivity.this, MainActivity.class)); // 進入主畫面
            } else {
                tvStatus.setText("Account or Password incorrect.");
            }
        });
    }
}
