package com.velocity.coding.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * Style
 * @author hsheng1
 *
 */
public class Style extends BaseEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String name;

	
	public void setName(String name) {
	this.name = name;
}
public String getName() {
	return name;
}

}