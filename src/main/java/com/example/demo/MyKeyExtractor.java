package com.example.demo;

import org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy;
import org.springframework.messaging.Message;

public class MyKeyExtractor implements PartitionKeyExtractorStrategy {

    private static final String SOME_KEY = "someKey";

    @Override
    public Object extractKey(Message<?> message) {
        return SOME_KEY;
    }
}
