package com.trashparadise.lifemanager.ui.bills;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
    private BillListAdapter billListAdapter;
    private Integer form;

    public BillListFragment(Integer form) {
        this.form=form;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBillListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        application = (LifeManagerApplication) this.getActivity().getApplication();

        recyclerView=binding.recyclerView;
        billListAdapter=new BillListAdapter(getContext(),form);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(billListAdapter);
        return root;
    }
    public void updateDateSet(Integer form){
        billListAdapter.callUpdateDataSet(form);
    }

}