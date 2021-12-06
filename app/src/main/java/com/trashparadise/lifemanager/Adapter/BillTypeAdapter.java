package com.trashparadise.lifemanager.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.TypeBean;

import java.util.ArrayList;

public class BillTypeAdapter extends RecyclerView.Adapter<BillTypeAdapter.ViewHolder>  {
    private ArrayList<TypeBean> localDataSet;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ConstraintLayout layout;

        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view){
            super(view);
            layout=(ConstraintLayout)view.findViewById(R.id.layout);
            textView=(TextView)view.findViewById(R.id.textView);
            imageView=(ImageView) view.findViewById(R.id.imageView);
        }

        public ConstraintLayout getLayout() {
            return layout;
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public BillTypeAdapter(ArrayList<TypeBean> dataSet, OnItemClickListener listener){
        localDataSet=dataSet;
        this.listener=listener;
    }

    @NonNull
    @Override
    public BillTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_type,viewGroup,false);
        BillTypeAdapter.ViewHolder viewHolder=new BillTypeAdapter.ViewHolder(view);
        viewHolder.getLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(viewHolder.getBindingAdapterPosition());
                notifyDataSetChanged();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BillTypeAdapter.ViewHolder viewHolder, int position) {
        viewHolder.getImageView().setImageResource(getIconId(position));
        viewHolder.getTextView().setText(localDataSet.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private int getIconId(int position){
        if (localDataSet.get(position).isChecked()){
            return localDataSet.get(position).getIcon();
        }
        else {
            return localDataSet.get(position).getIconGray();
        }
    }

}