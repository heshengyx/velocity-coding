package com.velocity.coding.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 分页控件
 * @author hsheng1
 *
 * @param <T>
 */
public final class Page<T> implements IPage<T>, Serializable {

	private static final long serialVersionUID = 4336812407961532349L;

	/**
	 * 总记录数
	 */
	private int total;
	
	/**
	 * 每页数据
	 */
	private Collection<T> rows;

	public Page() {
	}

	public Page(Collection<T> rows, int total) {
		this.rows = (rows == null ? new ArrayList<T>(0) : rows);
		this.total = total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotal() {
		return total;
	}
	
	public Collection<T> getRows() {
		return rows;
	}
}
