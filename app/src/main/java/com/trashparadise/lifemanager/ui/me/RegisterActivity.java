package com.trashparadise.lifemanager.ui.me;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.network.RegisterRequest;
import com.trashparadise.lifemanager.bean.network.RegisterResponse;
import com.trashparadise.lifemanager.constants.NetworkDescriptionRes;
import com.trashparadise.lifemanager.databinding.ActivityRegisterBinding;
import com.trashparadise.lifemanager.util.ValidUtils;
import com.trashparadise.lifemanager.service.RequestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final String TAG = "RegisterActivity";
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.account_register);


        setOnClickListener();

        setOnFocusChangeErrMsg(binding.etPassword, "password", getString(R.string.password_no_empty));
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


    @Override
    public void onClick(View v) {
        String username = binding.etUsername.getText().toString();
        String password1 = binding.etPassword.getText().toString();
        String password2 = binding.etPassword2.getText().toString();
        if (!(password1.equals(password2))) {
            Toast.makeText(this, R.string.password_differ,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ValidUtils.isPasswordValid(password1)){
            Toast.makeText(this, R.string.password_no_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Call<RegisterResponse> call = RequestService.API.register(new RegisterRequest(username, password1));
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                try {
                    RegisterResponse body = response.body();
                    Toast.makeText(RegisterActivity.this, NetworkDescriptionRes.REGISTER[body.state], Toast.LENGTH_SHORT).show();
                    if (body.state == RegisterResponse.OK) {
                        RegisterActivity.this.finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
