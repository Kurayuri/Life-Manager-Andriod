package com.trashparadise.lifemanager.ui.works;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.adapter.WorkListAdapter;


public class WorkListFragment extends Fragment {
    private LifeManagerApplication application;
    private View view;
    private WorkListAdapter workListAdapter;
    private RecyclerView recyclerView;
    private Integer form;
    private Boolean auditOn;
    private Boolean slimOn;
    private int workListLayout;
    
    public WorkListFragment(Integer form,Boolean auditOn) {
        this.form=form;
        this.auditOn=auditOn;
        slimOn=false;
    }
    public WorkListFragment() {
        this.form=-1;
        this.auditOn=false;
        slimOn=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        workListLayout=slimOn?R.layout.fragment_work_list_slim:R.layout.fragment_work_list;
        view=inflater.inflate(workListLayout, container, false);

        application = (LifeManagerApplication) this.getActivity().getApplication();

        recyclerView=view.findViewById(R.id.recyclerView);
        workListAdapter=new WorkListAdapter(getContext(),form,auditOn,slimOn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(workListAdapter);

        return view;
    }

    public void updateDateSet(Integer form){
        workListAdapter.callUpdateDataSet(form);
    }
}