package com.weixin.util;

import java.security.MessageDigest;

public class MD5Util {

	public static String md5(String source) {

		StringBuffer sb = new StringBuffer(32);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(source.getBytes("utf-8"));

			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
			}
		} catch (Exception e) {
			LoggerHandle.handle("Can not encode the string '" + source + "' to MD5!", e);
			return null;
		}

		return sb.toString();
	}
}
