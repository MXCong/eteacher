package com.turing.eteacher.remote;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.junit.Test;

import com.turing.eteacher.util.HttpClientUtil;

public class UserRemoteTest {

	@Test
	public void testLogin() throws ParseException, IOException {
		String url = "login";
		Map<String, String> map = new HashMap<String, String>();
		map.put("account", "15631223549");
		map.put("password", "asdfgh");
		String body = HttpClientUtil.loginInBeforeSend(url, map);
		System.out.println("响应结果：");
		System.out.println(body);
		System.out.println("-----------------------------------");
	}

	@Test
	public void testLoginout() throws ParseException, IOException {
		String url = "logout";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", "QkIZWN3ih8");
		String body = HttpClientUtil.send(url, map);
		System.out.println("响应结果：");
		System.out.println(body);
		System.out.println("-----------------------------------");
	}

	@Test
	public void testVerifycode() {
		fail("Not yet implemented");
	}

	@Test
	public void Register() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatePassword() {
		fail("Not yet implemented");
	}

	@Test
	public void testValidate() {
		fail("Not yet implemented");
	}

	@Test
	public void testRecovered() {
		fail("Not yet implemented");
	}

}
