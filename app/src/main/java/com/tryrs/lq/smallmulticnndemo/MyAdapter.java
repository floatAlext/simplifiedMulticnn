package com.tryrs.lq.smallmulticnndemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class MyAdapter extends BaseAdapter {
    LinkedList<String> names;
    LinkedList<String>address;
    Context context;
    MyAdapter(LinkedList<String> names, LinkedList<String> address, Context context){
        this.names=names;
        this.address=address;
        this.context=context;
    }
    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layoutView;
        if(convertView==null){
            layoutView=View.inflate(context,R.layout.listview,null);
        }else layoutView=convertView;
        TextView nameTv=(TextView) layoutView.findViewById(R.id.tv_name);
        TextView addressTv=(TextView)layoutView.findViewById(R.id.tv_address);
        //ImageView imageView=(ImageView)layoutView.findViewById(R.id.im_img);
        if(names.size()>=1 && position<names.size()) {
            nameTv.setText(names.get(position));
            addressTv.setText(address.get(position));

        }
        return layoutView;

    }
}
