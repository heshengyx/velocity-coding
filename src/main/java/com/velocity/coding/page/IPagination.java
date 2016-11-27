package com.velocity.coding.page;

import java.util.List;

public interface IPagination<T> {

	public int count();
	public List<T> query(int start, int end);
}
