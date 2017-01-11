package com.turing.eteacher.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * httpclient实例
 * 
 * @author arron
 * @date 2015年11月11日 下午6:36:49
 * @version 1.0
 */
public class HttpClientUtil {
	/**
	 * 模拟请求: 账号登录前
	 * 
	 * @param url
	 *            资源地址
	 * @param map
	 *            参数列表
	 * @param encoding
	 *            编码
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String loginInBeforeSend(String url, Map<String, String> map)
			throws ParseException, IOException {
		String body = "";
		url = "http://localhost:8080/eteacher/remote/" + url;
		String encoding = "utf-8";
		// 创建httpclient对象
		CloseableHttpClient client = HttpClients.createDefault();
		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);
		// 模拟数据
		String appKey = "20161001_ITEACHER";
		String timeStamp = String.valueOf(System.currentTimeMillis());
		String IMEI = "867919024853460";

		String account = map.get("account");
		String pwd = Encryption.encryption(map.get("password"));

		String sign = Encryption.encryption(appKey + account + timeStamp + pwd
				+ IMEI);

		map.put("appKey", appKey);
		map.put("imei", IMEI);
		map.put("timeStamp", timeStamp);
		map.put("sign", sign);
		// 装填参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		// 设置参数到请求对象中
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

		System.out.println("请求地址：" + url);
		System.out.println("请求参数：" + nvps.toString());

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		// 执行请求操作，并拿到结果（同步阻塞）
		CloseableHttpResponse response = client.execute(httpPost);
		// 获取结果实体
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// 按指定编码转换结果实体为String类型
			body = EntityUtils.toString(entity, encoding);
		}
		EntityUtils.consume(entity);
		// 释放链接
		response.close();
		return body;
	}

	/**
	 * 模拟请求: 账号登录后
	 * 
	 * @param url
	 *            资源地址
	 * @param map
	 *            参数列表
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String send(String url, Map<String, String> map)
			throws ParseException, IOException {
		String body = "";
		url = "http://localhost:8080/eteacher/remote/" + url;
		String encoding = "utf-8";
		// 创建httpclient对象
		CloseableHttpClient client = null;
		try {
			client = HttpClients.createDefault();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);
		// 模拟数据
		String timeStamp = String.valueOf(System.currentTimeMillis());
		map.put("timeStamp", timeStamp);
		map.put("appKey", "20161001_ITEACHER");
		map.put("userId", "bI6c4rTArw");
		String token = "aee345d5dae47c1ada7be3ae1234555f";
		String pwd = "e10adc3949ba59abbe56e057f20f883e";
		String token1 = Encryption.encryption(token + pwd);
		// map.put("token", token);
		String signature = Encryption.encryption(token1 + timeStamp);
		map.put("signature", signature);
		// 装填参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		// 设置参数到请求对象中
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

		System.out.println("请求地址：" + url);
		System.out.println("请求参数：" + nvps.toString());

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		// httpPost.setHeader("Content-type",
		// "application/x-www-form-urlencoded");
		// httpPost.setHeader("User-Agent",
		// "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		//
		CloseableHttpResponse response = null;
		// 执行请求操作，并拿到结果（同步阻塞）
		try {
			response = client.execute(httpPost);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// 获取结果实体
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// 按指定编码转换结果实体为String类型
			body = EntityUtils.toString(entity, encoding);
		}
		EntityUtils.consume(entity);
		// 释放链接
		response.close();
		return body;
	}
}