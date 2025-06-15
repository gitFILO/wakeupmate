package com.example.wakeupmate.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private static final Logger kafkaLogger = LoggerFactory.getLogger(KafkaController.class);

    @RequestMapping(method = RequestMethod.GET, path = "")
    public String loggingTest()
    {
        kafkaLogger.info("kafka-log-info");
        kafkaLogger.debug("kafka-log-debug");

        return "Test Page for logging: Success";
    }
}