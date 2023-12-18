package com.study.train.batch.feign;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "business",url = "http://localhost:8002/business")
public class BussinessFeign {

}
