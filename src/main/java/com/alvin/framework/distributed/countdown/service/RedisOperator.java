package com.alvin.framework.distributed.countdown.service;

import java.util.concurrent.TimeUnit;

/**
 * datetime 2019/5/10 10:12
 *
 * @author sin5
 */
public interface RedisOperator {

    void set(String key, String value);

    void set(String key, String value, long ttl, TimeUnit timeUnit);

    String get(String key);

    void delete(String key);
}
