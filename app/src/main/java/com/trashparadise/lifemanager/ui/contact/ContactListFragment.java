package com.trashparadise.lifemanager.ui.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.Contact;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.adapter.ContactListAdapter;
import com.trashparadise.lifemanager.databinding.FragmentContactListBinding;


public class ContactListFragment extends Fragment implements ContactListAdapter.OnItemClickListener {
    private LifeManagerApplication application;
    private Context context;
    private RecyclerView recyclerView;
    private View view;
    private FragmentContactListBinding binding;
    private ContactListAdapter contactListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentContactListBinding.inflate(inflater,container,false);

        context=getContext();
        application=(LifeManagerApplication)getActivity().getApplication();
        recyclerView=binding.recyclerView;
        contactListAdapter=new ContactListAdapter(context, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(contactListAdapter);


        return binding.getRoot();
    }


    @Override
    public void onItemClick(String uuid,int action) {
        // todo
        switch (action){
            case 0:
                Intent intent=new Intent();
                intent.putExtra("contactUuid",uuid);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
                break;
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.delete_confirm_text)
                        .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                application.delContact(uuid);
                                contactListAdapter.updateData();
                            }
                        })
                        .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                Dialog dialogFragment = builder.create();
                dialogFragment.show();
        }

    }

    public void updateData(){
        contactListAdapter.updateData();
    }
}