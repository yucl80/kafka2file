package com.yucl.log.handle.async;

public interface KafkaProperties
{
  final static String zkConnect = "10.62.14.27:2181,10.62.14.10:2181,10.62.14.28:2181";
  final static  String groupId = "kafka2filedev";
  final static int connectionTimeOut = 100000;
  final static int reconnectInterval = 10000;
}
