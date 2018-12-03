package com.example.hp.keju.util;

public class StringUtil {

    public final static String switchPunctuation(String str) {

        if (str.contains(",")) {
            str = str.replaceAll(",", "，");
        }

        if (str.contains("?")) {
            str = str.replaceAll("\\?", "？");
        }

        if (str.contains(":")) {
            str = str.replaceAll(":", "：");
        }

        if (str.contains(";")) {
            str = str.replaceAll(";", "；");
        }

        if (str.contains("!")) {
            str = str.replaceAll("!", "！");
        }

        if (str.contains(".")) {
            str = str.replaceAll("\\.", "。");
        }

        return str;
    }
}
