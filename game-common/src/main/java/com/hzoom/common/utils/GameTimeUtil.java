package com.hzoom.common.utils;

import org.apache.commons.lang.time.DateFormatUtils;

public class GameTimeUtil {
    /**
     * 日期格式：yyyy-MM-dd HH:mm:ss
     */
    public final static String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getStringDate(long millTime) {
        String value = DateFormatUtils.format(millTime, NORMAL_DATE_FORMAT);
        return value;
    }
}
