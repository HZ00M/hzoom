package com.example.demo.schedule.server;

import com.example.demo.schedule.task.DefultOrderTask;
import com.example.demo.schedule.taskGroup.MapTaskGroup;
import com.example.demo.schedule.taskGroup.TaskGroup;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 *  查询调度控制服务
 */
//@Component
public class QueryServer {
    ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Scheduled(fixedRate = 20000)
    public void task1() throws Exception {
        System.out.println(" 定时任务执行 ");
        Function converter1 = (i) -> query1();
        Function<String, Optional> converter2 = this::query2 ;
        Function<String, Optional> converter3 = this::query3;
        Function<List<Optional<String>>, Optional> converter4 = this::query4;
        TaskGroup group = new MapTaskGroup<>();
        group.add(new DefultOrderTask<>(converter1, 1));
        group.add(new DefultOrderTask(converter2, 2));
        group.add(new DefultOrderTask(converter3, 2));
        group.add(new DefultOrderTask(converter4, 3));

        Future future = pool.submit(group);
        System.out.println(future.get());
    }
    public String query1() {
        return " 初始化完成 ";
    }

    public Optional query2(String str) {
        return Optional.ofNullable(" 查询用户成功 ");
    }

    public Optional query3(String str) {
        System.out.println(str);
        return Optional.ofNullable(" 查询用户权限成功 ");
    }

    public Optional query4(List<Optional<String>> optional) {
        String result = optional.stream().map(i->i.get()).reduce("", String::concat);
        return Optional.ofNullable("执行结果: "+result);
    }

}
