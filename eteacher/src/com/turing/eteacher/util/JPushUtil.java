package com.turing.eteacher.util;

import java.util.HashMap;
import java.util.Map;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.eteacher.model.PushMessage;
/**
 * 极光推送
 * @author lifei
 *
 */
public class JPushUtil {
	// 在极光注册上传应用的 appKey 和 masterSecret
	private static final String stuAppKey = "5b73c7485fe5534a9262e4c4";// //必填，例如466f7032ac604e02fb7bda89
	private static final String stuMasterSecret = "d0c4ca3f6625e81e16b09850";// 必填，每个应用都对应一个masterSecret
	private static final String teachAppKey = "3ea95ca12ee4a62e3ff58a96";// //必填，例如466f7032ac604e02fb7bda89
	private static final String teachMasterSecret = "05051cb0bb9c5b30ad968edc";// 必填，每个应用都对应一个masterSecret
	private static JPushClient stuJpush = null;
	private static JPushClient teachJPush = null; 
	// 消息内容
	public static final String ALERT = "JPush Test - alert";
	
	public static String SHOW_ON = "1"; 
	public static String SHOW_OFF = "0"; 
	public static String ACTION_NOTICE_DETAIL = "1"; 
	public static String ACTION_HOMEWORK_DETAIL = "2"; 
	public static String ACTION_SIGNIN_DETAIL = "3"; 
	public static String ACTION_ALERT = "4"; 

	public static JPushClient getStuClient() {
		if (null == stuJpush) {
			stuJpush = new JPushClient(stuMasterSecret, stuAppKey);
		}
		return stuJpush;
	}
	public static JPushClient getteachClient() {
		if (null == teachJPush) {
			teachJPush = new JPushClient(teachMasterSecret, teachAppKey);
		}
		return teachJPush;
	}
	
	public static void main(String[] args) {
		PushMessage message = new PushMessage();
		message.setAction(ACTION_NOTICE_DETAIL);
		message.setContent("消息内容打发第三方了解阿斯蒂芬骄傲了打发斯蒂芬拉萨的房间里的撒！");
		message.setTitle("消息标题发第三方了解阿斯蒂芬骄傲了打发斯蒂芬拉萨的房间里的撒！");
		//message.setSchoolId("4");
		//message.setClassId("p74GYIXJs1s");
		//message.setUserId("BLx00leujb");
		message.setShow(SHOW_ON);
		message.setUserType(PushMessage.UTYPE_TEACHER);
		Map map = new HashMap<>();
		map.put("noticeId", "1DQXtG2pWN");
		map.put("flag", 1);
		message.setExtra(map);
		pushMessage(message);
		//testMessage();
	}
	
	  public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
	        return PushPayload.newBuilder()
	                .setPlatform(Platform.android_ios())
                    .setAudience(Audience.newBuilder()
                    .addAudienceTarget(AudienceTarget.tag("p74GYIXJss"))
                    .build())
	                .setMessage(Message.newBuilder()
	                        .setMsgContent("消息内容是打发斯蒂芬！")
	                        .addExtra("from", "JPush")
	                        .build())
	                .build();
	    }
	
	public static void testMessage(){
		PushPayload payload = buildPushObject_ios_audienceMore_messageWithExtras();
	    try {
	        PushResult result = getStuClient().sendPush(payload);
	        System.out.println("Got result - " + result);

	    } catch (APIConnectionException e) {
	    	System.out.println("Connection error, should retry later");

	    } catch (APIRequestException e) {
	        System.out.println("HTTP Status: " + e.getStatus());
	        System.out.println("Error Code: " + e.getErrorCode());
	        System.out.println("Error Message: " + e.getErrorMessage());
	    }
	}
	
	/**
	 * 发送消息
	 * @param message
	 */
	public static void pushMessage(PushMessage message) {
		PushPayload payload = buildPushMessage(message);
		if (null != payload) {
			try {
				PushResult result = null;
				switch (message.getUserType()) {
				case PushMessage.UTYPE_STUDENT:
					result = getStuClient().sendPush(payload);
					System.out.println("Got result - " + result);
					break;
				case PushMessage.UTYPE_TEACHER:
					result = getteachClient().sendPush(payload);
					System.out.println("Got result - " + result);
					break;
				case PushMessage.UTYPE_ALL:
					result = getStuClient().sendPush(payload);
					result = getteachClient().sendPush(payload);
					break;
				default:
					break;
				}
			} catch (APIConnectionException e) {
				System.out.println("Connection error, should retry later" + e);
			} catch (APIRequestException e) {
				System.out.println("Should review the error, and fix the request"+ e);
				System.out.println("HTTP Status: " + e.getStatus());
				System.out.println("Error Code: " + e.getErrorCode());
				System.out.println("Error Message: " + e.getErrorMessage());
			}
		}
	}

	/**
	 * 向所有平台推送消息
	 * @param message
	 * @return
	 */
	public static PushPayload buildPushMessage(PushMessage message) {
			try {
				System.out.println("message:"+new ObjectMapper().writeValueAsString(message));
				return PushPayload.messageAll(new ObjectMapper().writeValueAsString(message));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
	}
	/**
	 * 快捷地构建推送对象：所有平台，所有设备的通知。
	 * 
	 * @param content
	 *            内容
	 * @return
	 */
	public static PushPayload buildPushObject_all_alert(String content) {
		return PushPayload.alertAll(content);
	}

	/**
	 * 构建推送对象：平台是 Android
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param tag
	 *            设备标签
	 * @return
	 */
	public static PushPayload buildMessage_android_tag_alert(String title,
			String content, String tag) {
		return PushPayload.newBuilder().setPlatform(Platform.android())
				.setAudience(Audience.tag(tag))
				.setNotification(Notification.android(content, title, null))
				.build();
	}

	/**
	 * 构建推送对象：平台是 iOS
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param tag
	 *            设备标签
	 * @return
	 */
	public static PushPayload buildPushObject_ios_tag_alert(String title,
			String content, String tag) {
		return PushPayload.newBuilder().setPlatform(Platform.ios())
				.setAudience(Audience.tag(tag))
				.setNotification(Notification.android(content, title, null))
				.build();
	}
}