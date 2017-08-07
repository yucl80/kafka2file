package com.yucl.log.handle.async;

public interface KafkaProperties
{
  final static String zkConnect = "127.0.0.1";
  final static  String groupId = "log2file";
  final static int connectionTimeOut = 100000;
  final static int reconnectInterval = 10000;
}
