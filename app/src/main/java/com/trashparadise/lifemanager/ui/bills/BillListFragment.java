package com.trashparadise.lifemanager.ui.bills;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.adapter.BillListAdapter;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.databinding.FragmentBillListBinding;

public class BillListFragment extends Fragment {
    private LifeManagerApplication application;
    private View view;
    private BillListAdapter billListAdapter;
    private RecyclerView recyclerView;
    private Integer form;
    private Boolean auditOn;
    private Boolean slimOn;
    private int billListLayout;

    public BillListFragment(Integer form,Boolean auditOn) {
        this.form=form;
        this.auditOn=auditOn;
        slimOn=false;
    }
    public BillListFragment() {
        this.form=-1;
        this.auditOn=false;
        slimOn=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        billListLayout=slimOn?R.layout.fragment_bill_list_slim:R.layout.fragment_bill_list;
        view=inflater.inflate(billListLayout, container, false);

        application = (LifeManagerApplication) this.getActivity().getApplication();

        recyclerView=view.findViewById(R.id.recyclerView);
        billListAdapter=new BillListAdapter(getContext(),form,auditOn,slimOn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(billListAdapter);

        return view;
    }
    public void updateDateSet(Integer form){
        billListAdapter.callUpdateDataSet(form);
    }

}