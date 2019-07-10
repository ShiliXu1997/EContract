package com.google.zxing.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.android.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.decoding.RGBLuminanceSource;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;


/**
 * Initial the camera
 */
public class CaptureActivity extends AppCompatActivity implements Callback {

    private static final int REQUEST_CODE_SCAN_GALLERY = 100;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private ImageView back;
    private TextView add;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private String photo_path;
    private boolean isOpen;
    private android.hardware.Camera.Parameters parameter;
    private Bitmap scanBitmap;
    private android.hardware.Camera camera;
    private Button flash;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        /**访问照片权限
         *
         */
        final int checked = ContextCompat.checkSelfPermission(CaptureActivity.this
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checked == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(CaptureActivity.this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_content);

        //退出按钮
        back =  findViewById(R.id.scanner_toolbar_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //闪光灯按钮
        flash=findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightOn();
            }
        });

        //添加相册图片

        add = findViewById(R.id.scanner_toolbar_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限申请
                final int checked = ContextCompat.checkSelfPermission(CaptureActivity.this
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (checked == PackageManager.PERMISSION_GRANTED) {
                    open_picture();
                } else {
                    ActivityCompat.requestPermissions(CaptureActivity.this
                            , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

            }
        });

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);


    }

    //打开图片
    private void open_picture(){
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "请选择二维码图片");
        CaptureActivity.this.startActivityForResult(wrapperIntent, REQUEST_CODE_SCAN_GALLERY);
    }

    /**
     *闪光灯控制
     */
    private void lightOn(){
        camera = CameraManager.getCamera();
        System.out.println("begin");
        parameter = camera.getParameters();
        if (!isOpen) {
            parameter.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
            isOpen = true;
        } else {  // 关灯
            parameter.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
            isOpen = false;
        }
    }


    /**
     * 这里是扫描相册图片的处理方法
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    //获取选中图片的路径
//                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);

                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();
                    //扫描结果
                    Result result = scanningImage(photo_path);

                    jumpToNewActivity(result.getText().toString());

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /**
     * 扫描二维码图片的方法
     * 压缩后进行扫描
     */
    public Result scanningImage(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 这里是用相机扫描的处理方法
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();

        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            jumpToNewActivity(resultString);
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void jumpToNewActivity(String qrCode) {
        System.out.println("二维码:"+qrCode);
        Intent intent;

        //判断该二维码是否是新设备登录用
        if(qrCode.substring(0,6).equals("qrCode")) {
            //去掉二维码串的标识前缀
            qrCode = qrCode.substring(6);
            System.out.println("截取后的qrCode:"+qrCode);
            //跳转到授权新设备的ConfirmActivity
            intent = new Intent(CaptureActivity.this, com.example.android.ConfirmActivity.class);
        } else {
            intent = new Intent(CaptureActivity.this, com.example.android.WebsignActivity.class);
        }
        Bundle data=new Bundle();
        data.putString("qrCode",qrCode);
        intent.putExtras(data);
        startActivity(intent);
    }
}