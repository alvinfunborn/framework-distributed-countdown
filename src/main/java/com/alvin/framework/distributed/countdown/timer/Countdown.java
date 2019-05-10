package com.alvin.framework.distributed.countdown.timer;

import com.alvin.framework.distributed.countdown.model.CountdownTimer;

/**
 * datetime 2019/5/9 17:03
 *
 * @author sin5
 */
public interface Countdown {

    boolean startCountdown(CountdownTimer timer);

    void cancelCountdown(CountdownTimer timer);

    void cancelCountdown(String timerName);
}
