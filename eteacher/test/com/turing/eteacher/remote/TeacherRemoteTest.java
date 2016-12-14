package com.turing.eteacher.remote;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.junit.Test;

import com.turing.eteacher.util.HttpClientUtil;


public class TeacherRemoteTest {

	@Test
	public void testGetPersonInfo() throws ParseException, IOException {
		String url = "teacher/personInfo";
		Map<String, String> map = new HashMap<String, String>();
		map.put("teacherId", "QkIZWN3ih8");
		String body = HttpClientUtil.send(url, map);
		System.out.println("响应结果：");
		System.out.println(body);
		System.out.println("-----------------------------------");

	}

	@Test
	public void testEditPersonInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCourseList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCommunicationList() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddCommunication() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelCommunication() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWorkday() {
		fail("Not yet implemented");
	}

}
