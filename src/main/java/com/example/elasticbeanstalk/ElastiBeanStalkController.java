package com.example.elasticbeanstalk;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElastiBeanStalkController {

    @GetMapping(value = "/hi", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String gettingStarted() {
        return "hello";
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBeanStalk() {
        //ElasticBeanStalk.createElasticBeanStalk("elasticBeanstalk");
        return new ResponseEntity<String>("EBS created", HttpStatus.CREATED);
    }
}
