package com.turing.eteacher.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

public class StringUtil {

	public static boolean isNotEmpty(String str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return false;
		}
		return true;
	}

	public static String getBigDecimalStr(BigDecimal num) {
		if (num == null) {
			return "";
		}
		String str = num.toString();
		while ((str.indexOf(".") > -1 && str.endsWith("0"))
				|| str.endsWith(".")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public static String joinByList(List<String> list, String prex) {
		String str = "";
		for (String s : list) {
			str += s + prex;
		}
		if (str.endsWith(prex)) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 检查字符串数组中是否有空字符串
	 * 
	 * @author lifei
	 * @param params
	 * @return
	 */
	public static boolean checkParams(String... params) {
		for (int i = 0; i < params.length; i++) {
			if (!isNotEmpty(params[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 两个数相除获取百分数
	 * 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static String getPercent(String arg1, String arg2) {
		if (checkParams(arg1, arg2)) {
			// 这里的数后面加“D”是表明它是Double类型，否则相除的话取整，无法正常使用
			double percent = Double.valueOf(arg1 + "") / Double.valueOf(arg2 + "");
			// 输出一下，确认你的小数无误
			System.out.println("小数：" + percent);
			// 获取格式化对象
			NumberFormat nt = NumberFormat.getPercentInstance();
			// 设置百分数精确度2即保留两位小数
			nt.setMinimumFractionDigits(2);
			// 最后格式化并输出
			return nt.format(percent);
		} else {
			return null;
		}
	}
}
