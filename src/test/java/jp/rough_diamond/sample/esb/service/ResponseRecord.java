package jp.rough_diamond.sample.esb.service;

import java.io.Serializable;

public class ResponseRecord implements Serializable {
	private String name;
	private int total;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

}
