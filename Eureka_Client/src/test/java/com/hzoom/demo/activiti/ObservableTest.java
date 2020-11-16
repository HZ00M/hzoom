package com.hzoom.demo.activiti;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;

public class ObservableTest {
    @Test
    public void test(){
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()){
                    subscriber.onNext("我很好");
                    subscriber.onCompleted();
                }
            }
        });
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("完成");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }
        };
        observable.subscribe(subscriber);
    }
}
