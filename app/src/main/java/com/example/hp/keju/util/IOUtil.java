package com.example.hp.keju.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

    /**
     * TODO 关闭可关闭对象
     *
     * @param closeable Closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO 关闭输入流
     *
     * @param is InputStream
     */
    public static void closeInputStream(InputStream is) {
        close(is);
    }


    /**
     * TODO 关闭输出流
     *
     * @param os OutputStream
     */
    public static void closeOutputStream(OutputStream os) {
        close(os);
    }
}
