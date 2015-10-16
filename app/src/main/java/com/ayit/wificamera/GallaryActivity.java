package com.ayit.wificamera;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.ayit.bean.MyGridAdapter;
import com.ayit.bean.Photos;
import com.ayit.utils.FileUtil;

/**
 * 相册
 */
public class GallaryActivity extends Activity {
    private ProgressDialog progressDialog;
    private GridView gv;
    private ArrayList<Photos> datas;
    private MyGridAdapter adapter;
    private Handler handler;
    private AlertDialog dialog;
    private ImageView imageView;
    private View view;
    private ImageView ivBack;
    private ImageView ivNext;
    private Bitmap bitmap;
    private int position = 0;
    private float screenWidth = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        HomeActivity.tabWidget.getChildAt(1).setClickable(true);
        progressDialog = new ProgressDialog(GallaryActivity.this);
        progressDialog.setMessage("正在加载图片");
        progressDialog.setCancelable(true);


        gv = (GridView) findViewById(R.id.gv_photos);
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_image, null);
        imageView = (ImageView) view.findViewById(R.id.dialog_iv);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        ivNext = (ImageView) view.findViewById(R.id.iv_next);
        dialog = new AlertDialog.Builder(GallaryActivity.this)
                .setView(view)
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                })
                .create();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        datas = new ArrayList<Photos>();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    datas = (ArrayList<Photos>) msg.obj;
                    Collections.reverse(datas);
                    adapter = new MyGridAdapter(GallaryActivity.this, datas);
                    gv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }

            ;
        };
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                position = i;
                bitmap = BitmapFactory.decodeFile(datas.get(i).getPath());
                imageView.setImageBitmap(bitmap);
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && motionEvent.getX() > screenWidth / 2) {
                            ivNext.performClick();
                        } else {
                            ivBack.performClick();
                        }

                        return false;
                    }
                });
                ivBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position > 0) {
                            position--;
                            bitmap = BitmapFactory.decodeFile(datas.get(position).getPath());
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(getApplicationContext(), "已经到第一张了", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                ivNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position < datas.size() - 1) {
                            position++;
                            bitmap = BitmapFactory.decodeFile(datas.get(position).getPath());
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(getApplicationContext(), "已经到最后一张了", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.show();
            }
        });

    }


    @Override
    protected void onResume() {
        progressDialog.show();
        getDatas();
        super.onResume();
    }

    private void getDatas() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                File dir = new File(FileUtil.initPath());
                if (dir.exists()) {
                    String[] list = dir.list();
                    String path = null;
                    Bitmap bitmap = null;
                    ArrayList<Photos> photos = new ArrayList<Photos>();
                    Photos photo;
                    for (int i = 0; i < list.length; i++) {
                        photo = new Photos();
                        path = FileUtil.initPath() + "/" + list[i];
                        Options options = new Options();
                        options.inSampleSize = 15;
                        bitmap = BitmapFactory.decodeFile(path, options);
                        photo.setPath(path);
                        photo.setBitmap(bitmap);
                        photos.add(photo);
                    }
                    Message message = new Message();
                    message.what = 1;
                    message.obj = photos;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

}
