package com.xiaoshu.entity;

public class Vo extends Goods{
	private String typename;
	private String kdate;
	private String jdate;
	private int num;
	
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public String getKdate() {
		return kdate;
	}
	public void setKdate(String kdate) {
		this.kdate = kdate;
	}
	public String getJdate() {
		return jdate;
	}
	public void setJdate(String jdate) {
		this.jdate = jdate;
	}
}
