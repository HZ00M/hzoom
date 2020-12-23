package com.hzoom.core.completableFuture;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CompletableFutureTest {

    /**
     * runAsync方法可以在后台执行异步计算，但是此时并没有返回值。持有一个Runnable对象。
     */
    @Test
    public void test() {
        CompletableFuture noReturn = CompletableFuture.runAsync(() -> {
            log.info("no return value");
        });
    }

    /**
     * 传入相应任务,有返回值
     */
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> have_return_value = CompletableFuture.supplyAsync(this::get);
        log.info(have_return_value.getNow(null));
    }

    /**
     * Supplier<T>
     *
     * @return
     */
    private String get() {
        log.info("Begin Invoke getFuntureHasReturnLambda");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {

        }
        log.info("End Invoke getFuntureHasReturnLambda");
        return "hasReturnLambda";
    }

    /**
     * 按顺序执行异步任务
     * <p>
     * 方法名	是否可获得前一个任务的返回值	是否有返回值
     * thenApply	能获得	有
     * thenAccept	能获得	无
     * thenRun	不可获得	无
     */
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        //thenApply  可获取到前一个任务的返回值,也有返回值
        CompletableFuture<String> seqFutureOne = CompletableFuture.supplyAsync(()-> "seqFutureOne");
        CompletableFuture<String> seqFutureTwo = seqFutureOne.thenApply(name -> name + " seqFutureTwo");
        log.info(seqFutureTwo.get());


        //thenAccept  可获取到前一个任务的返回值,但是无返回值
        CompletableFuture<Void> thenAccept = seqFutureOne.thenAccept(name -> log.info(name + "thenAccept"));
        log.info("-------------");
        log.info(String.valueOf(thenAccept.get()));

        //thenRun 获取不到前一个任务的返回值,也无返回值
        log.info("-------------");
        CompletableFuture<Void> thenRun = seqFutureOne.thenRun(() -> {
            log.info("thenRun");
        });
    }

    /**
     * thenApply和thenApplyAsync的区别
     *
     * 这两个方法区别就在于谁去执行这个任务，如果使用thenApplyAsync，
     * 那么执行的线程是从ForkJoinPool.commonPool()中获取不同的线程进行执行，
     * 如果使用thenApply，如果supplyAsync方法执行速度特别快，
     * 那么thenApply任务就是主线程进行执行，如果执行特别慢的话就是和supplyAsync执行线程一样
     */
    @Test
    public void test3() throws ExecutionException, InterruptedException {
        //thenApply和thenApplyAsync的区别
        System.out.println("-------------Sleep");
        CompletableFuture<String> supplyAsyncWithSleep = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "supplyAsyncWithSleep Thread Id : " + Thread.currentThread();
        });
        CompletableFuture<String> thenApply = supplyAsyncWithSleep
                .thenApply(name -> name + "------thenApply Thread Id : " + Thread.currentThread());
        CompletableFuture<String> thenApplyAsync = supplyAsyncWithSleep
                .thenApplyAsync(name -> name + "------thenApplyAsync Thread Id : " + Thread.currentThread());
        System.out.println("Main Thread Id: "+ Thread.currentThread());
        System.out.println(thenApply.get());
        System.out.println(thenApplyAsync.get());
        System.out.println("-------------No Sleep");
        CompletableFuture<String> supplyAsyncNoSleep = CompletableFuture.supplyAsync(()->{
            return "supplyAsyncNoSleep Thread Id : " + Thread.currentThread();
        });
        CompletableFuture<String> thenApplyNoSleep = supplyAsyncNoSleep
                .thenApply(name -> name + "------thenApply Thread Id : " + Thread.currentThread());
        CompletableFuture<String> thenApplyAsyncNoSleep = supplyAsyncNoSleep
                .thenApplyAsync(name -> name + "------thenApplyAsync Thread Id : " + Thread.currentThread());
        System.out.println("Main Thread Id: "+ Thread.currentThread());
        System.out.println(thenApplyNoSleep.get());
        System.out.println(thenApplyAsyncNoSleep.get());
    }

    /**
     * 组合CompletableFuture
     * 将两个CompletableFuture组合到一起有两个方法
     * thenCompose()：当第一个任务完成时才会执行第二个操作
     * thenCombine()：两个异步任务全部完成时才会执行某些操作
     */
    @Test
    public void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<String> thenComposeComplet = getTastOne().thenCompose(CompletableFutureTest::getTastTwo);
        System.out.println(thenComposeComplet.get());

        //thenApply()类似实现
        CompletableFuture<CompletableFuture<String>> thenApply = getTastOne()
                .thenApply(CompletableFutureTest::getTastTwo);
        System.out.println(thenApply.get().get());
    }
    public static CompletableFuture<String> getTastOne(){
        return CompletableFuture.supplyAsync(()-> "topOne");
    }
    public static CompletableFuture<String> getTastTwo(String s){
        return CompletableFuture.supplyAsync(()-> s + "  topTwo");
    }

    @Test
    public void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> thenComposeOne = CompletableFuture.supplyAsync(() -> 192);
        CompletableFuture<Integer> thenComposeTwo = CompletableFuture.supplyAsync(() -> 196);
        CompletableFuture<Integer> thenComposeCount = thenComposeOne
                .thenCombine(thenComposeTwo, (s, y) -> s + y);
        System.out.println(thenComposeCount.get());
    }

    /**
     * 组合多个CompletableFuture
     * allOf()：等待所有CompletableFuture完后以后才会运行回调函数
     * anyOf()：只要其中一个CompletableFuture完成，那么就会执行回调函数。注意此时其他的任务也就不执行了。
     */
    @Test
    public void allOf(){
        //allOf()
        CompletableFuture<Integer> one = CompletableFuture.supplyAsync(() -> {System.out.println("1");return 1;});
        CompletableFuture<Integer> two = CompletableFuture.supplyAsync(() -> {System.out.println("2");return 2;});
        CompletableFuture<Integer> three = CompletableFuture.supplyAsync(() -> {System.out.println("3");return 3;});
        CompletableFuture<Integer> four = CompletableFuture.supplyAsync(() -> {System.out.println("4");return 4;});
        CompletableFuture<Integer> five = CompletableFuture.supplyAsync(() -> {System.out.println("5");return 5;});
        CompletableFuture<Integer> six = CompletableFuture.supplyAsync(() -> {System.out.println("6");return 6;});

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(one, two, three, four, five, six);
        voidCompletableFuture.thenApply(v->{
            return Stream.of(one,two,three,four, five, six)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        }).thenAccept(System.out::println);
    }

    @Test
    public void anyOf() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> one = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1");return 1;});
        CompletableFuture<Integer> two = CompletableFuture.supplyAsync(() -> {try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }System.out.println("2");return 2;});
        CompletableFuture<Integer> three = CompletableFuture.supplyAsync(() -> {try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }System.out.println("3");return 3;});
        CompletableFuture<Integer> four = CompletableFuture.supplyAsync(() -> {try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }System.out.println("4");return 4;});
        CompletableFuture<Integer> five = CompletableFuture.supplyAsync(() -> {try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }System.out.println("5");return 5;});
        CompletableFuture<Integer> six = CompletableFuture.supplyAsync(() -> {System.out.println("6");return 6;});

        CompletableFuture anyOf = CompletableFuture.anyOf(one, two, three, four, five, six);
        System.out.println(anyOf.get());

    }

    /**
     * 异常处理
     */
    @Test
    public void exceptionally() throws ExecutionException, InterruptedException {
        //执行链中有一个发生了异常，那么接下来的链条也就不执行了，但是主流程下的其他CompletableFuture还是会运行的。
        CompletableFuture.supplyAsync(()->{
            //发生异常
            int i = 10/0;
            return "Success";
        }).thenRun(()-> System.out.println("thenRun"))
                .thenAccept(v -> System.out.println("thenAccept"));

        CompletableFuture.runAsync(()-> System.out.println("CompletableFuture.runAsync"));

        //处理异常

        CompletableFuture<String> exceptionally = CompletableFuture.supplyAsync(() -> {
            //发生异常
            int i = 10 / 0;
            return "Success";
        }).exceptionally(e -> {
            log.error(e.getMessage());
            return "Exception has Handl";
        });
        System.out.println(exceptionally.get());
    }

    /**
     * handle()
     * 调用handle()方法也能够捕捉到异常并且自定义返回值，他和exceptionally()方法不同一点是handle()方法无论发没发生异常都会被调用。例子如下
     */
    @Test
    public void handle(){
        System.out.println("-------有异常-------");
        CompletableFuture.supplyAsync(()->{
            //发生异常
            int i = 10/0;
            return "Success";
        }).handle((response,e)->{
            System.out.println("Exception:" + e);
            System.out.println("Response:" + response);
            return response;
        });

        System.out.println("-------无异常-------");
        CompletableFuture.supplyAsync(()->{
            return "Sucess";
        }).handle((response,e)->{
            System.out.println("Exception:" + e);
            System.out.println("Response:" + response);
            return response;
        });
    }
}
