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
import android.widget.Toast;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.network.DownloadRequest;
import com.trashparadise.lifemanager.bean.network.DownloadResponse;
import com.trashparadise.lifemanager.bean.network.UploadRequest;
import com.trashparadise.lifemanager.bean.network.UploadResponse;
import com.trashparadise.lifemanager.constants.NetworkDescriptionRes;
import com.trashparadise.lifemanager.databinding.FragmentMeBinding;
import com.trashparadise.lifemanager.service.RequestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;


public class MeFragment extends Fragment {

    private AppCompatActivity activity;
    private FragmentMeBinding binding;
    private LifeManagerApplication application;
    private DataManager dataManager;
    private User user;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    //    private View account, sync, upload, issue, praise, about;
    private Toast toast;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        activity = (AppCompatActivity) getActivity();
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        application = (LifeManagerApplication) this.getActivity().getApplication();
        dataManager = DataManager.getInstance();

        user = dataManager.getUser();
//        account = binding.layoutLogin;
//        sync = binding.layoutSync;
//        upload = binding.layoutUpload;
//        praise = binding.textViewPraise;
//        about = binding.textViewAboutUs;
//        issue = binding.textViewIssue;
//        upload.setOnClickListener(this);
//        sync.setOnClickListener(this);
//        account.setOnClickListener(this);
//        issue.setOnClickListener(this);
//        praise.setOnClickListener(this);
//        about.setOnClickListener(this);

        initView();
        initListener();

        return root;
    }

    private void initListener() {
        binding.layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getContext(), LoginActivity.class);
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
    }


    private void initView() {
        if (user.isValidation()) {
            binding.tvUserName.setText(user.getUsername());
            binding.textViewUserUuid.setText(user.getUuid());
        } else {
            binding.tvUserName.setText(R.string.no_login);
            binding.textViewUserUuid.setText("");
        }
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.app_name);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }


    private void onUpload() {
        if (user.isValidation()) {
            Toast.makeText(application, R.string.on_data_upload,
                    Toast.LENGTH_SHORT).show();

            Call<UploadResponse> call = RequestService.API.upload(new UploadRequest(user.getUuid(), user.getSession(), application.onUpload()));
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    try {
                        UploadResponse body = response.body();
                        Toast.makeText(getContext(), NetworkDescriptionRes.UPLOAD[body.state], Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(application, R.string.user_no_login,
                    Toast.LENGTH_SHORT).show();
            Intent login = new Intent(getContext(), LoginActivity.class);
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
                        Toast.makeText(getContext(), NetworkDescriptionRes.DOWNLOAD[body.state], Toast.LENGTH_SHORT).show();
                        if (body.state == DownloadResponse.OK) {
                            application.onDownload(body.getData());
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DownloadResponse> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(application, R.string.user_no_login,
                    Toast.LENGTH_SHORT).show();
            Intent login = new Intent(getContext(), LoginActivity.class);
            startActivity(login);
        }
    }

    private void onSync() {
        if (user.isValidation()) {
            Toast.makeText(application, R.string.on_data_sync,
                    Toast.LENGTH_SHORT).show();

            Call<DownloadResponse> call = RequestService.API.sync(new UploadRequest(user.getUuid(), user.getSession(), application.onUpload()));
            call.enqueue(new Callback<DownloadResponse>() {
                @Override
                public void onResponse(Call<DownloadResponse> call, Response<DownloadResponse> response) {
                    try {
                        DownloadResponse body = response.body();
                        Toast.makeText(getContext(), NetworkDescriptionRes.SYNC[body.state], Toast.LENGTH_SHORT).show();
                        if (body.state == DownloadResponse.OK) {
                            application.onDownload(body.getData());
                        }
                    } catch (Exception e) {
                        Log.e("www", e.toString());
                        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DownloadResponse> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(application, R.string.user_no_login,
                    Toast.LENGTH_SHORT).show();
            Intent login = new Intent(getContext(), LoginActivity.class);
            startActivity(login);
        }
    }
}