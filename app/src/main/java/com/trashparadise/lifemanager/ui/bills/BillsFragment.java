package com.trashparadise.lifemanager.ui.bills;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.FragmentBillsBinding;

public class BillsFragment extends Fragment {

    private BillsViewModel mViewModel;
    private FragmentBillsBinding binding;

    public static BillsFragment newInstance() {
        return new BillsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(BillsViewModel.class);

        binding = FragmentBillsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getChildFragmentManager().beginTransaction().add(R.id.fragmentContainer_billList,new BillListFragment()).commit();


        binding.floatingActionButtonNewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BillEditActivity.class);
                intent.putExtra("uuid","");
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getChildFragmentManager().beginTransaction().replace(R.id.fragmentContainer_billList,new BillListFragment()).commit();
    }
}