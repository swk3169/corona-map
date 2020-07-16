package com.example.coronamap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.coronamap.model.BulletinboardModel;
import com.example.coronamap.R;

import java.util.ArrayList;

public class ListviewAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<BulletinboardModel> sample;

    public ListviewAdapter(Context context, ArrayList<BulletinboardModel> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public BulletinboardModel getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.custom_listview, null);

        TextView title = (TextView) view.findViewById(R.id.tv_title);
        TextView date = (TextView) view.findViewById(R.id.tv_date);
        TextView writer = (TextView)view.findViewById(R.id.tv_writer);

        title.setText(sample.get(position).getTitle());
        date.setText(sample.get(position).getDate());
        writer.setText(sample.get(position).getId());

        return view;
    }
}
