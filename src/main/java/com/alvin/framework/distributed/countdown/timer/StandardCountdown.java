package com.alvin.framework.distributed.countdown.timer;

import com.alvin.framework.distributed.countdown.model.CountdownTimer;
import com.alvin.framework.distributed.countdown.service.DistributedLocker;
import com.alvin.framework.distributed.countdown.service.RedisOperator;

/**
 * datetime 2019/5/9 17:16
 *
 * @author sin5
 */
public class StandardCountdown implements Countdown {

    private DistributedLocker distributedLocker;
    private RedisOperator redisOperator;

    public StandardCountdown(DistributedLocker distributedLocker, RedisOperator redisOperator) {
        this.distributedLocker = distributedLocker;
        this.redisOperator = redisOperator;
    }

    @Override
    public boolean startCountdown(CountdownTimer timer) {
        if (!timer.initialized()) {
            timer.setRedisOperator(redisOperator);
            timer.setDistributedLocker(distributedLocker);
        }
        String name = timer.getName();
        try {
            distributedLocker.lock(name);
            if (timer.started()) {
                return false;
            }
            timer.start();
            return true;
        } finally {
            distributedLocker.unlock(name);
        }
    }

    @Override
    public void cancelCountdown(CountdownTimer timer) {
        if (timer.initialized()) {
            String name = timer.getName();
            try {
                distributedLocker.lock(name);
                if (timer.started()) {
                    timer.cancel();
                }
            } finally {
                distributedLocker.unlock(name);
            }
        }
    }

    @Override
    public void cancelCountdown(String timerName) {
        try {
            distributedLocker.lock(timerName);
            CountdownTimer.cancelSibling(redisOperator, timerName);
        } finally {
            distributedLocker.unlock(timerName);
        }
    }
}
