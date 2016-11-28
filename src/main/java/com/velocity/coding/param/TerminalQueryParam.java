package com.velocity.coding.param;

import java.io.Serializable;


public class TerminalQueryParam extends QueryParam implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;
	private String birthBeginDate;
	private String birthEndDate;

    private String createDateBegin;
	private String createDateEnd;
    
    public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setBirthBeginDate(String birthBeginDate) {
		this.birthBeginDate = birthBeginDate;
	}
	public String getBirthBeginDate() {
		return birthBeginDate;
	}
	public void setBirthEndDate(String birthEndDate) {
		this.birthEndDate = birthEndDate;
	}
	public String getBirthEndDate() {
		return birthEndDate;
	}

    public String getCreateDateBegin() {
		return createDateBegin;
	}
	public void setCreateDateBegin(String createDateBegin) {
		this.createDateBegin = createDateBegin;
	}
	public String getCreateDateEnd() {
		return createDateEnd;
	}
	public void setCreateDateEnd(String createDateEnd) {
		this.createDateEnd = createDateEnd;
	}
}