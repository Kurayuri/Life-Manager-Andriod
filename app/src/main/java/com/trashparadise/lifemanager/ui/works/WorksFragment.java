package com.trashparadise.lifemanager.ui.works;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.databinding.FragmentWorksBinding;

public class WorksFragment extends Fragment {

    private WorksViewModel mViewModel;
    private FragmentWorksBinding binding;

    public static WorksFragment newInstance() {
        return new WorksFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(WorksViewModel.class);

        binding = FragmentWorksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.floatingActionButtonNewWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WorkEditActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

}