package com.velocity.coding.data;

import java.io.Serializable;


/**
 * Style
 * @author hsheng1
 *
 */
public class StyleData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String[] name;

	
	public void setName(String[] name) {
	this.name = name;
}
public String[] getName() {
	return name;
}

}