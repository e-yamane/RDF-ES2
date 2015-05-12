package jp.rough_diamond.sample.esb.service;

import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private List<Integer> score;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Integer> getScore() {
		return score;
	}
	public void setScore(List<Integer> score) {
		this.score = score;
	}
}
