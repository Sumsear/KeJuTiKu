package com.example.hp.keju.mvp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.hp.keju.R;
import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.http.HttpUtil;
import com.example.hp.keju.mvp.tiku.QuestionActivity;
import com.example.hp.keju.service.QIntentService;
import com.example.hp.keju.util.LogUtil;
import com.example.hp.keju.util.NetworkUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class WelcomeActivity extends BaseActivity {

    private ImageView imWel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        init();

        //检测网络是否可用
        if (NetworkUtil.checkNetwork(this)) {
            //网络可用则开启 服务加载试题
            QIntentService.startInitQuestion(WelcomeActivity.this);
//            HttpUtil.get("http://tool.duowan.com/jx3/ui/exam/ex.php")
//                    .params("s","1")
//                    .params("q","天策")
//                    .params("_",System.currentTimeMillis())
//                    .setTag("test")
//                    .perform(new RequestCallBack<String>() {
//                @Override
//                public void success(int code, String data) {
//                    LogUtil.e(data);
//                }
//
//                @Override
//                public void defeated(int code, String msg) {
//                    LogUtil.e(msg);
//                }
//            });

        }
    }

    /**
     * TODO 初始化
     */
    private void init() {

        imWel = findViewById(R.id.wel_im);
        imWel.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.wel));
        imWel.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, QuestionActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * TODO image size compress
     */
    void imageSizeCompress() {
        //尺寸压缩
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.wel, options);
        int originalW = options.outWidth;
        int originalH = options.outHeight;
        LogUtil.i("originalW = " + originalW + " originalH = " + originalH);

        ViewGroup.LayoutParams p = imWel.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();

        int targetW = dm.widthPixels;
        int targetH = dm.heightPixels;
        LogUtil.i("targetW = " + targetW + " targetH = " + targetH);
        int w = 1;
        if (originalW > targetW) {
            w = originalW / targetW;
        }

        int h = 1;
        if (originalH > targetH) {
            h = originalH / targetH;
        }

        int v = w > h ? h : w;
        if (v <= 0) {
            v = 1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = v;
        Bitmap wel = BitmapFactory.decodeResource(getResources(), R.drawable.wel, options);
        imWel.setImageBitmap(wel);
    }


    /**
     * TODO image quality compress
     */
    void imageQuaityCompress() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap wel = BitmapFactory.decodeResource(getResources(), R.drawable.wel, options);

        //质量压缩 (似乎有点问题) 卡的一逼
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        int quality = 100;
        wel.compress(Bitmap.CompressFormat.JPEG, quality, bo);
        while (bo.toByteArray().length / 1024 > 100 && quality > 0) {
            bo.reset();
            quality -= 5;
            wel.compress(Bitmap.CompressFormat.JPEG, quality, bo);
        }

        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        imWel.setImageBitmap(BitmapFactory.decodeStream(bi));

        try {
            bo.close();
            bi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
