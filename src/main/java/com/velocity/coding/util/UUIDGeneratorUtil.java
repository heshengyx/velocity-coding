package com.velocity.coding.util;

import java.util.UUID;

/**
 * UUID生成器
 * @author hsheng1
 *
 */
public class UUIDGeneratorUtil {

	private UUIDGeneratorUtil() {}
	
	/**
	 * 获取随机UUID
	 * 
	 * @return 随机UUID
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().toUpperCase().replaceAll("-", "")
				.replaceAll(" ", "");
	}
}
