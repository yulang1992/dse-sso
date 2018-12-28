package com.dse.sso.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * FileName: DseUser
 * Author:   EdwinYu
 * Date:     2018-12-19 14:22
 * Description: 用户对象
 * Version:1.0.0
 */
@Data
public class DseUser implements Serializable{

 private String  id;  // 主键id
 private String  userName;  // 用户名
 private String  password;  //  密码
 private String  name;  //  姓名
 private String  cardId;  // 	身份证号码
 private String  sex;  // 性别(下拉)
 private String  duty_level	;  // 职务级别(下拉)
 private String  tech_title	;  // 	技术职称(手动填写)
 private String  degree	;  // 	学历(下拉)
 private String  telephone	;  //  (15)	y			电话号码
 private String  mobile	;  //  (11)	y			手机号码
 private String  email	;  //  (40)	y			电子邮件

 private String  school	;  //  (100)	y			毕业院校
 private String  study_major	;  //  (100)	y			所学专业
 private String  address	;  //  (100)	y			家庭住址
 private String  born_place	;  //  (100)	y			出生地
 private String  political_status	;  //  (32)	y			政治面貌
 private String  nationality	;  //  (32)	y			用户民族
 private String  description	;  //  (256)	y			备注
 //private String  status	number	n			账号启用状态(0-禁用 1-启用)
 private String  line_status	;  // (1)	y			登录状态(0-退出状态1-登录状态)
 private String  creator	;  //  (32)	n			创建者
 private String  last_updator	;  //  (32)	y			修改者
 private String  del_flag	;  // (1)	n			删除标识(0-未删除;1-已删除)
 private String  type	;  // (1)	y			类型（是否管理员0-非管理员 1-管理员）

}
