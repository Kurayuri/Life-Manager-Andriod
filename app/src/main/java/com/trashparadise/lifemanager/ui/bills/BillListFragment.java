package com.trashparadise.lifemanager.ui.bills;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.adapter.BillListAdapter;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.databinding.FragmentBillListBinding;

import java.util.TreeSet;

public class BillListFragment extends Fragment {
    private LifeManagerApplication application;
    private DataManager dataManager;
    private View view;
    private BillListAdapter billListAdapter;
    private RecyclerView recyclerView;
    private Integer form;
    private Boolean auditOn;
    private Boolean slimOn;
    private int billListLayout;

    public BillListFragment(Integer form, Boolean auditOn) {
        this.form = form;
        this.auditOn = auditOn;
        slimOn = false;
    }

    public BillListFragment() {
        this.form = -1;
        this.auditOn = false;
        slimOn = true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        billListLayout = slimOn ? R.layout.fragment_bill_list_slim : R.layout.fragment_bill_list;
        view = inflater.inflate(billListLayout, container, false);

        application = (LifeManagerApplication) this.getActivity().getApplication();
        dataManager = DataManager.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        billListAdapter = new BillListAdapter(getContext(), form, auditOn, slimOn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(billListAdapter);
        return view;
    }

    public void updateDateSet(Integer form) {
        this.form = form;
        billListAdapter.callUpdateDataSet(form);
    }

    public void onMultiChoice() {
        billListAdapter.onMultiChoice();
    }

    public void onMultiDelete() {
        TreeSet<String> chosen = billListAdapter.onMultiDelete();
        for (String uuid : chosen) {
            dataManager.delBill(uuid);
        }
        billListAdapter.callUpdateDataSet(form);
    }

    public void onMultiInterval() {
        billListAdapter.onMultiInterval();
    }

    public void onMultiReverse() {
        billListAdapter.onMultiReverse();
    }

}