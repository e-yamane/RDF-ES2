package jp.rough_diamond.sample.esb.service;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
	private List<ResponseRecord> records;

	public List<ResponseRecord> getRecords() {
		return records;
	}

	public void setRecords(List<ResponseRecord> records) {
		this.records = records;
	}
}
