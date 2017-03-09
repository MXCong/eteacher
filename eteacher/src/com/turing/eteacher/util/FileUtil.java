package com.turing.eteacher.util;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.turing.eteacher.model.CustomFile;

public class FileUtil {

	public static String getRootPath() {
		return FileUtil.class.getResource("/").getPath()
				.replace("WEB-INF/classes", "");
	}

	public static String getFileStorePath() {
		return FileUtil.class.getResource("/").getPath()
				.replace("WEB-INF/classes", "")
				+ "/upload/";
	}

	public static String getSuffixes(String fileName) {
		String suffixes = "";
		if (StringUtil.isNotEmpty(fileName) && fileName.indexOf(".") != -1) {
			suffixes = fileName.substring(fileName.lastIndexOf("."));
		}
		return suffixes;
	}

	public static CustomFile genCustomFile(MultipartFile file, String dataId)
			throws IllegalStateException, IOException {
		CustomFile customFile = null;
		if (!file.isEmpty()) {
			String pathRoot = FileUtil.getRootPath();
			String serverName = CustomIdGenerator.generateShortUuid()
					+ FileUtil.getSuffixes(file.getOriginalFilename());
			String path = "/upload/" + serverName;
			file.transferTo(new File(pathRoot + path));
			customFile = new CustomFile();
			customFile.setDataId(dataId);
			customFile.setFileName(file.getOriginalFilename());
			customFile.setServerName(serverName);
		}
		return customFile;
	}

	public static String getUploadPath() {
		// 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
		// String savePath =
		// request.getServletContext().getRealPath("/WEB-INF/upload");
		// File filesavePath = new File(savePath);
		// if (!filesavePath.exists()) {
		// //创建临时目录
		// filesavePath.mkdir();
		// }
		String savePath = "D://upload";
		// String savePath = "/usr/local/eteacherFile";
		File filesavePath = new File(savePath);
		if (!filesavePath.exists()) {
			filesavePath.mkdir();
		}
		return savePath;
	}

	/**
	 * 获取Tomcat的url和端口号
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		return request.getRequestURL().substring(0,
				request.getRequestURL().indexOf("8080") + 5)
				+ "download/";
	}

	/**
	 * @Method: makeFileName
	 * @Description: 生成上传文件的文件名，文件名以：uuid+"_"+文件的原始名称
	 * @author lifei
	 * @param filename
	 *            文件的原始名称
	 * @return uuid+"_"+文件的原始名称
	 */
	public static String makeFileName(String filename) { // 2.jpg
		// 为防止文件覆盖的现象发生，要为上传文件产生一个唯一的文件名
	 
		String lastName=getSuffixes(filename);
		return CustomIdGenerator.generateShortUuid() + "." + lastName;
	}

	/**
	 * 为防止一个目录下面出现太多文件，要使用hash算法打散存储
	 * 
	 * @Method: makePath
	 * @Description:
	 * @author lifei
	 * @param filename
	 *            文件名，要根据文件名生成存储目录
	 * @param savePath
	 *            文件存储路径
	 * @return 新的存储目录
	 */
	public static String makePath(String filename, String savePath) {
		// 得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
		int hashcode = filename.hashCode();
		int dir1 = hashcode & 0xf; // 0--15
		int dir2 = (hashcode & 0xf0) >> 4; // 0-15
		// 构造新的保存目录
		String dir = savePath + "\\" + dir1 + "\\" + dir2; // upload\2\3
															// upload\3\5
		// File既可以代表文件也可以代表目录
		File file = new File(dir);
		// 如果目录不存在
		if (!file.exists()) {
			// 创建目录
			file.mkdirs();
		}
		return dir;
	}
}
