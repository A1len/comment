package com.task.timewheel;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ScheduleTest {



    public static void main(String[] args) {
        TimedTask timedTask = new TimedTask(10000, () -> {
            log.info("111111111");
        });
        System.out.println(timedTask.getExpireTimestamp());
        TimedTask timedTask2 = new TimedTask(10000, () -> {
            log.info("222222222");
        });
        Timer.getInstance().addTask(timedTask);
        Timer.getInstance().addTask(timedTask2);
       // private DelayQueue<Bucket> delayQueue = new DelayQueue<>();

        }
    }

