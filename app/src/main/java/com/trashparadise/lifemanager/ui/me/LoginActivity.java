package com.trashparadise.lifemanager.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.network.LoginRequest;
import com.trashparadise.lifemanager.bean.network.LoginResponse;
import com.trashparadise.lifemanager.constants.NetworkDescriptionRes;
import com.trashparadise.lifemanager.databinding.ActivityLoginBinding;
import com.trashparadise.lifemanager.service.RequestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

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
        dataManager = DataManager.getInstance();
        Intent intent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.account_login);

        initListener();


    }

    private void initListener() {
        binding.btLogin.setOnClickListener(this);
        binding.tvToRegister.setOnClickListener(this);
        binding.tvForgetPassword.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        String username = binding.etAccount.getText().toString();
        String password = binding.etPassword.getText().toString();

        switch (v.getId()) {
            case R.id.bt_login:
                binding.etPassword.clearFocus();
                login(username, password);
                break;
            case R.id.tv_to_register:
                Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                intentToRegister.putExtra("account", username);
                startActivity(intentToRegister);
                break;
        }
    }

    private void login(String username, String password) {
        Call<LoginResponse> call = RequestService.API.login(new LoginRequest(username, password));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    LoginResponse body = response.body();
                    Toast.makeText(LoginActivity.this, NetworkDescriptionRes.LOGIN[body.state], Toast.LENGTH_SHORT).show();
                    if (body.state == LoginResponse.OK) {
                        dataManager.getUser().setUsername(body.username);
                        dataManager.getUser().setUuid(body.uuid);
                        dataManager.getUser().setSession(body.session);
                        dataManager.getUser().setValidation(true);
                        LoginActivity.this.finish();
                    }
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
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

