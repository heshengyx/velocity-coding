package com.velocity.coding.param;

import java.io.Serializable;


public class StyleQueryParam extends QueryParam implements Serializable {

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