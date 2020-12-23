package com.hzoom.demo.schedule.server;


import com.hzoom.demo.schedule.task.DefultOrderTask;
import com.hzoom.demo.schedule.task.OrderTask;
import com.hzoom.demo.schedule.taskGroup.ListTaskGroup;
import com.hzoom.demo.schedule.taskGroup.TaskGroup;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Test {
    public static void main(String[] args) throws Exception {
        Function<Optional<Integer>, Optional<Integer>> converter1 = (i) -> {return Optional.ofNullable(1);};
        Function<Optional<Integer>, Optional<Integer>> converter2 = (i) -> {return Optional.ofNullable(i.get()*5);};
        Function<Optional<Integer>, Optional<String>> converter3 = (i) -> {return Optional.ofNullable("第三阶段"+i.get());};
        Function<List, Optional<String>> converter4 = (i) -> {
            String s = "最终结果 ：";
            for (Object value :i){
                s+=value.toString();
            }
            return Optional.ofNullable(s);
        };
        OrderTask<Optional, Optional> task1 = new DefultOrderTask<>(converter1,1);
        OrderTask<Optional, Optional> task2 = new DefultOrderTask(converter2,2);
        OrderTask<Optional, Optional> task3 = new DefultOrderTask(converter3,3);
//        OrderTask<Optional,Optional> task4 = new DefultOrderTask(converter4,3);
        TaskGroup group = new ListTaskGroup<>();
        group.add(task1);
        group.add(task2);
        group.add(task3);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        FutureTask future = new FutureTask(group);
        service.scheduleWithFixedDelay(future, 0, 10, TimeUnit.SECONDS);

        System.out.println(future.isDone());
        System.out.println("main result  " + future.get());
        System.out.println(future.isDone());
        System.out.println("end");
    }
}
