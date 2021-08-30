package com.xuanhuo.common.core.config;

import com.xuanhuo.common.core.builer.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统线程池配置
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor taskExecutor() {
        return ThreadPoolBuilder.create().setCorePoolSize(5).setMaxPoolSize(10).setWorkQueue(new LinkedBlockingQueue<>(100)).setThreadFactory(new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                String threadName = ThreadPoolBuilder.class.getSimpleName() + "-thread-" + count.addAndGet(1);
                t.setName(threadName);
                return t;

            }
        }).build();
    }
}
