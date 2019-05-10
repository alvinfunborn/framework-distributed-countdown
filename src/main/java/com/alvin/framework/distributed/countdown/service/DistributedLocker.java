package com.alvin.framework.distributed.countdown.service;

/**
 * datetime 2019/5/9 17:17
 *
 * @author sin5
 */
public interface DistributedLocker {

    void lock(String name);

    void unlock(String name);
}
