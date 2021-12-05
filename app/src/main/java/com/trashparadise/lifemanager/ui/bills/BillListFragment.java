package com.trashparadise.lifemanager.ui.bills;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.Adapter.BillListAdapter;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.databinding.FragmentBillListBinding;

public class BillListFragment extends Fragment {
    private FragmentBillListBinding binding;
    private RecyclerView recyclerView;
    private LifeManagerApplication application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBillListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        application = (LifeManagerApplication) this.getActivity().getApplication();

        recyclerView=binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new BillListAdapter(getContext()));

        return root;
    }
}