package com.ayit.bean;

import java.io.File;
import java.util.ArrayList;

import com.ayit.wificamera.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MyGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Photos> datas;

    public MyGridAdapter(Context context, ArrayList<Photos> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_photo, null);
            holder = new Holder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.image.setImageBitmap(datas.get(position).getBitmap());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("删除")
                        .setMessage("是否要删除该图片？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File file = new File(datas.get(position).getPath());
                                file.delete();
                                datas.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();


            }
        });
        return convertView;
    }

    class Holder {
        ImageView image;
        ImageView delete;
    }
}
