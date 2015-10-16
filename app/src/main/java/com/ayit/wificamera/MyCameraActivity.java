package com.ayit.wificamera;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.ayit.threads.SendBitampThread;
import com.ayit.threads.SendYuLanBitampThread;
import com.ayit.utils.FileUtil;
import com.ayit.utils.ImageUtil;

public class MyCameraActivity extends Activity {
    private SurfaceView sView;
    private SurfaceHolder surfaceHolder;
    private static Button bt_paizhao;
    private static Button bt_close;
    private int screenWidth, screenHeight;
    private Camera camera = null; //
    private boolean isPreview = false; //
    YuvImage image = null;
    private long exitTime;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏幕
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        bt_paizhao = (Button) findViewById(R.id.btn_shutter);
        bt_paizhao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                doTakePicture();
            }
        });
        bt_close = (Button) findViewById(R.id.btn_close);
        bt_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //销毁
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                }
            }
        });
        screenWidth = 640;
        screenHeight = 480;
        sView = (SurfaceView) findViewById(R.id.sView); //
        surfaceHolder = sView.getHolder(); //
        surfaceHolder.addCallback(new Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    if (isPreview)
                        camera.stopPreview();
                    camera.release();
                    camera = null;
                }
                System.exit(0);
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initCamera() {
        if (!isPreview) {
            if (camera == null) {
                camera = Camera.open();
                camera.setDisplayOrientation(90);
            }

        }
        if (camera != null && !isPreview) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewFpsRange(15, 20); // ֡
                parameters.setPictureFormat(ImageFormat.NV21); //
                // camera.setParameters(parameters); //
                // android2.3.3
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setJpegQuality(100);
                parameters.setJpegThumbnailQuality(100);
                parameters.setPictureSize(screenWidth, screenHeight); //
                camera.autoFocus(null);
                camera.setPreviewDisplay(surfaceHolder); //
                camera.setPreviewCallback(new StreamIt()); //
                camera.startPreview(); //

            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    /**
     *
     */
    public void doTakePicture() {
        if (isPreview && (camera != null)) {
            camera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    ShutterCallback mShutterCallback = new ShutterCallback() {
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };
    PictureCallback mRawCallback = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            ArrayList<String> connectedIP = MainActivity.getConnectedIP();
            for (String ip : connectedIP) {
                if (ip.contains(".")) {
                    new SendBitampThread(ip, data).start();
                }
            }
            try {
                if (null != data) {
                    Bitmap b = null;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 3;
                    b = BitmapFactory.decodeByteArray(data, 0, data.length,
                            options);
                    if (b.getHeight() < b.getWidth()) {
                        Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                        FileUtil.saveBitmap(rotaBitmap);
                        rotaBitmap = null;
                    } else {
                        FileUtil.saveBitmap(b);
                    }
                    b = null;
                    data = null;
                    new Thread(new Runnable() {

                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                camera.startPreview();
                isPreview = true;
            }

        }
    };

    class StreamIt implements Camera.PreviewCallback {
        public StreamIt() {

        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Size size = camera.getParameters().getPreviewSize();

            try {
                image = new YuvImage(data, ImageFormat.NV21, size.width,
                        size.height, null);
                if (image != null) {
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    image.compressToJpeg(
                            new Rect(0, 0, size.width, size.height), 80,
                            outstream);
                    outstream.flush();
                    ArrayList<String> connectedIP = MainActivity
                            .getConnectedIP();
                    for (String ip : connectedIP) {
                        if (ip.contains(".")) {
                            new SendYuLanBitampThread(ip, outstream).start();
                        }
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void kuaimen() {
        // TODO Auto-generated method stub
        bt_paizhao.performClick();
    }

    public static void close() {
        // TODO Auto-generated method stub
        bt_close.performClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(MyCameraActivity.this, MainActivity.class);
                startActivity(intent);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
