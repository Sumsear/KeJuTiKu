package com.example.hp.keju.mvp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.hp.keju.R;
import com.example.hp.keju.mvp.tiku.QuestionActivity;
import com.example.hp.keju.service.QIntentService;


public class WelcomeActivity extends BaseActivity {

    private ImageView imWel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();
        QIntentService.startInitQuestion(WelcomeActivity.this);
    }

    /**
     * TODO 初始化
     */
    private void init() {

        imWel = findViewById(R.id.wel_im);
        imWel.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, QuestionActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }


}
