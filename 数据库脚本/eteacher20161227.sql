/*
Navicat MySQL Data Transfer

Source Server         : UP服务器
Source Server Version : 50634
Source Host           : 60.205.153.22:3306
Source Database       : eteacher

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2016-12-27 13:47:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_app`
-- ----------------------------
DROP TABLE IF EXISTS `t_app`;
CREATE TABLE `t_app` (
  `A_ID` varchar(10) NOT NULL COMMENT 'id',
  `APPKEY` varchar(20) DEFAULT NULL COMMENT 'APPKEY',
  `VALUE` varchar(20) DEFAULT NULL COMMENT '组织/机构/程序',
  `USER_TYPE` char(2) DEFAULT NULL COMMENT '用户类型（01教师02学生）',
  PRIMARY KEY (`A_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_app
-- ----------------------------
INSERT INTO `t_app` VALUES ('1', '20161001_ITEACHER', 'UP教学助手', '01');
INSERT INTO `t_app` VALUES ('2', '20161001_ISTUDENT', 'UP教学助手', '02');

-- ----------------------------
-- Table structure for `t_class`
-- ----------------------------
DROP TABLE IF EXISTS `t_class`;
CREATE TABLE `t_class` (
  `CLASS_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '班级表主键',
  `MAJOR_ID` varchar(10) DEFAULT NULL COMMENT '专业ID',
  `CLASS_NAME` varchar(80) DEFAULT NULL COMMENT '班级名称',
  `GRADE` varchar(4) DEFAULT NULL COMMENT '年级 （如2016）',
  `CLASS_TYPE` varchar(20) DEFAULT NULL COMMENT '类型（研究生、本科、专科、中专、技校）',
  `SCHOOL_ID` varchar(10) DEFAULT NULL COMMENT '该班级所属学校ID',
  `END_TIME` int(6) DEFAULT NULL COMMENT '班级有效时间',
  PRIMARY KEY (`CLASS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_class
-- ----------------------------

-- ----------------------------
-- Table structure for `t_config`
-- ----------------------------
DROP TABLE IF EXISTS `t_config`;
CREATE TABLE `t_config` (
  `KEY` varchar(20) NOT NULL DEFAULT '' COMMENT '配置表主键',
  `VALUE` varchar(80) DEFAULT NULL COMMENT '配置项的值',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_config
-- ----------------------------

-- ----------------------------
-- Table structure for `t_course`
-- ----------------------------
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course` (
  `COURSE_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '课程表主键',
  `TERM_ID` varchar(10) DEFAULT NULL COMMENT '学期ID',
  `COURSE_NAME` varchar(80) DEFAULT NULL COMMENT '课程名称',
  `INTRODUCTION` varchar(400) DEFAULT NULL COMMENT '课程简介',
  `CLASS_HOURS` int(5) DEFAULT NULL COMMENT '学时',
  `MAJOR_ID` varchar(10) DEFAULT NULL COMMENT '专业ID',
  `TEACHING_METHOD_ID` varchar(20) DEFAULT NULL COMMENT '授课方式ID',
  `COURSE_TYPE_ID` varchar(20) DEFAULT NULL COMMENT '课程类型ID',
  `EXAMINATION_MODE_ID` varchar(20) DEFAULT NULL COMMENT '考核方式ID',
  `FORMULA` varchar(40) DEFAULT NULL COMMENT '工作量公式',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '所属用户ID',
  `REMIND_TIME` int(3) DEFAULT NULL COMMENT '课程提醒时间',
  PRIMARY KEY (`COURSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of t_course
-- ----------------------------

-- ----------------------------
-- Table structure for `t_course_cell`
-- ----------------------------
DROP TABLE IF EXISTS `t_course_cell`;
CREATE TABLE `t_course_cell` (
  `CT_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '课程单元主键',
  `CI_ID` varchar(10) DEFAULT NULL COMMENT '所属课程项ID',
  `WEEKDAY` varchar(20) DEFAULT NULL COMMENT '星期几',
  `LESSON_NUMBER` varchar(20) DEFAULT NULL COMMENT '第几节课（多节课小数点隔开，如：2.3）',
  `LOCATION` varchar(40) DEFAULT NULL COMMENT '教学楼ID',
  `CLASSROOM` varchar(5) DEFAULT NULL COMMENT '教室',
  PRIMARY KEY (`CT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_course_cell
-- ----------------------------

-- ----------------------------
-- Table structure for `t_course_class`
-- ----------------------------
DROP TABLE IF EXISTS `t_course_class`;
CREATE TABLE `t_course_class` (
  `CC_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '课程班级关联表主键',
  `COURSE_ID` varchar(10) DEFAULT NULL COMMENT '课程ID',
  `CLASS_ID` varchar(10) DEFAULT NULL COMMENT '班级ID',
  PRIMARY KEY (`CC_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_course_class
-- ----------------------------

-- ----------------------------
-- Table structure for `t_course_file(abandon,与t_file表重复)`
-- ----------------------------
DROP TABLE IF EXISTS `t_course_file(abandon,与t_file表重复)`;
CREATE TABLE `t_course_file(abandon,与t_file表重复)` (
  `File_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '资源表主键',
  `DATA_ID` varchar(10) DEFAULT NULL COMMENT '文件所属数据ID',
  `FILE_NAME` varchar(100) DEFAULT NULL COMMENT '资源原始名称',
  `SERVER_NAME` varchar(10) DEFAULT NULL COMMENT '服务器存储名称',
  `IS_COURSE_FILE` int(2) DEFAULT NULL COMMENT '是否为课程资源   01：是   02：不是',
  `VOCABULARY_ID` varchar(2) DEFAULT NULL COMMENT '课程资源类型ID',
  `FILE_AUTH` varchar(2) DEFAULT NULL COMMENT '资源权限（01公开 02不公开）',
  PRIMARY KEY (`File_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_course_file(abandon,与t_file表重复)
-- ----------------------------
INSERT INTO `t_course_file(abandon,与t_file表重复)` VALUES ('bb4abZWKkS', 'qJTKcpG0Mc', 'Keep You In My Heart v5(1).mp3', 'ThowHSB6ga', null, '01', '01');
INSERT INTO `t_course_file(abandon,与t_file表重复)` VALUES ('icvdRNf0Wn', 'DDJHAT0SKb', 'TODO.txt', '1i4gx9lVHB', null, '01', '01');
INSERT INTO `t_course_file(abandon,与t_file表重复)` VALUES ('w3qphYG0PH', 'qJTKcpG0Mc', '颈椎健康操.mp4', 'focg2j7w4o', null, '01', '01');

-- ----------------------------
-- Table structure for `t_course_item`
-- ----------------------------
DROP TABLE IF EXISTS `t_course_item`;
CREATE TABLE `t_course_item` (
  `CI_ID` varchar(10) NOT NULL COMMENT '课程项ID',
  `REPEAT_TYPE` varchar(2) DEFAULT NULL COMMENT '重复类型（01 天 02 周）',
  `REPEAT_NUMBER` int(3) DEFAULT NULL COMMENT '重复数字',
  `START_WEEK` int(3) DEFAULT NULL COMMENT '起始周',
  `END_WEEK` int(3) DEFAULT NULL COMMENT '结束周',
  `START_DAY` varchar(10) DEFAULT NULL COMMENT '起始日期',
  `END_DAY` varchar(10) DEFAULT NULL COMMENT '结束日期',
  `COURSE_ID` varchar(10) DEFAULT NULL COMMENT '所属课程ID',
  PRIMARY KEY (`CI_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_course_item
-- ----------------------------

-- ----------------------------
-- Table structure for `t_course_score_private`
-- ----------------------------
DROP TABLE IF EXISTS `t_course_score_private`;
CREATE TABLE `t_course_score_private` (
  `CSP_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '课程成绩组成表主键',
  `COURSE_ID` varchar(10) DEFAULT NULL,
  `SCORE_NAME` varchar(20) DEFAULT NULL COMMENT '成绩组成名称',
  `SCORE_PERCENT` decimal(5,2) DEFAULT NULL COMMENT '成绩组成百分比',
  `CS_ORDER` int(11) DEFAULT NULL,
  `SCORE_POINT_ID` varchar(10) DEFAULT NULL COMMENT '分制',
  `STATUS` int(2) DEFAULT NULL COMMENT '成绩组成项的性质。  01：替换上一次成绩   02：新增一次该项成绩',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '用户ID',
  `CS_ID` varchar(10) DEFAULT NULL COMMENT '共有表主键',
  PRIMARY KEY (`CSP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_course_score_private
-- ----------------------------

-- ----------------------------
-- Table structure for `t_course_score_public`
-- ----------------------------
DROP TABLE IF EXISTS `t_course_score_public`;
CREATE TABLE `t_course_score_public` (
  `CS_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '课程成绩组成表主键',
  `COURSE_ID` varchar(10) DEFAULT NULL,
  `SCORE_NAME` varchar(20) DEFAULT NULL COMMENT '成绩组成名称',
  `SCORE_PERCENT` decimal(5,2) DEFAULT NULL COMMENT '成绩组成百分比',
  `CS_ORDER` int(11) DEFAULT NULL,
  `SCORE_POINT_ID` varchar(10) DEFAULT NULL COMMENT '分制',
  `STATUS` int(2) DEFAULT NULL COMMENT '成绩组成项的性质。  01：替换上一次成绩   02：新增一次该项成绩',
  PRIMARY KEY (`CS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_course_score_public
-- ----------------------------
INSERT INTO `t_course_score_public` VALUES ('1', null, '期末成绩', '60.00', null, '10', '1');
INSERT INTO `t_course_score_public` VALUES ('2', null, '期中成绩', '30.00', null, '10', '1');
INSERT INTO `t_course_score_public` VALUES ('3', null, '平时成绩', '10.00', null, '5', '2');

-- ----------------------------
-- Table structure for `t_custom_data`
-- ----------------------------
DROP TABLE IF EXISTS `t_custom_data`;
CREATE TABLE `t_custom_data` (
  `CD_ID` varchar(10) NOT NULL DEFAULT '',
  `USER_ID` varchar(10) DEFAULT NULL,
  `DATA_TYPE` varchar(10) DEFAULT NULL,
  `DATA_LABEL` varchar(10) DEFAULT NULL,
  `DATA_VALUE` varchar(20) DEFAULT NULL,
  `DATA_ID` varchar(10) DEFAULT NULL,
  `IS_CUSTOM` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`CD_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_custom_data
-- ----------------------------

-- ----------------------------
-- Table structure for `t_dictionary2_private`
-- ----------------------------
DROP TABLE IF EXISTS `t_dictionary2_private`;
CREATE TABLE `t_dictionary2_private` (
  `DP_ID` varchar(10) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '公共字典表主键',
  `TYPE` int(2) DEFAULT NULL COMMENT '字典表类型。 01：课程类型   02：授课方式  03：考核类型  04：职称  05:职务  06：课程资源类型 ',
  `CODE` int(5) NOT NULL AUTO_INCREMENT COMMENT '类型编码',
  `PARENT_CODE` int(5) DEFAULT NULL COMMENT '父类的类型编码',
  `VALUE` varchar(20) DEFAULT NULL COMMENT '名称，值',
  `LEVEL` int(2) DEFAULT NULL COMMENT '层级，到根节点的层级',
  `STATUS` int(2) DEFAULT '1' COMMENT '状态码。  01可用，02不可用',
  `CREATE_TIME` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '用户ID',
  `DICTIONARY_ID` varchar(10) DEFAULT NULL COMMENT '引用的公共表的主键',
  PRIMARY KEY (`DP_ID`),
  UNIQUE KEY `CODE_2` (`CODE`),
  KEY `CODE` (`CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=21422 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_dictionary2_private
-- ----------------------------

-- ----------------------------
-- Table structure for `t_dictionary2_public`
-- ----------------------------
DROP TABLE IF EXISTS `t_dictionary2_public`;
CREATE TABLE `t_dictionary2_public` (
  `DICTIONARY_ID` varchar(10) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '字典表主键',
  `TYPE` int(2) DEFAULT NULL COMMENT '字典表类型。 01：课程类型   02：授课方式  03：考核类型  04：职称  05:职务  06：课程资源类型  07:分制 ',
  `CODE` int(5) NOT NULL AUTO_INCREMENT COMMENT '类型编码',
  `PARENT_CODE` int(5) DEFAULT NULL COMMENT '父类的类型编码',
  `VALUE` varchar(20) DEFAULT NULL COMMENT '名称，值',
  `LEVEL` int(2) DEFAULT NULL COMMENT '层级，到根节点的层级',
  `STATUS` int(2) DEFAULT '1' COMMENT '状态码。  01可用，02不可用',
  `CREATE_TIME` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `SCHOOL_ID` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`DICTIONARY_ID`),
  UNIQUE KEY `CODE_2` (`CODE`),
  KEY `CODE` (`CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=855443 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_dictionary2_public
-- ----------------------------
INSERT INTO `t_dictionary2_public` VALUES ('1', '1', '1', null, '课程类型', '1', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('2', '2', '2', null, '授课方式', '1', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('3', '3', '3', null, '考核类型', '1', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('4', '4', '4', null, '职称', '1', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('5', '5', '5', null, '职务', '1', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('6', '6', '6', null, '课程资源类型', '1', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('7', '7', '7', null, '分制', '1', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('aznuYBs', '7', '19', '7', '十分制', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('BHJndje', '2', '12', '2', '上机', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('BHkhjhu', '2', '213521', '3', '考试', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('BHzzuen', '1', '11', '1', '公共必修课', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('GjnnueH', '4', '5346', '4', '教授', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('HGJKghe', '6', '22', '6', '课件', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('HJjkjbhj', '4', '15', '4', '助教', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('HUHJhh', '6', '21', '6', '大纲', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('HUieiwh', '7', '482156', '7', '两分制', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('hunHUO', '5', '135216', '5', '副院长', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('HUnuwe', '1', '821354', '1', '公共选修课', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('HWAewx', '3', '20', '3', '考查', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('ILK5Oud', '4', '14', '4', '副教授', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('jhaGYwji', '5', '213825', '5', '主任', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('JHHUjh', '7', '245632', '7', '百分制', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('JHLhjkiu', '6', '218185', '6', '教学日历', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('JhuihjHU', '6', '454625', '6', '教案', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('jjNLKUjjh', '5', '523456', '5', '副主任', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('jknHUIjn', '4', '56101', '4', '讲师', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('JNlkeun', '1', '855442', '1', '专业主干课', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('kIJIhuo', '6', '212452', '6', '课件', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('kjzxieknkl', '5', '564852', '5', '组长', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('nbNsuz', '1', '10', '1', '专业基础课', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('nkjhUNjh', '6', '485218', '6', '大纲', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('NMByejh', '5', '435723', '5', '院长', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('Ymmzey', '7', '18', '7', '五分制', '2', '1', null, '4');
INSERT INTO `t_dictionary2_public` VALUES ('YNzueaw', '2', '13', '2', '理论', '2', '1', null, '4');

-- ----------------------------
-- Table structure for `t_file`
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file` (
  `File_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '资源表主键',
  `DATA_ID` varchar(10) DEFAULT NULL COMMENT '文件所属数据ID',
  `FILE_NAME` varchar(100) DEFAULT NULL COMMENT '资源原始名称',
  `SERVER_NAME` varchar(200) DEFAULT NULL COMMENT '服务器存储名称',
  `IS_COURSE_FILE` int(2) DEFAULT NULL COMMENT '是否为课程资源   01：是   02：不是',
  `VOCABULARY_ID` varchar(10) DEFAULT NULL COMMENT '课程资源类型ID',
  `FILE_AUTH` varchar(2) DEFAULT NULL COMMENT '资源权限（01公开 02不公开）',
  PRIMARY KEY (`File_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_file
-- ----------------------------

-- ----------------------------
-- Table structure for `t_log`
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log` (
  `LOG_ID` varchar(10) NOT NULL COMMENT '日志ID',
  `STU_ID` varchar(10) DEFAULT NULL COMMENT '学生id',
  `NOTICE_ID` varchar(10) DEFAULT NULL COMMENT '通知ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '时间',
  `TYPE` int(2) DEFAULT NULL COMMENT '日志类型。    01：通知查看日志',
  PRIMARY KEY (`LOG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_log
-- ----------------------------

-- ----------------------------
-- Table structure for `t_major`
-- ----------------------------
DROP TABLE IF EXISTS `t_major`;
CREATE TABLE `t_major` (
  `MAJOR_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '专业表主键',
  `MAJOR_NAME` varchar(80) DEFAULT NULL COMMENT '专业名称',
  `PARENT_ID` varchar(10) DEFAULT NULL COMMENT '父专业ID',
  PRIMARY KEY (`MAJOR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_major
-- ----------------------------
INSERT INTO `t_major` VALUES ('01', '哲学', '0');
INSERT INTO `t_major` VALUES ('0101', '哲学类', '01');
INSERT INTO `t_major` VALUES ('010101', '哲学', '0101');
INSERT INTO `t_major` VALUES ('010102', '逻辑学', '0101');
INSERT INTO `t_major` VALUES ('02', '经济学', '0');
INSERT INTO `t_major` VALUES ('0201', '经济学类', '02');
INSERT INTO `t_major` VALUES ('020101', '经济学', '0201');
INSERT INTO `t_major` VALUES ('020102', '经济统计学', '0201');
INSERT INTO `t_major` VALUES ('0202', '财政学类', '02');
INSERT INTO `t_major` VALUES ('020202', '税收学', '0202');
INSERT INTO `t_major` VALUES ('0203', '金融学类', '02');
INSERT INTO `t_major` VALUES ('020301K', '金融学', '0203');
INSERT INTO `t_major` VALUES ('020302', '金融工程', '0203');
INSERT INTO `t_major` VALUES ('020303', '保险学', '0203');
INSERT INTO `t_major` VALUES ('020304', '投资学', '0203');
INSERT INTO `t_major` VALUES ('0204', '经济与贸易类', '02');
INSERT INTO `t_major` VALUES ('020401', '国际经济与贸易类', '0204');
INSERT INTO `t_major` VALUES ('020402', '贸易经济', '0204');
INSERT INTO `t_major` VALUES ('10103K', '宗教学', '0101');
INSERT INTO `t_major` VALUES ('20201K', '财政学', '0202');

-- ----------------------------
-- Table structure for `t_message`
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `MSG_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '消息表主键',
  `FORM_USER_ID` varchar(10) DEFAULT NULL COMMENT '发送人ID',
  `TO_USER_ID` varchar(10) DEFAULT NULL COMMENT '接受人ID',
  `CONTENT` varchar(400) DEFAULT NULL COMMENT '消息内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `IS_HAVE_FILE` bit(2) DEFAULT NULL COMMENT '该消息是否带有附件',
  `PARENT_MSG_ID` varchar(10) DEFAULT NULL COMMENT '对应的messageID',
  PRIMARY KEY (`MSG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_message
-- ----------------------------

-- ----------------------------
-- Table structure for `t_note`
-- ----------------------------
DROP TABLE IF EXISTS `t_note`;
CREATE TABLE `t_note` (
  `NOTE_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '笔记表主键',
  `TITLE` varchar(40) DEFAULT '' COMMENT '笔记标题',
  `CONTENT` varchar(400) DEFAULT NULL COMMENT '笔记内容',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '创建人ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `COURSE_ID` varchar(10) DEFAULT NULL COMMENT '所属课程Id',
  `IS_KEY` char(1) DEFAULT NULL COMMENT '是否标记为重点资源（0:不标记  1：标记）',
  PRIMARY KEY (`NOTE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_note
-- ----------------------------

-- ----------------------------
-- Table structure for `t_notice`
-- ----------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice` (
  `NOTICE_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '通知表主键',
  `TITLE` varchar(80) DEFAULT NULL COMMENT '通知标题',
  `CONTENT` text COMMENT '内容',
  `PUBLISH_TIME` varchar(25) DEFAULT NULL COMMENT '发布时间',
  `CREATE_TIME` varchar(25) DEFAULT NULL COMMENT '创建时间',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '发布人ID',
  `STATUS` int(2) DEFAULT NULL COMMENT '通知状态(0是已删除，1是未删除，2是编辑)',
  PRIMARY KEY (`NOTICE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice
-- ----------------------------

-- ----------------------------
-- Table structure for `t_post`
-- ----------------------------
DROP TABLE IF EXISTS `t_post`;
CREATE TABLE `t_post` (
  `POST_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '同学帮表主键',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '创建用户',
  `CONTENT` varchar(400) DEFAULT NULL COMMENT '内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `FILE_ID` varchar(10) DEFAULT NULL COMMENT '附件id',
  PRIMARY KEY (`POST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_post
-- ----------------------------

-- ----------------------------
-- Table structure for `t_post_activity`
-- ----------------------------
DROP TABLE IF EXISTS `t_post_activity`;
CREATE TABLE `t_post_activity` (
  `PA_ID` varchar(10) NOT NULL,
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '用户id',
  `ACTIVITY` int(10) DEFAULT NULL COMMENT '用户热度(点赞+1，评论+2)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '生成时间',
  `TARGET_ID` varchar(10) DEFAULT NULL COMMENT '使用户产生活动的目标对象',
  PRIMARY KEY (`PA_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_post_activity
-- ----------------------------

-- ----------------------------
-- Table structure for `t_post_like`
-- ----------------------------
DROP TABLE IF EXISTS `t_post_like`;
CREATE TABLE `t_post_like` (
  `PL_ID` varchar(10) DEFAULT NULL COMMENT '学生帮点赞表主键',
  `POST_ID` varchar(10) DEFAULT NULL COMMENT '同学帮主键',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '用户ID',
  `CREATE_TIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_post_like
-- ----------------------------

-- ----------------------------
-- Table structure for `t_post_reply`
-- ----------------------------
DROP TABLE IF EXISTS `t_post_reply`;
CREATE TABLE `t_post_reply` (
  `PR_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '同学帮回复表主键',
  `POST_ID` varchar(10) DEFAULT NULL COMMENT '同学帮ID',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '回复人ID',
  `PARENT_PR_ID` varchar(10) DEFAULT NULL COMMENT '评论的POST_REPlY的ID（对POST的评论此栏位为空）',
  `CONTENT` varchar(400) DEFAULT NULL COMMENT '回复内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (`PR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_post_reply
-- ----------------------------

-- ----------------------------
-- Table structure for `t_regist_config`
-- ----------------------------
DROP TABLE IF EXISTS `t_regist_config`;
CREATE TABLE `t_regist_config` (
  `CONFIG_ID` varchar(20) NOT NULL COMMENT '课程签到表主键',
  `REGIST_BEFORE` int(2) DEFAULT NULL COMMENT '课程开始前多长时间开始签到',
  `REGIST_AFTER` int(2) DEFAULT NULL COMMENT '课程开始后多长时间结束签到',
  `REGIST_DISTANCE` int(4) DEFAULT NULL COMMENT '签到距离设置',
  `STATUS` int(2) DEFAULT '1' COMMENT '状态： 0：系统默认    1：用户自定义  ',
  `USER_ID` varchar(20) DEFAULT NULL COMMENT '教师用户ID',
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`CONFIG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_regist_config
-- ----------------------------
INSERT INTO `t_regist_config` VALUES ('HUIJnknj', '10', '10', '100', '0', null, null);

-- ----------------------------
-- Table structure for `t_school`
-- ----------------------------
DROP TABLE IF EXISTS `t_school`;
CREATE TABLE `t_school` (
  `SCHOOL_ID` varchar(10) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '字典表主键',
  `TYPE` int(2) DEFAULT NULL COMMENT '字典表类型。  01：省，02：市，03：学校，04：教学楼, 05：教学楼的经度坐标  06：教学楼的纬度坐标',
  `CODE` int(5) DEFAULT NULL COMMENT '类型编码',
  `PARENT_CODE` int(5) DEFAULT NULL COMMENT '父类的类型编码',
  `VALUE` varchar(20) DEFAULT NULL COMMENT '名称，值',
  `LEVEL` int(2) DEFAULT NULL COMMENT '层级，到根节点的层级',
  `STATUS` int(2) DEFAULT '1' COMMENT '状态码。  01可用，02不可用',
  `lon` varchar(10) DEFAULT NULL COMMENT '经度坐标',
  `lat` varchar(10) DEFAULT NULL COMMENT '纬度坐标',
  PRIMARY KEY (`SCHOOL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_school
-- ----------------------------
INSERT INTO `t_school` VALUES ('1', '1', '1', null, '河北省', '1', '1', null, null);
INSERT INTO `t_school` VALUES ('10', '4', '10', '4', '尚学楼', '4', '1', null, null);
INSERT INTO `t_school` VALUES ('11', '4', '11', '4', '崇静楼', '4', '1', null, null);
INSERT INTO `t_school` VALUES ('12', '4', '12', '4', '致远楼', '4', '1', '114.55662', '35.037429');
INSERT INTO `t_school` VALUES ('13', '4', '13', '4', '尚志楼', '4', '1', null, null);
INSERT INTO `t_school` VALUES ('2', '1', '2', null, '山东省', '1', '1', null, null);
INSERT INTO `t_school` VALUES ('3', '2', '3', '1', '石家庄市', '2', '1', null, null);
INSERT INTO `t_school` VALUES ('4', '3', '4', '3', '石家庄学院', '3', '1', null, null);
INSERT INTO `t_school` VALUES ('5', '2', '5', '2', '济南市', '2', '1', null, null);
INSERT INTO `t_school` VALUES ('6', '3', '6', '5', '山东大学', '3', '1', null, null);
INSERT INTO `t_school` VALUES ('7', '3', '7', '3', '河北师范大学', '3', '1', null, null);
INSERT INTO `t_school` VALUES ('8', '3', '8', '3', '河北科技大学', '3', '1', null, null);
INSERT INTO `t_school` VALUES ('9', '4', '9', '4', '尚德楼', '4', '1', null, null);

-- ----------------------------
-- Table structure for `t_school_major`
-- ----------------------------
DROP TABLE IF EXISTS `t_school_major`;
CREATE TABLE `t_school_major` (
  `SM_ID` varchar(10) NOT NULL COMMENT '学校-专业关联表id',
  `SCHOOL_ID` varchar(10) DEFAULT NULL,
  `MAJOR_ID` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`SM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_school_major
-- ----------------------------
INSERT INTO `t_school_major` VALUES ('0avT5tQW65', '4', '020402');
INSERT INTO `t_school_major` VALUES ('62g6NwCZya', '4', '010101');
INSERT INTO `t_school_major` VALUES ('8pPvJejHWQ', '4', '020301K');
INSERT INTO `t_school_major` VALUES ('ab3UEtsJhg', '4', '0101');
INSERT INTO `t_school_major` VALUES ('aCgEFKZQKy', '4', '0201');
INSERT INTO `t_school_major` VALUES ('bmFn0HDx82', '4', '020101');
INSERT INTO `t_school_major` VALUES ('FgeOvodgVo', '4', '020201K');
INSERT INTO `t_school_major` VALUES ('I7ivMWm7dg', '4', '010103K');
INSERT INTO `t_school_major` VALUES ('iqo40OUX3I', '4', '020401');
INSERT INTO `t_school_major` VALUES ('lfpotwuKRG', '4', '020102');
INSERT INTO `t_school_major` VALUES ('Lfu1smaklW', '4', '020302');
INSERT INTO `t_school_major` VALUES ('M4yoIQ8Pgk', '4', '0203');
INSERT INTO `t_school_major` VALUES ('MxaMekqQny', '4', '010102');
INSERT INTO `t_school_major` VALUES ('o768Sk6oJm', '4', '02');
INSERT INTO `t_school_major` VALUES ('pgvZQzBxWn', '4', '0204');
INSERT INTO `t_school_major` VALUES ('qnuQe5GP5f', '4', '020304');
INSERT INTO `t_school_major` VALUES ('rdFGaW4Ho4', '4', '01');
INSERT INTO `t_school_major` VALUES ('uB9q0IHmwb', '4', '0202');

-- ----------------------------
-- Table structure for `t_score`
-- ----------------------------
DROP TABLE IF EXISTS `t_score`;
CREATE TABLE `t_score` (
  `SCORE_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '成绩表主键',
  `STU_ID` varchar(10) DEFAULT NULL COMMENT '学生ID',
  `COURSE_ID` varchar(10) DEFAULT NULL COMMENT '课程ID',
  `CS_ID` varchar(10) DEFAULT NULL COMMENT '课程成绩组成ID',
  `SCORE` varchar(3) DEFAULT NULL COMMENT '分数',
  `CREATE_TIME` varchar(30) DEFAULT NULL COMMENT '该条记录的生成时间',
  PRIMARY KEY (`SCORE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_score
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sign_in`
-- ----------------------------
DROP TABLE IF EXISTS `t_sign_in`;
CREATE TABLE `t_sign_in` (
  `SIGN_ID` varchar(10) NOT NULL,
  `COURSE_ID` varchar(10) DEFAULT NULL COMMENT '课程id',
  `SIGN_TIME` varchar(20) DEFAULT NULL COMMENT '签到日期（2016-12-30）',
  `CURRENT_CELL` varchar(20) DEFAULT NULL COMMENT '第几节课',
  `STUDENT_ID` varchar(10) DEFAULT NULL COMMENT '学生id',
  `STATUS` int(2) DEFAULT NULL COMMENT '签到状态【0:统计数据   1：已签到】',
  `CREATE_TIME` smallint(30) DEFAULT NULL COMMENT '签到记录生成时间（签到时间）',
  `LON` varchar(10) DEFAULT NULL COMMENT '签到地点的经度',
  `LAT` varchar(10) DEFAULT NULL COMMENT '签到地点的纬度',
  `COURSE_NUM` int(3) DEFAULT '0' COMMENT '上课次数',
  PRIMARY KEY (`SIGN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sign_in
-- ----------------------------

-- ----------------------------
-- Table structure for `t_status`
-- ----------------------------
DROP TABLE IF EXISTS `t_status`;
CREATE TABLE `t_status` (
  `WS_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '学生作业完成标识主键',
  `WORK_ID` varchar(10) DEFAULT NULL COMMENT '作业ID',
  `STU_ID` varchar(10) DEFAULT NULL COMMENT '学生用户ID',
  PRIMARY KEY (`WS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_status
-- ----------------------------

-- ----------------------------
-- Table structure for `t_student`
-- ----------------------------
DROP TABLE IF EXISTS `t_student`;
CREATE TABLE `t_student` (
  `STU_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '学生表主键',
  `CLASS_ID` varchar(10) DEFAULT NULL COMMENT '班级ID',
  `STU_NO` varchar(40) DEFAULT NULL COMMENT '学号',
  `STU_NAME` varchar(8) DEFAULT NULL COMMENT '姓名',
  `SEX` char(2) DEFAULT NULL,
  `SCHOOL_ID` varchar(40) DEFAULT NULL COMMENT '所属学校ID',
  `FACULTY` varchar(40) DEFAULT NULL COMMENT '院系',
  `PICTURE` varchar(50) DEFAULT NULL COMMENT '头像文件路径',
  PRIMARY KEY (`STU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_student
-- ----------------------------

-- ----------------------------
-- Table structure for `t_teacher`
-- ----------------------------
DROP TABLE IF EXISTS `t_teacher`;
CREATE TABLE `t_teacher` (
  `TEACHER_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '教室表主键',
  `TEACHER_NO` varchar(40) DEFAULT NULL COMMENT '教工号',
  `NAME` varchar(20) DEFAULT NULL COMMENT '姓名',
  `SEX` char(2) DEFAULT NULL COMMENT '性别(男or女)',
  `TITLE_ID` varchar(40) DEFAULT NULL COMMENT '职称ID',
  `POST_ID` varchar(40) DEFAULT NULL COMMENT '职务ID',
  `SCHOOL_ID` varchar(40) DEFAULT NULL COMMENT '学校ID',
  `DEPARTMENT` varchar(40) DEFAULT NULL COMMENT '院系',
  `INTRODUCTION` varchar(400) DEFAULT NULL COMMENT '简介',
  `PICTURE` varchar(50) DEFAULT NULL COMMENT '用户头像所在路径',
  PRIMARY KEY (`TEACHER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_teacher
-- ----------------------------

-- ----------------------------
-- Table structure for `t_term`
-- ----------------------------
DROP TABLE IF EXISTS `t_term`;
CREATE TABLE `t_term` (
  `TERM_ID` varchar(10) NOT NULL COMMENT '学期ID',
  `TERM_NAME` varchar(20) DEFAULT NULL COMMENT '学期名称：2015-2016学年第1学期',
  `START_DATE` date DEFAULT NULL,
  `END_DATE` date DEFAULT NULL,
  `WEEK_COUNT` int(3) DEFAULT NULL,
  `SCHOOL_ID` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`TERM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_term
-- ----------------------------
INSERT INTO `t_term` VALUES ('1', '2015-2016学年第一学期', '2015-09-01', '2016-01-11', '19', '3');
INSERT INTO `t_term` VALUES ('2', '2015-2016学年第二学期', '2016-02-23', '2016-06-27', '18', '4');
INSERT INTO `t_term` VALUES ('3', '2016-2017学年第一学期', '2016-09-02', '2017-01-10', '20', '4');
INSERT INTO `t_term` VALUES ('4', '2016-2017学年第二学期', '2017-02-23', '2017-06-27', '19', '4');

-- ----------------------------
-- Table structure for `t_term_private`
-- ----------------------------
DROP TABLE IF EXISTS `t_term_private`;
CREATE TABLE `t_term_private` (
  `TP_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '学期表主键',
  `START_DATE` varchar(10) DEFAULT NULL COMMENT '学期开始日期 yyyy-MM-dd',
  `END_DATE` varchar(10) DEFAULT NULL COMMENT '学期结束日期 yyyy-MM-dd',
  `WEEK_COUNT` int(3) DEFAULT NULL COMMENT '周数',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '所属用户ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `TREM_ID` varchar(10) DEFAULT NULL,
  `STATUS` int(2) DEFAULT NULL COMMENT '状态：  1 可用 ，2 不可用',
  PRIMARY KEY (`TP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_term_private
-- ----------------------------

-- ----------------------------
-- Table structure for `t_textbook`
-- ----------------------------
DROP TABLE IF EXISTS `t_textbook`;
CREATE TABLE `t_textbook` (
  `TEXTBOOK_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '教材教辅表主键',
  `COURSE_ID` varchar(10) DEFAULT NULL COMMENT '课程ID',
  `TEXTBOOK_NAME` varchar(40) DEFAULT NULL COMMENT '名称',
  `AUTHOR` varchar(20) DEFAULT NULL COMMENT '作者',
  `PUBLISHER` varchar(40) DEFAULT NULL COMMENT '出版社',
  `EDITION` varchar(20) DEFAULT NULL COMMENT '版次',
  `ISBN` varchar(40) DEFAULT NULL COMMENT '国际标准书号',
  `TEXTBOOK_TYPE` varchar(2) DEFAULT NULL COMMENT '类型（01 教材 02 教辅）',
  PRIMARY KEY (`TEXTBOOK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_textbook
-- ----------------------------

-- ----------------------------
-- Table structure for `t_timetable`
-- ----------------------------
DROP TABLE IF EXISTS `t_timetable`;
CREATE TABLE `t_timetable` (
  `TIMETABLE_ID` varchar(10) NOT NULL COMMENT '作息时间表主键',
  `SCHOOL_ID` varchar(10) DEFAULT NULL COMMENT '学校ID',
  `END_TIME` varchar(6) DEFAULT NULL COMMENT '课程结束时间',
  `START_TIME` varchar(6) DEFAULT NULL COMMENT '课程开始时间',
  `LESSON_NUMBER` varchar(4) DEFAULT NULL COMMENT '第几节课',
  PRIMARY KEY (`TIMETABLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_timetable
-- ----------------------------
INSERT INTO `t_timetable` VALUES ('1', '4', '08:50', '08:00', '1');
INSERT INTO `t_timetable` VALUES ('2', '4', '09:50', '09:00', '2');
INSERT INTO `t_timetable` VALUES ('3', '4', '10:50', '10:00', '3');
INSERT INTO `t_timetable` VALUES ('4', '4', '11:50', '11:00', '4');
INSERT INTO `t_timetable` VALUES ('5', '4', '13:50', '13:00', '5');
INSERT INTO `t_timetable` VALUES ('6', '4', '14:50', '14:00', '6');
INSERT INTO `t_timetable` VALUES ('7', '4', '17:50', '17:00', '7');
INSERT INTO `t_timetable` VALUES ('8', '4', '18:50', '18:00', '8');

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `USER_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '注册用户表主键',
  `USER_TYPE` char(2) DEFAULT NULL COMMENT '用户类型(01教师 02学生 03管理员)',
  `ACCOUNT` varchar(11) DEFAULT NULL COMMENT '账号',
  `PASSWORD` varchar(32) DEFAULT NULL COMMENT '密码',
  `CREATE_TIME` varchar(19) DEFAULT NULL COMMENT '注册日期 yyyy-MM-dd HH:mm:ss',
  `LAST_LOGIN_TIME` varchar(19) DEFAULT NULL COMMENT '上次登录时间 yyyy-MM-dd HH:mm:ss',
  `TOKEN` varchar(40) DEFAULT NULL COMMENT '用户令牌',
  `LAST_ACCESS_TIME` varchar(19) DEFAULT NULL COMMENT '最终成功访问时间 yyyy-MM-dd HH:mm:ss',
  `IMEI` varchar(30) DEFAULT NULL COMMENT '设备唯一标识',
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------

-- ----------------------------
-- Table structure for `t_user_communication`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_communication`;
CREATE TABLE `t_user_communication` (
  `C_ID` varchar(10) NOT NULL COMMENT '用户通讯信息表主键',
  `USER_ID` varchar(10) DEFAULT NULL COMMENT '用户ID',
  `TYPE` int(1) DEFAULT NULL COMMENT '类型：1：电话  2：邮箱   3：IM ',
  `NAME` varchar(6) DEFAULT NULL COMMENT '名称',
  `VALUE` varchar(20) DEFAULT NULL COMMENT '值',
  `STATUS` int(1) DEFAULT NULL COMMENT '状态。（此处指邮箱是否绑定）',
  PRIMARY KEY (`C_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_communication
-- ----------------------------

-- ----------------------------
-- Table structure for `t_verify_code`
-- ----------------------------
DROP TABLE IF EXISTS `t_verify_code`;
CREATE TABLE `t_verify_code` (
  `CODE_ID` varchar(11) NOT NULL COMMENT 'ID为手机号',
  `VERIFY_CODE` varchar(8) DEFAULT NULL COMMENT '验证码',
  `IMEI` varchar(40) DEFAULT NULL COMMENT '设备标识',
  `TIME` varchar(20) DEFAULT NULL COMMENT '发送的时间',
  `TYPE` int(2) DEFAULT NULL COMMENT '0:用户注册；1：忘记密码',
  PRIMARY KEY (`CODE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_verify_code
-- ----------------------------

-- ----------------------------
-- Table structure for `t_work`
-- ----------------------------
DROP TABLE IF EXISTS `t_work`;
CREATE TABLE `t_work` (
  `WORK_ID` varchar(10) NOT NULL DEFAULT '' COMMENT '作业表主键',
  `CONTENT` varchar(400) DEFAULT NULL COMMENT '作业内容',
  `PUBLISH_TIME` varchar(25) DEFAULT NULL COMMENT '发布时间',
  `END_TIME` varchar(25) DEFAULT NULL COMMENT '终止时间',
  `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `REMIND_TIME` varchar(3) DEFAULT NULL COMMENT '作业提醒时间',
  `STATUS` int(3) DEFAULT NULL COMMENT '作业状态(0:删除  1:发布/待发布  2:草稿)',
  PRIMARY KEY (`WORK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_work
-- ----------------------------

-- ----------------------------
-- Table structure for `t_work_course`
-- ----------------------------
DROP TABLE IF EXISTS `t_work_course`;
CREATE TABLE `t_work_course` (
  `WC_ID` varchar(10) NOT NULL COMMENT '作业/通知—课程关联表',
  `WORK_ID` varchar(10) DEFAULT NULL COMMENT '作业ID/通知ＩＤ',
  `COURSE_ID` varchar(10) DEFAULT NULL COMMENT '课程ID',
  PRIMARY KEY (`WC_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_work_course
-- ----------------------------
