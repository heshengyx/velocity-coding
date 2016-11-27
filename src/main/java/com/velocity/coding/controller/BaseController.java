package com.velocity.coding.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.velocity.coding.entity.User;

public class BaseController {

	/**
	 * 取得当前登录用户
	 * @return
	 */
	protected User getCurrentUser() {
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		User user = (User) session.getAttribute("currentUser");
		return user;
	}
}
