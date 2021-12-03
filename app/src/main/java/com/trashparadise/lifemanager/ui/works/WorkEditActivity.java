package com.trashparadise.lifemanager.ui.works;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.ActivityBillEditBinding;
import com.trashparadise.lifemanager.databinding.ActivityWorkEditBinding;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class WorkEditActivity extends AppCompatActivity {
    private ActivityWorkEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWorkEditBinding.inflate(getLayoutInflater());
        View root=binding.getRoot();
        setContentView(root);

    }
}