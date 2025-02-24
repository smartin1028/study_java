package org.study.green.generics;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedItem implements Delayed {
    private final long delayTime; // 지연시간
    private final long expire;  // 만료시간

    public DelayedItem(int delayInSeconds) {
        delayTime = delayInSeconds;
        expire = System.currentTimeMillis() + delayTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = expire - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.delayTime, ((DelayedItem) o).delayTime);
//        if (this.delayTime < ((DelayedItem) o).delayTime) {
//            return -1;
//        }
//        if (this.delayTime > ((DelayedItem) o).delayTime) {
//            return 1;
//        }
//        return 0;
    }
}
