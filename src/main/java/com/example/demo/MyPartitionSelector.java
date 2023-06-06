package com.example.demo;

import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;

public class MyPartitionSelector implements PartitionSelectorStrategy {

    @Override
    public int selectPartition(Object key, int partitionCount) {
        // selecting always last partition to clearly show the issue
        return partitionCount - 1;
    }
}