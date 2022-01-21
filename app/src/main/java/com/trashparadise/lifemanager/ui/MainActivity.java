package com.trashparadise.lifemanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.network.DownloadResponse;
import com.trashparadise.lifemanager.bean.network.UploadRequest;
import com.trashparadise.lifemanager.constants.NetworkDescriptionRes;
import com.trashparadise.lifemanager.databinding.ActivityMainBinding;
import com.trashparadise.lifemanager.service.RequestService;
import com.trashparadise.lifemanager.ui.me.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavHostFragment fragment;
    private LifeManagerApplication application;
    private DataManager dataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application=(LifeManagerApplication)getApplication();
        dataManager=DataManager.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(
                R.id.nav_host_fragment_activity_main);


        BottomNavigationView navView = findViewById(R.id.nav_view);


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_bills, R.id.navigation_works, R.id.navigation_me)
                .build();
        NavController navController = fragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    @Override
    protected void onPause() {
        application.saveData();
        onAutoSync();
        super.onPause();
    }

    private void onAutoSync() {
        User user= dataManager.getUser();
        Preference preference=dataManager.getPreference();
        if (user.isValidation() && preference.isAutoSync()) {
//            Toast.makeText(application, R.string.on_data_sync,
//                    Toast.LENGTH_SHORT).show();

            Call<DownloadResponse> call = RequestService.API.sync(new UploadRequest(user.getUuid(), user.getSession(), dataManager.onUpload()));
            call.enqueue(new Callback<DownloadResponse>() {
                @Override
                public void onResponse(Call<DownloadResponse> call, Response<DownloadResponse> response) {
                    try {
                        DownloadResponse body = response.body();
//                        Toast.makeText(MainActivity.this, NetworkDescriptionRes.SYNC[body.state], Toast.LENGTH_SHORT).show();
                        if (body.state == DownloadResponse.OK) {
                            dataManager.onDownload(body.getData());
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DownloadResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}