package com.learn.kafka;

import kafka.producer.Partitioner;

public class SimplePartitioner implements Partitioner {
    public int partition(Object key, int numPartitions) {
        int partition = 0;
        int iKey = (Integer)key;
        if (iKey > 0) {
           partition = iKey % numPartitions;
        }
       return partition;
  }
}
