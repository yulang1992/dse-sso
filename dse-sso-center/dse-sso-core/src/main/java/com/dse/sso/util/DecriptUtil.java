package com.dse.sso.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 加解密工具
 * 
 * @author yulang
 */

public class DecriptUtil {

	private final static Logger logger = LoggerFactory.getLogger(DecriptUtil.class);
	private static final DecriptUtil DECRIPT_UTIL = new DecriptUtil();
	private SecretKeySpec key = null;

	private DecriptUtil() {
		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("init KeyGenerator.getInstance failed.");
		}
		kgen.init(128, new SecureRandom("www.noarter.com_web_socket_token".getBytes()));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		this.key = key;
	}

	public static DecriptUtil getInstance() {
		return DECRIPT_UTIL;
	}

	public static String SHA1(String decript) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			logger.warn(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * sha
	 * 
	 * @param decript
	 * @return
	 */
	public static String SHA(String decript) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.warn(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * md5
	 * 
	 * @param input
	 * @return
	 */
	public static String MD5(String input) {
		try {
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(input.getBytes());
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < md.length; i++) {
				String shaHex = Integer.toHexString(md[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.warn(e.getMessage(), e);
		}
		return "";
	}


	public byte[] encryptAES(byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// 创建密码器
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(bytes);// 加密
			return result;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return null;
	}


	public byte[] decryptAES(byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(bytes);// 加密
			return result;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return null;
	}


}
