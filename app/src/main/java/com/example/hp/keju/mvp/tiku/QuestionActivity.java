package com.example.hp.keju.mvp.tiku;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hp.keju.R;
import com.example.hp.keju.adapter.AnswerAdapter;
import com.example.hp.keju.callback.PermissionCallback;
import com.example.hp.keju.constant.Constants;
import com.example.hp.keju.entity.OCRResultEntity;
import com.example.hp.keju.mvp.BaseActivity;
import com.example.hp.keju.ocr.camera.CameraActivity;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.util.FileUtil;
import com.example.hp.keju.util.LogUtil;
import com.example.hp.keju.ocr.RecognizeService;
import com.example.hp.keju.util.NetworkUtil;
import com.example.hp.keju.util.NotificationHelper;
import com.example.hp.keju.util.UpdateUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends BaseActivity implements QuestionContract.View {

    private final static int GET_QUESTION_SUCCESS = 0x0001;
    private final static int GET_QUESTION_DEFAULT = 0x0002;
    private final static int GET_QUESTION_ERROR = 0x0003;
    private static final int REQUEST_CODE_GENERAL_BASIC = 106;

    private Toolbar tbTitle;
    private TextView tvNotifacation;
    private RecyclerView rvAnswer;
    private EditText etQuestion;
    private Button btnSearch;
    private AnswerAdapter adapter;

    private QuestionContract.Presenter mPresenter;
    private ProgressDialog pd;
    private volatile String mUpdateMessage;

    private PermissionCallback mCallBack = new PermissionCallback() {
        @Override
        public void granted(int code, String permission) {
            switch (permission) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    updateApplication();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void denied(int code, String permission) {
            showToast("施主，请进入设置页给定权限哦！");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

//        requestPermission(Manifest.permission.READ_PHONE_STATE, 1000, mCallBack);

        new QuestionPresenter(this);

        initView();
        initDate();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 识别成功回调，通用文字识别
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == RESULT_OK) {
            RecognizeService.recGeneralBasic(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            LogUtil.e(result);
                            OCRResultEntity entity = new Gson().fromJson(result, OCRResultEntity.class);
                            showListDialog(entity.getWordsResult());
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_init, menu);
        return true;
    }

    @Override
    public Application getApp() {
        return getApplication();
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            pd.show();
        } else {
            pd.dismiss();
        }
    }

    @Override
    public void showQuestions(List<QuestionEntity> questions) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putParcelableArrayList("questions", (ArrayList<? extends Parcelable>) questions);
        msg.what = GET_QUESTION_SUCCESS;
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    @Override
    public void displayNotificationView(String content, boolean display) {
        if (display) {
            if (!TextUtils.isEmpty(content)) tvNotifacation.setText(content);
            mUpdateMessage = content;
            tvNotifacation.setVisibility(View.VISIBLE);
        } else {
            tvNotifacation.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPresenter(QuestionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void handleMessage(Activity activity, Message msg) {
        if (activity instanceof QuestionActivity) {
            QuestionActivity ac = (QuestionActivity) activity;
            switch (msg.what) {
                case GET_QUESTION_SUCCESS:
                    List<QuestionEntity> questions = msg.getData().getParcelableArrayList("questions");
                    ac.adapter.setData(questions);
                    ac.pd.dismiss();
                    break;
                case GET_QUESTION_DEFAULT:
                    ac.pd.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void showToast(String msg) {
        super.showToast(msg);
    }

    /**
     * TODO 初始化 VIEW
     */
    private void initView() {

        tbTitle = findViewById(R.id.main_title);
        tbTitle.setTitle(R.string.title);
        tbTitle.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(tbTitle);
        tbTitle.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_init:
                        if (NetworkUtil.checkNetwork(getApplicationContext())) {
//                            mPresenter.initQuestions();
                            mPresenter.getQuestionsByDuoWan("天策");
                        } else {
                            showToast("施主，您的手机该交网费了！");
                        }
                        break;
                }
                return true;
            }
        });

        tvNotifacation = findViewById(R.id.tv_notification);
        tvNotifacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(mUpdateMessage);
            }
        });

        etQuestion = findViewById(R.id.et_q);

        //计划使用BMOB,但是BMOB内部使用的RXJava+Retrofit所以本项目中请求网站直接用系统原生的HttpUrlConnection
        btnSearch = findViewById(R.id.btn_s);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(QuestionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
            }
        });

        rvAnswer = findViewById(R.id.rv_a);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayout.VERTICAL);
        rvAnswer.setLayoutManager(lm);
        rvAnswer.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        adapter = new AnswerAdapter();
        rvAnswer.setAdapter(adapter);

        pd = new ProgressDialog(this);
        pd.setMessage("正在加载，请稍后...");
    }

    /**
     * TODO 初始化数据
     */
    private void initDate() {
        mPresenter.checkUpdate();
    }

    /**
     * TODO 显示识别结果到的
     *
     * @param data 识别结果列表
     */
    private void showListDialog(List<String> data) {

        if (data == null) return;

        final String[] arr = new String[data.size()];
        final boolean[] isCheck = new boolean[data.size()];
        data.toArray(arr);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMultiChoiceItems(arr, isCheck, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String result = arr[which];
                LogUtil.e(result);
                etQuestion.setText(result);
                mPresenter.getQuestionsByLocal(result);
                dialog.dismiss();
            }
        });

        builder.setTitle("请选择与题目一致的选项！");
        builder.create().show();
    }

    /**
     * TODO xianshi
     *
     * @param message 更新信息
     */
    private void showUpdateDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("应用更新");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtil.e("点击权限");
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001, mCallBack);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //关闭提示窗口
                displayNotificationView("", false);
            }
        });
        builder.setMessage(message);
        builder.create().show();
    }

    private void updateApplication() {
        final NotificationHelper helper = new NotificationHelper(QuestionActivity.this);
        helper.setTitle(QuestionActivity.this.getString(R.string.app_name));
        helper.setContent("下载应用");
        helper.setMaxProgress(100);

        //更新APP
        UpdateUtil.Builder build = new UpdateUtil.Builder();
        build.setUrl("http://codown.youdao.com/dictmobile/youdaodict_android_youdaoweb.apk");
        String path = Constants.DOWNLOAD;
        LogUtil.e(path);
        build.setPath(path);
        build.setFileName(getString(R.string.app_name) + ".apk");
        build.setListener(new UpdateUtil.DownloadListener() {
            @Override
            public void onStart() {
                LogUtil.e("开始");
            }

            @Override
            public void onProgress(int progress) {
                helper.notification(progress);
            }

            @Override
            public void onDone(File file) {
                helper.destroyed();

                //调用安装应用
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(QuestionActivity.this, "com.xzh.jskjapplication.fileprovider", file);
                    //对目标应用临时授权该Uri所代表的文件
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(file);
                }
                install.setDataAndType(uri, "application/vnd.android.package-archive");
                QuestionActivity.this.startActivity(install);
            }

            @Override
            public void onFailure(String msg) {
                LogUtil.e(msg);
                helper.destroyed();
            }
        });
        build.build().execute();
    }
}
