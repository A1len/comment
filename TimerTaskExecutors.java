package com.task.timewheel;



import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Created by Anur IjuoKaruKas on 2/1/2019
 */
@Slf4j
public class TimerTaskExecutors {


    /**
     * 统一管理的线程池
     */
    private static ExecutorService Pool;

    /**
     * 线程池的 Queue
     */
    private static BlockingDeque<Runnable> MissionQueue;

    static {
        // 任务可无限堆积
        MissionQueue = new LinkedBlockingDeque<>();

        int coreCount = Runtime.getRuntime()
                .availableProcessors();
        int threadCount = coreCount * 2;
        log.info("创建时间轮线程池 => 机器核心数为 {}, 故创建线程 {} 个", coreCount, threadCount);
        Pool = new _TimerTaskExecutors(threadCount, threadCount, 5, TimeUnit.MILLISECONDS, MissionQueue, new ThreadFactoryBuilder().setNameFormat("Timer-Task-Pool")
                .setDaemon(true)
                .build());
    }

    public static void execute(Runnable runnable) {
        Pool.execute(runnable);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return Pool.submit(task);
    }

    public static int getBlockSize() {
        return MissionQueue.size();
    }

    public static class _TimerTaskExecutors extends ThreadPoolExecutor {

        public _TimerTaskExecutors(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (t == null && r instanceof Future<?>) {
                try {
                    Future<?> future = (Future<?>) r;
                    if (future.isDone()) {
                        future.get();
                    }
                } catch (CancellationException ce) {
                    t = ce;
                } catch (ExecutionException ee) {
                    t = ee.getCause();
                } catch (InterruptedException ie) {
                    Thread.currentThread()
                            .interrupt();
                }
            }
            if (t != null) {
                log.error(t.getMessage(), t);
            }
        }
    }
}
