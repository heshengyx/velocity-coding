package com.velocity.coding.data;

import java.io.Serializable;


/**
 * Terminal
 * @author hsheng1
 *
 */
public class TerminalData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String[] name;

	private String createBy;
	
	public void setName(String[] name) {
		this.name = name;
	}
	public String[] getName() {
		return name;
	}

	public String getCreateBy() {
        return createBy;
    }
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}