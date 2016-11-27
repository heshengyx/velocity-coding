package com.velocity.coding.page;

import java.util.Collection;

public interface IPage<T> {

	int getTotal();
	Collection<T> getRows();
}
