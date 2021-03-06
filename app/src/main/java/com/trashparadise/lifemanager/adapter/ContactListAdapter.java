package com.trashparadise.lifemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.bean.Contact;
import com.trashparadise.lifemanager.R;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private ArrayList<Contact> localDataSet;
    private Context context;
    private DataManager dataManager;

    private ContactListAdapter.OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String uuid,int action);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView textViewName;
        private final TextView textViewUuid;


        public TextView getTextViewName() {
            return textViewName;
        }

        public TextView getTextViewUuid() {
            return textViewUuid;
        }

        public ViewHolder(View view) {
            super(view);
            layout = (ConstraintLayout) view.findViewById(R.id.layout);
            textViewName = (TextView) view.findViewById(R.id.textView_name);
            textViewUuid = (TextView) view.findViewById(R.id.textView_uuid);

        }

        public ConstraintLayout getLayout() {
            return layout;
        }
    }

    public ContactListAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;

        dataManager=DataManager.getInstance();

        updateDataSet();
    }

    public void updateData(){
        updateDataSet();
    }

    private void updateDataSet() {
        localDataSet = dataManager.getContactList();

        ContactListAdapter.this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
        ContactListAdapter.ViewHolder viewHolder = new ContactListAdapter.ViewHolder(view);
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(localDataSet.get(viewHolder.getBindingAdapterPosition()).getContactUuid(),0);
            }
        });
        viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemClick(localDataSet.get(viewHolder.getBindingAdapterPosition()).getContactUuid(),1);
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.ViewHolder viewHolder, int position) {
        Contact contact = localDataSet.get(position);
        viewHolder.getTextViewName().setText(contact.getRemarkName());
        viewHolder.getTextViewUuid().setText(contact.getContactUuid());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
