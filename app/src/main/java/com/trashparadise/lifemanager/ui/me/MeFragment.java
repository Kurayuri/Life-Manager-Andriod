package com.trashparadise.lifemanager.ui.me;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.FragmentMeBinding;

public class MeFragment extends Fragment {

    private AppCompatActivity activity;
    private FragmentMeBinding binding;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        activity=(AppCompatActivity)getActivity();



        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ActionBar actionBar=activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);



        return root;
    }

}