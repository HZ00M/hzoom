package com.hzoom.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtil {
    /**
     * 
     * <p>
     * Description: 压缩消息
     * </p>
     * 
     * @param msg
     * @return
     * @author wgs
     * @throws IOException
     * @date 2019年4月7日 上午11:30:41
     *
     */
    public static byte[] compress(byte[] msg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        byte[] ret;
        try {
            gzip.write(msg);
            gzip.finish();
            ret = bos.toByteArray();
        } finally {
            gzip.close();
            bos.close();
        }
        return ret;
    }

    /**
     * 
     * <p>
     * Description:解压消息
     * </p>
     * 
     * @param msg
     * @return
     * @author wgs
     * @throws IOException
     * @date 2019年4月7日 上午11:30:51
     *
     */
    public static byte[] decompress(byte[] msg) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(msg);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] ret;
        try {
            byte[] buf = new byte[1024];
            int num = -1;
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, num);
            }
            ret = bos.toByteArray();
            bos.flush();
        } finally {
            gzip.close();
            bis.close();
            bos.close();
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        String str = "wsfsdfsdfdsfdsfsxcvcxvvvvvvvvv     afsdfsdfdsffsdfafsdfasdfaasfasdfsdfdsfvbvbxfdscvcxvfdgvxcvxcvx vbvbbcv    cvbvbxvxcvxc";
        byte[] value = str.getBytes();
        System.out.println("压缩前：" + value.length);
        byte[] value2 = compress(value);
        System.out.println("压缩后：" + value2.length);

        String msg = new String(decompress(value2));
        System.out.println(msg.equals(str));
    }
}
