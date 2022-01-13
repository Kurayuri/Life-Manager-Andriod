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

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.databinding.FragmentMeBinding;
import com.trashparadise.lifemanager.util.RequestUtils;


public class MeFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private AppCompatActivity activity;
    private FragmentMeBinding binding;
    private LifeManagerApplication application;
    private DataManager dataManager;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    private View account, sync, upload, issue, praise, about;
    private Toast toast;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        activity = (AppCompatActivity) getActivity();
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        application = (LifeManagerApplication) this.getActivity().getApplication();
        dataManager=DataManager.getInstance();
        account = binding.layoutLogin;
        sync = binding.layoutSync;
        upload = binding.layoutUpload;
//        praise = binding.textViewPraise;
//        about = binding.textViewAboutUs;
//        issue = binding.textViewIssue;
        upload.setOnClickListener(this);
        sync.setOnClickListener(this);
        account.setOnClickListener(this);
//        issue.setOnClickListener(this);
//        praise.setOnClickListener(this);
//        about.setOnClickListener(this);
        binding.textViewUserUuid.setOnLongClickListener(this);

        initView();

        return root;
    }

    @Override
    public void onClick(View v) {
        toast = null;
        Integer id = v.getId();
        switch (id) {
            case R.id.layout_login:
                Intent login = new Intent(getContext(), LoginActivity.class);
                startActivity(login);
                break;
            case R.id.layout_sync:
                if (dataManager.getUser().isValidation()) {

                    Toast.makeText(application, "正在同步您的数据及设置",
                            Toast.LENGTH_SHORT).show();
                    Thread t = new Thread(() -> {
                        Looper.prepare();
                        String a = RequestUtils.download(dataManager.getUser().getUuid());
                        if (a.equals("0000000000000000000000000000000000000000")) {
                            Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                        } else {
                            application.onPull(a);
                            Toast.makeText(application, "已从云端下载用户数据及设置",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    t.start();
                    try {
                        t.start();
                    } catch (Exception ignored) {
                    }

                } else {
                    Toast.makeText(application, "用户未登陆",
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.layout_upload:
                if (dataManager.getUser().isValidation()) {
                    Toast.makeText(application, "正在上传您的数据及设置",
                            Toast.LENGTH_SHORT).show();
                    Thread t = new Thread(() -> {
                        Looper.prepare();
                        String a = RequestUtils.upload(dataManager.getUser().getUuid(), application.onPush());
                        if (a.equals("0000000000000000000000000000000000000000")) {
                            Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(application, "已保存用户数据及设置到云端",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    t.start();
                    try {
                        t.start();
                    } catch (Exception ignored) {
                    }
                } else {
                    Toast.makeText(application, "用户未登陆",
                            Toast.LENGTH_SHORT).show();
                }

                break;
//            case R.id.textView_issue:
//                Toast.makeText(application, "这个软件完美无暇，如有问题请自我反思",
//                        Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.textView_praise:
//                Toast.makeText(application, "请对给你分享这个软件的人一个好评",
//                        Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.textView_aboutUs:
//                Toast.makeText(application, "这是一个软件工程大作业",
//                        Toast.LENGTH_SHORT).show();
//                break;
        }
    }

    private void initView() {
        User user = dataManager.getUser();
        if (user.isValidation()) {
            binding.tvUserName.setText(user.getUsername());
            binding.textViewUserUuid.setText(user.getUuid());
        } else {
            binding.tvUserName.setText("未登录");
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

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.textView_userUuid:
                ClipboardManager clipboard = (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("uuid", dataManager.getUser().getUuid());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(application, "已复制用户链接",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}