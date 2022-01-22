package com.trashparadise.lifemanager.ui.me;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.network.DownloadRequest;
import com.trashparadise.lifemanager.bean.network.DownloadResponse;
import com.trashparadise.lifemanager.bean.network.UploadRequest;
import com.trashparadise.lifemanager.bean.network.UploadResponse;
import com.trashparadise.lifemanager.constants.NetworkDescriptionRes;
import com.trashparadise.lifemanager.databinding.FragmentMeBinding;
import com.trashparadise.lifemanager.service.RequestService;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MeFragment extends Fragment implements LifeManagerApplication.OnAutoSyncFinishListener {

    private AppCompatActivity activity;
    private FragmentMeBinding binding;
    private LifeManagerApplication application;
    private DataManager dataManager;
    private User user;

    private SimpleDateFormat dateFormatDate;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        activity = (AppCompatActivity) getActivity();
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        application = (LifeManagerApplication) this.getActivity().getApplication();
        dataManager = DataManager.getInstance();
        dateFormatDate = new SimpleDateFormat(getString(R.string.date_format_date));

        user = dataManager.getUser();

        initView();
        initListener();
        application.meFragment=this;
        return root;
    }

    private void initListener() {
        binding.textViewUserUuid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("uuid", user.getUuid());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(application, R.string.copy_uuid,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        binding.layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(application, LoginActivity.class);
                startActivity(login);
            }
        });
        binding.layoutDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownload();
            }
        });
        binding.layoutUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpload();
            }
        });
        binding.layoutSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSync();
            }
        });
        binding.layoutAutoSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSyncSwitch();
            }
        });
        binding.switchAutoSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSyncSwitch();
            }
        });
    }


    private void initView() {
        if (user.isValidation()) {
            binding.switchAutoSync.setChecked(dataManager.getPreference().isAutoSync());
            binding.tvUserName.setText(user.getUsername());
            binding.textViewUserUuid.setText(user.getUuid());
        } else {
            binding.tvUserName.setText(R.string.no_login);
            binding.textViewUserUuid.setText("");
            dataManager.getPreference().set(Preference.AUTOSYNC, false);

        }
        binding.textViewSyncTime.setText(dateFormatDate.format(application.autoSyncTime.getTime()));
        binding.textViewSyncDescription.setText(application.autoSyncDescription);

        if (dataManager.getPreference().isAutoSync()) {
            binding.layoutManualSync.setVisibility(View.GONE);
            binding.layoutSyncTime.setVisibility(View.VISIBLE);
        }

        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.app_name);
    }


    public void onSyncSwitch() {

        Animation animationUp = AnimationUtils.loadAnimation(application, R.anim.anim_translate_up);
        Animation animationDown = AnimationUtils.loadAnimation(application, R.anim.anim_translate_down);
        if (!dataManager.getPreference().isAutoSync()) {

            binding.layoutManualSyncContainer.startAnimation(animationUp);
            animationUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dataManager.getPreference().set(Preference.AUTOSYNC, !(dataManager.getPreference().isAutoSync()));
                    binding.switchAutoSync.setChecked(dataManager.getPreference().isAutoSync());
                    binding.layoutManualSync.setVisibility(View.GONE);
                    binding.layoutSyncTime.setVisibility(View.VISIBLE);
                    binding.layoutSyncTime.startAnimation(animationDown);
                    application.onAutoSync(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else {
            binding.layoutSyncTime.startAnimation(animationUp);
            animationUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dataManager.getPreference().set(Preference.AUTOSYNC, !(dataManager.getPreference().isAutoSync()));
                    binding.switchAutoSync.setChecked(dataManager.getPreference().isAutoSync());
                    binding.layoutManualSync.setVisibility(View.VISIBLE);
                    binding.layoutSyncTime.setVisibility(View.GONE);
                    binding.layoutManualSync.startAnimation(animationDown);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private void onUpload() {
        if (user.isValidation()) {
            Toast.makeText(application, R.string.on_data_upload,
                    Toast.LENGTH_SHORT).show();

            Call<UploadResponse> call = RequestService.API.upload(new UploadRequest(user.getUuid(), user.getSession(), dataManager.onUpload()));
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    try {
                        UploadResponse body = response.body();
                        Toast.makeText(application, NetworkDescriptionRes.UPLOAD[body.state], Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(application, R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(application, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(application, R.string.user_no_login,
                    Toast.LENGTH_SHORT).show();
            Intent login = new Intent(application, LoginActivity.class);
            startActivity(login);
        }
    }

    private void onDownload() {
        if (user.isValidation()) {
            Toast.makeText(application, R.string.on_data_download,
                    Toast.LENGTH_SHORT).show();
            Call<DownloadResponse> call = RequestService.API.download(new DownloadRequest(user.getUuid(), user.getSession()));
            call.enqueue(new Callback<DownloadResponse>() {
                @Override
                public void onResponse(Call<DownloadResponse> call, Response<DownloadResponse> response) {
                    try {
                        DownloadResponse body = response.body();
                        Toast.makeText(application, NetworkDescriptionRes.DOWNLOAD[body.state], Toast.LENGTH_SHORT).show();
                        if (body.state == DownloadResponse.OK) {
                            dataManager.onDownload(body.getData());
                        }
                    } catch (Exception e) {
                        Toast.makeText(application, R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DownloadResponse> call, Throwable t) {
                    Toast.makeText(application, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(application, R.string.user_no_login,
                    Toast.LENGTH_SHORT).show();
            Intent login = new Intent(application, LoginActivity.class);
            startActivity(login);
        }
    }

    private void onSync() {
        if (user.isValidation()) {
            Toast.makeText(application, R.string.on_data_sync,
                    Toast.LENGTH_SHORT).show();

            Call<DownloadResponse> call = RequestService.API.sync(new UploadRequest(user.getUuid(), user.getSession(), dataManager.onUpload()));
            call.enqueue(new Callback<DownloadResponse>() {
                @Override
                public void onResponse(Call<DownloadResponse> call, Response<DownloadResponse> response) {
                    try {
                        DownloadResponse body = response.body();
                        Toast.makeText(application, NetworkDescriptionRes.SYNC[body.state], Toast.LENGTH_SHORT).show();
                        if (body.state == DownloadResponse.OK) {
                            dataManager.onDownload(body.getData());
                        }
                    } catch (Exception e) {
                        Toast.makeText(application, R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DownloadResponse> call, Throwable t) {
                    Toast.makeText(application, R.string.network_error, Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            try {
                Toast.makeText(application, R.string.user_no_login,
                        Toast.LENGTH_SHORT).show();
                Intent login = new Intent(application, LoginActivity.class);
                startActivity(login);

            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onAutoSyncFinish() {
        binding.textViewSyncTime.setText(dateFormatDate.format(application.autoSyncTime.getTime()));
        binding.textViewSyncDescription.setText(application.autoSyncDescription);
    }
}