package com.example.hp.keju.util;


import com.example.hp.keju.callback.RequestCallBack;
import com.example.hp.keju.entity.QuestionEntity;
import com.example.hp.keju.entity.SearchEntity;
import com.google.gson.Gson;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static HttpUtil instance;
    private ExecutorService executor = new ThreadPoolExecutor(3, 6, 15, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

    private HttpUtil() {

    }

    public static HttpUtil getInstance() {
        if (instance == null) {
            synchronized (HttpUtil.class) {
                if (instance == null) instance = new HttpUtil();
            }
        }
        return instance;
    }


    public void get(final String urlAddress, final RequestCallBack<List<QuestionEntity>> callBack) {

        executor.execute(new Runnable() {
            @Override
            public void run() {

                StringBuilder sb = new StringBuilder();
                InputStreamReader is = null;
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlAddress);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Language", "zh-CN");
                    conn.setRequestProperty("author", "evilwk");
                    conn.setRequestMethod("GET");
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        is = new InputStreamReader(conn.getInputStream());
                        char[] buff = new char[1024];
                        int len = 0;
                        int flag = 0;
                        while ((len = is.read(buff, len, 10)) != -1) {
                            sb.append(new String(buff, flag, 10));
                            flag = len;
                        }
                        String result = sb.toString().substring(sb.toString().indexOf("{"), sb.lastIndexOf("}") + 1);
                        LogUtil.e("result", result);
                        callBack.success(code, analyse(result));
                    } else {
                        callBack.defeated(code, conn.getResponseMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeInputStream(is);
                    disconnect(conn);
                }
            }
        });
    }

    /**
     * TODO 解析结果
     *
     * @param result json
     * @return list of result
     */
    private List<QuestionEntity> analyse(String result) {

        List<QuestionEntity> questions = new ArrayList<>();
        try {
            SearchEntity entity = new Gson().fromJson(result, SearchEntity.class);
            if ("ok".equals(entity.getStatus())) {
                questions.addAll(entity.getList());
                for (int i = 0; i < questions.size(); i++) {
                    QuestionEntity e = questions.get(i);
                    LogUtil.e("question", e.getQ());
                    LogUtil.e("answer", e.getA());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }


    /**
     * TODO 注销
     */
    public void destroyed() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            executor = null;
        }
    }


    /**
     * TODO 关闭输入流
     *
     * @param is 输入流
     */
    private void closeInputStream(InputStreamReader is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO 关闭输出流
     *
     * @param os 输出流
     */
    private void closeOutputStream(OutputStreamWriter os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO 关闭HttpUrlConnection网络连接
     *
     * @param conn HttpUrlConnection
     */
    private void disconnect(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }
}
