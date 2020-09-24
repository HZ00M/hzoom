package com.example.demo.rsa;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

public class Base64Utils {
	private static final Base64 BASE64 = new Base64();
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	public static String encode(byte [] source){
		if(source !=null && source.length >0){
			byte[] bytes = BASE64.encode(source);			 
			return new String(bytes,DEFAULT_CHARSET);
		}
		return new String(source,DEFAULT_CHARSET);
	}
	
	public static byte[] decode(String source){
		if(!isEmpty(source)){
			return BASE64.decode(source.getBytes(DEFAULT_CHARSET));
		}
		return source.getBytes(DEFAULT_CHARSET);
	}
	
	private static boolean isEmpty(String str){
		return str == null || str.length() == 0;
	}
	
}
