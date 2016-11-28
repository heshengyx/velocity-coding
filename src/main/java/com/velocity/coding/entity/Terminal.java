package com.velocity.coding.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * Terminal
 * @author hsheng1
 *
 */
public class Terminal extends BaseEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String name;
	private Date birthDate;

	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public Date getBirthDate() {
		return birthDate;
	}

}