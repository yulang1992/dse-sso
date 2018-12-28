package com.dse.sso.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果集
 */
public class Result {

	/**
     * 处理结果状态码，Constants.CONTROLLER_RESULT
	 */
	private int result = Constants.CONTROLLER_RESULT.SUCCESS.intValue();

	/**
	 * 结果描述
	 */
	private String desc = "ok";

	/**
	 * 消息体
	 */
	private Map<String, Object> data = new HashMap<String, Object>();

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public void put(String key, Object obj) {
		this.data.put(key, obj);
	}

	public Result setErr(int result, String desc) {
		this.result = result;
		this.desc = desc;
		return this;
	}

}
