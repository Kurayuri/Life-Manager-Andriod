package com.trashparadise.lifemanager.ui.me;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;
import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.bean.PackLoginBean;
import com.trashparadise.lifemanager.databinding.ActivityLoginBinding;
import com.trashparadise.lifemanager.util.RequestUtils;
import com.trashparadise.lifemanager.util.ValidUtils;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {
    private ActivityLoginBinding binding;
    private DataManager dataManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        dataManager=DataManager.getInstance();
        Intent intent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.account_login);

        setOnClickListener();

        setOnFocusChangeErrMsg(binding.etAccount, "phone", "手机号格式不正确");
        setOnFocusChangeErrMsg(binding.etPassword, "password", "密码必须不少于6位");
    }

    private void setOnClickListener() {
        binding.btLogin.setOnClickListener(this); // 登录按钮
        binding.tvToRegister.setOnClickListener(this); // 注册文字
        binding.tvForgetPassword.setOnClickListener(this); // 忘记密码文字

    }


    private void setOnFocusChangeErrMsg(EditText editText, String inputType, String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus) {
                            switch (inputType) {
//                                case "phone":
//                                    if (!ValidUtils.isPhoneValid(inputStr)) {
//                                        editText.setError(errMsg);
//                                    }
//                                    break;
                                case "password":
                                    if (!ValidUtils.isPasswordValid(inputStr)) {
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


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        // 获取用户输入的账号和密码以进行验证
        String account = binding.etAccount.getText().toString();
        String password = binding.etPassword.getText().toString();

        switch (v.getId()) {
            case R.id.bt_login:
                binding.etPassword.clearFocus();
                asyncLoginWithXiaoXiaoSHTTP(account, password);

                break;
            case R.id.tv_to_register:

                Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                intentToRegister.putExtra("account", account);
                startActivity(intentToRegister);
                break;


//                break;
        }
    }

    private void asyncLoginWithXiaoXiaoSHTTP(String telephone, String password) {
        Thread t = new Thread(() -> {
            Looper.prepare();
            String a = RequestUtils.login(telephone, password);

            if (a.equals("0000000000000000000000000000000000000000")) {
                Toast.makeText(this, "用户名不存在", Toast.LENGTH_SHORT).show();
            } else if (a.equals("1111111111111111111111111111111111111111")) {
                Toast.makeText(this, "密码不对", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                try {

                    Gson gson=new Gson();
                    PackLoginBean packLoginBean=gson.fromJson(a, PackLoginBean.class);
                    dataManager.getUser().setUuid(packLoginBean.getUuid());
                    dataManager.getUser().setUsername(packLoginBean.getUsername());
                    dataManager.getUser().setValidation(true);
                } catch (Exception e) {
                }
                finish();
            }
        });
        try {
            t.start();
        } catch (Exception ignored) {
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

