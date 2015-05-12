package jp.rough_diamond.sample.esb.service;

import java.util.*;

import javax.jws.WebService;
import jp.rough_diamond.framework.es.EnterpriseService;
import jp.rough_diamond.framework.es.ServiceConnecter;

@WebService(
    serviceName="SampleService2",
    name="SampleService2PortType",
    portName="SampleService2Port"
)
@SuppressWarnings("all")
public interface SampleService2 extends EnterpriseService {
    @ServiceConnecter(serviceName="SampleService2Connector")
    public jp.rough_diamond.sample.esb.service.Response calculate(
            List<jp.rough_diamond.sample.esb.service.Request> params
    );
}