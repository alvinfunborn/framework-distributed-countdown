package com.alvin.framework.distributed.countdown.model;

import com.alvin.framework.distributed.countdown.service.DistributedLocker;
import com.alvin.framework.distributed.countdown.service.RedisOperator;

import java.util.concurrent.TimeUnit;

/**
 * datetime 2019/5/9 17:12
 *
 * @author sin5
 */
public abstract class CountdownTimer {
    private static final String REDIS_KEY_COUNTDOWN_PREFIX = "countdown:%s";

    private String name;
    private long duration;
    private RedisOperator redisOperator;
    private DistributedLocker distributedLocker;

    private Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
            }
            distributedLocker.lock(name);
            if (!canceled() && !started()) {
                onCountdown();
            }
            distributedLocker.unlock(name);
        }
    });

    public CountdownTimer(String name, long duration, TimeUnit timeUnit) {
        this.name = name;
        this.duration = timeUnit.toMillis(duration);
    }

    public void setRedisOperator(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    public void setDistributedLocker(DistributedLocker distributedLocker) {
        this.distributedLocker = distributedLocker;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public boolean initialized() {
        return redisOperator != null && distributedLocker != null;
    }

    public void start() {
        redisOperator.set(String.format(REDIS_KEY_COUNTDOWN_PREFIX, name), "started", duration, TimeUnit.MILLISECONDS);
        t.start();
        onStart();
    }

    public void cancel() {
        t.interrupt();
        redisOperator.set(String.format(REDIS_KEY_COUNTDOWN_PREFIX, name), "canceled", duration, TimeUnit.MILLISECONDS);
    }

    public boolean started() {
        String value = redisOperator.get(String.format(REDIS_KEY_COUNTDOWN_PREFIX, name));
        return "started".equals(value);
    }

    private boolean canceled() {
        String value = redisOperator.get(String.format(REDIS_KEY_COUNTDOWN_PREFIX, name));
        return "canceled".equals(value);
    }

    public static void cancelSibling(RedisOperator redisOperator, String name) {
        redisOperator.delete(String.format(REDIS_KEY_COUNTDOWN_PREFIX, name));
        redisOperator.set(String.format(REDIS_KEY_COUNTDOWN_PREFIX, name), "canceled");
    }

    abstract void onStart();

    abstract void onCountdown();
}
