package com.example.hp.keju.http;


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * TODO 管理HttpUrlConnection
 */
public class HttpConnection implements Closeable {

    private HttpURLConnection mConn;

    public HttpConnection(HttpURLConnection conn) {
        this.mConn = conn;
    }

    /**
     * TODO 获取http请求头
     *
     * @return http请求头
     */
    public Map<String, List<String>> getHeaders() {
        return mConn.getHeaderFields();
    }

    /**
     * TODO 获取响应码
     *
     * @return 响应码
     * @throws IOException
     */
    public int getResponseCode() throws IOException {
        return mConn.getResponseCode();
    }

    /**
     * TODO 获取输入流
     *
     * @return InputStream
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        return mConn.getInputStream();
    }

    /**
     * TODO 获取输出流
     *
     * @return OutputStream
     * @throws IOException
     */
    public OutputStream getOutPutStream() throws IOException {
        return mConn.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        if (mConn != null) {
            mConn.disconnect();
        }
    }
}
