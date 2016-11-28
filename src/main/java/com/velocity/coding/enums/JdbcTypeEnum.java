package com.velocity.coding.enums;

import java.util.HashMap;
import java.util.Map;

public enum JdbcTypeEnum {

	STRING("VARCHAR"), DATE("TIMESTAMP");

	private String text;
	private static Map<String, String> types = new HashMap<String, String>();

	private JdbcTypeEnum(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	static {
		JdbcTypeEnum[] enums = JdbcTypeEnum.values();
		for (JdbcTypeEnum jdbcTypeEnum : enums) {
			types.put(jdbcTypeEnum.name(), jdbcTypeEnum.getText());
		}
	}

	public static String getJdbcType(String text) {
		return types.get(text.toUpperCase());
	}
}
