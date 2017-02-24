package com.turing.eteacher.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.eteacher.util.PushBody.Role;
import com.turing.eteacher.util.PushBody.SortComb;
import com.turing.eteacher.util.PushBody.SortType;

/**
 * 极光推送
 * 
 * @author lifei
 * 
 */
public class JPushUtils {
	// 在极光注册上传应用的 appKey 和 masterSecret
	private static final String stuAppKey = "b6ea7ba889557cfb16579e3c";// //必填，例如466f7032ac604e02fb7bda89
	private static final String stuMasterSecret = "a8623d40b4007d8cb38c664b";// 必填，每个应用都对应一个masterSecret
	private static final String teachAppKey = "41f45a885410ecfe56138d5f";// //必填，例如466f7032ac604e02fb7bda89
	private static final String teachMasterSecret = "3c3086fe4c212350b2419e38";// 必填，每个应用都对应一个masterSecret
	private static JPushClient stuJpush = null;
	private static JPushClient teachJPush = null;

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
		List<String> tagsList = new ArrayList<>();
		tagsList.add("EjCodTlSKK");
		
		PushBody pBody = new PushBody();
		pBody.setPlatform(com.turing.eteacher.util.PushBody.Platform.all);
		pBody.setRole(Role.student);
		pBody.setSortFlag(tagsList);
		pBody.setSortType(SortType.tag);
		pBody.setSortComb(SortComb.or);
		HashMap<String, Object> extra = new HashMap<>();
		extra.put("noticeId", "SA4m3f5lEg");
		extra.put("flag",0);
		NotifyBody nBody = NotifyBody.getNotifyBody("呃呃",
				"我想问一下",
				1, extra);
//		NotifyBody nBody = NotifyBody.getPassthroughBody(
//				1, extra);
		pBody.setNotifyBody(nBody);
		pushMessage(pBody);
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 */
	public static boolean pushMessage(PushBody message) {
		PushPayload payload = getLoadByPushBody(message);
		if (null != payload) {
			try {
				PushResult result = null;
				switch (message.getRole()) {
				case student:
					result = getStuClient().sendPush(payload);
					System.out.println("Got result - " + result);
					return true;
				case teacher:
					result = getteachClient().sendPush(payload);
					System.out.println("Got result - " + result);
					return true;
				case all:
					result = getStuClient().sendPush(payload);
					result = getteachClient().sendPush(payload);
					return true;
				default:
					break;
				}
			} catch (APIConnectionException e) {
				e.printStackTrace();
				return false;
			} catch (APIRequestException e) {
				if(e.getErrorCode() == 1011){
					e.printStackTrace();
					return true;
				}
				e.printStackTrace();
			}
		}
		return false;
	}

	private static PushPayload getLoadByPushBody(PushBody pushBody) {
		try {
			PushPayload.Builder pBuilder = PushPayload.newBuilder();
			if (null != pushBody.getNotifyBody()) {
				pBuilder.setMessage(Message.content(new ObjectMapper()
						.writeValueAsString(pushBody.getNotifyBody())));
			}
			if (null != pushBody.getPlatform()) {
				switch (pushBody.getPlatform()) {
				case android:
					pBuilder.setPlatform(Platform.android());
					break;
				case iOS:
					pBuilder.setPlatform(Platform.ios());
					break;
				case all:
					pBuilder.setPlatform(Platform.android_ios());
					break;
				default:
					break;
				}
			}
			if (null != pushBody.getSortType()) {
				switch (pushBody.getSortType()) {
				case tag:
					if (null != pushBody.getSortFlag()) {
						if (null != pushBody.getSortComb()) {
							switch (pushBody.getSortComb()) {
							case and:
								pBuilder.setAudience(Audience.tag_and(pushBody.getSortFlag()));
								break;
							default:
								pBuilder.setAudience(Audience.tag(pushBody.getSortFlag()));
								break;
							}
						}else{
							pBuilder.setAudience(Audience.tag(pushBody.getSortFlag()));
						}
					}
					break;
				case id:
					if (null != pushBody.getSortFlag()) {
						pBuilder.setAudience(Audience.registrationId(pushBody.getSortFlag()));
					}
					break;
				default:
					pBuilder.setAudience(Audience.all());
					break;
				}
			} else {
				pBuilder.setAudience(Audience.all());
			}

			return pBuilder.build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}