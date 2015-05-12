package jp.rough_diamond.sample.esb.service.impl;

import java.util.*;

@SuppressWarnings("all")
public class SampleServiceImpl implements jp.rough_diamond.sample.esb.service.SampleService {
    public void foo(
    )
    {
    	System.out.println("call foo.");
    }
    public String sayHello(
            java.lang.String name
    )
    {
    	System.out.println("call sayHello.");
        return "Hello " + name + ".";
    }
}