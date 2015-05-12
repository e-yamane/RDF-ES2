package jp.rough_diamond.sample.esb.service.impl;

import java.util.*;

import jp.rough_diamond.sample.esb.service.Request;
import jp.rough_diamond.sample.esb.service.Response;
import jp.rough_diamond.sample.esb.service.ResponseRecord;

@SuppressWarnings("all")
public class SampleService2Impl implements jp.rough_diamond.sample.esb.service.SampleService2 {
    public jp.rough_diamond.sample.esb.service.Response calculate(
            List<jp.rough_diamond.sample.esb.service.Request> params
    )
    {
    	Response ret = new Response();
    	List<ResponseRecord> records = new ArrayList<ResponseRecord>();
    	ret.setRecords(records);
    	for(Request req: params) {
    		int sum = 0;
    		for(Integer i : req.getScore()) {
    			sum += i;
    		}
    		ResponseRecord response = new ResponseRecord();
    		response.setName(req.getName());
    		response.setTotal(sum);
    		records.add(response);
    	}
        return ret;
    }
}