package com.trashparadise.lifemanager.ui.me;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.ActivityRegisterBinding;
import com.trashparadise.lifemanager.util.ValidUtils;
import com.trashparadise.lifemanager.util.RequestUtils;

public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final String TAG = "RegisterActivity";
    String account = "";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ActivityRegisterBinding binding;
    private LifeManagerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent intent = getIntent();
        account = intent.getStringExtra("account");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.account_register);


        setOnClickListener();

        setOnFocusChangeErrMsg(binding.etPassword, "password", "密码必须不少于6位");
    }

    private void setOnClickListener() {
        binding.btSubmitRegister.setOnClickListener(this);
    }


    private void setOnFocusChangeErrMsg(EditText editText, String inputType, String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        // 失去焦点
                        if (!hasFocus) {
                            switch (inputType) {
                                case "phone":
                                    if (!ValidUtils.isPhoneValid(inputStr)) {
                                        editText.setError(errMsg);
                                    }
                                    break;

                                case "password":
                                    if (!ValidUtils.isPasswordValid(inputStr)) {
                                        editText.setError(errMsg);
                                    }
                                    break;

                                case "gender":
                                    if (!ValidUtils.isGenderValid(inputStr)) {
                                        editText.setError(errMsg);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
        );
    }


    @Override
    public void onClick(View v) {
        String username = binding.etUsername.getText().toString();
        String password1 = binding.etPassword.getText().toString();
        String password2 = binding.etPassword2.getText().toString();
        if (!(password1.equals(password2))) {
            Toast.makeText(this, "两次输入的密码不一致",
                    Toast.LENGTH_SHORT).show();

        } else {
            Thread t = new Thread(() -> {
                Looper.prepare();
                String a = RequestUtils.register(username, password1);
                if (a.equals("0000000000000000000000000000000000000000")) {
                    Toast.makeText(this.getApplication(), "用户名已存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getApplication(), "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            t.start();
            try {
                t.join();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
