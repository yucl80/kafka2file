package com.yucl.log.handle.async;

public interface KafkaProperties
{
  final static String zkConnect = "192.168.21.12:2181,192.168.21.13:2181,192.168.21.14:2181";
  final static  String groupId = "kafka2filedev";
  final static int connectionTimeOut = 100000;
  final static int reconnectInterval = 10000;
}
