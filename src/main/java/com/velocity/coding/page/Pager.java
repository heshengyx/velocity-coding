package com.velocity.coding.page;

import java.util.ArrayList;
import java.util.List;

public final class Pager {

	public static <T> IPage<T> execute(IPagination<T> pagination, int pageNo, int pageSize) {
		List<T> list = null;
		IPage<T> page = null;
		int count = pagination.count();
		if (count > 0) {
			pageNo = (pageNo <= 0) ? 1 : pageNo;
			pageSize = (pageSize <= 0) ? 10 : pageSize;
			int start = (pageNo - 1) * pageSize;
			list = pagination.query(start, pageSize);
			page = new Page<T>(list, count);
		}
		if (null == page) {
			page = new Page<T>(new ArrayList<T>(), 0);
		}
		return page;
	}
}
