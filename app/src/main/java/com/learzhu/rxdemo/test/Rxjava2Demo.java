package com.learzhu.rxdemo.test;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * ${className}.java是极搜浏览器的$DES$类。
 *
 * @author Learzhu
 * @version 3.0.0 2017/8/19 15:22
 * @update Learzhu 2017/8/19 15:22
 * @updateDes
 * @include {@link }
 * @used {@link }
 */


public class Rxjava2Demo {
    private static final String TAG = "Rxjava2Demo";

    public static void main(String args[]) {
        RxOne rxOne = new RxOne();
        rxOne.run();
    }
}

class RxOne {

    private static final String TAG = "RxOne";
    Observer<String> mObserver;
    Observable<String> mObservable;

    public RxOne() {
        //1) 创建 Observer
        mObserver = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i(TAG, "onSubscribe: " + d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };

        /**
         * 不仅基本使用方式一样，实质上，在 RxJava 的 subscribe 过程中，Observer 也总是会先被转换成一个 Subscriber 再使用。
         * 所以如果你只想使用基本功能，选择 Observer 和 Subscriber 是完全一样的。它们的区别对于使用者来说主要有两点：
         * <p>
         * onStart(): 这是 Subscriber 增加的方法。它会在 subscribe 刚开始，而事件还未发送之前被调用，
         * 可以用于做一些准备工作，例如数据的清零或重置。这是一个可选方法，默认情况下它的实现为空。
         * 需要注意的是，如果对准备工作的线程有要求（例如弹出一个显示进度的对话框，这必须在主线程执行），
         * onStart() 就不适用了，因为它总是在 subscribe 所发生的线程被调用，而不能指定线程。
         * 要在指定的线程来做准备工作，可以使用 doOnSubscribe() 方法，具体可以在后面的文中看到。
         * unsubscribe(): 这是 Subscriber 所实现的另一个接口 Subscription 的方法，用于取消订阅。
         * 在这个方法被调用后，Subscriber 将不再接收事件。一般在这个方法调用前，可以使用 isUnsubscribed() 先判断一下状态。
         * unsubscribe() 这个方法很重要，因为在 subscribe() 之后， Observable 会持有 Subscriber 的引用，这个引用如果不能及时被释放，
         * 将有内存泄露的风险。所以最好保持一个原则：要在不再使用的时候尽快在合适的地方（例如 onPause() onStop() 等方法中）
         * 调用 unsubscribe() 来解除引用关系，以避免内存泄露的发生。
         */
        Subscriber<String> mStringSubscriber = new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.i(TAG, "onSubscribe: " + s);
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG, "onError: " + t);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };


        //2) 创建 Observable  Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件。 RxJava 使用 create() 方法来创建一个 Observable ，并为它定义事件触发规则：
        mObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> subscriber) throws Exception {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
//            subscriber.onCompleted();
            }
        });

        //just(T...): 将传入的参数依次发送出来。
        Observable observable = Observable.just("Hello", "Hi", "Aloha");

        // from(T[]) / from(Iterable<? extends T>) : 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来。
        String[] words = {"Hello", "Hi", "Aloha"};
        //    Observable observable1 = Observable.from(words);
        Observable observable1 = Observable.fromArray(words);


    /*3) Subscribe (订阅)
    创建了 Observable 和 Observer 之后，再用 subscribe() 方法将它们联结起来，整条链子就可以工作了。
    */

        //    Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
//        @Override
//        public void call(Subscriber<? super String> subscriber) {
//            subscriber.onNext("Hello");
//            subscriber.onNext("Hi");
//            subscriber.onNext("Aloha");
//            subscriber.onCompleted();
//        }
//    });

        /**
         * // 注意：这不是 subscribe() 的源码，而是将源码中与性能、兼容性、扩展性有关的代码剔除后的核心代码。
         * // 如果需要看源码，可以去 RxJava 的 GitHub 仓库下载。
         * public Subscription subscribe(Subscriber subscriber) {
         * subscriber.onStart();
         * onSubscribe.call(subscriber);
         * return subscriber;
         * }
         * 可以看到，subscriber() 做了3件事：
         * <p>
         * 调用 Subscriber.onStart() 。这个方法在前面已经介绍过，是一个可选的准备方法。
         * 调用 Observable 中的 OnSubscribe.call(Subscriber) 。在这里，事件发送的逻辑开始运行。从这也可以看出，在 RxJava 中， Observable 并不是在创建的时候就立即开始发送事件，而是在它被订阅的时候，即当 subscribe() 方法执行的时候。
         * 将传入的 Subscriber 作为 Subscription 返回。这是为了方便 unsubscribe().
         */
    }

    public void run() {
        mObservable.subscribe(mObserver);
//            mObservable.subscribe(mStringSubscriber);

//            observable.subscribe(observer);
//            // 或者：
//            observable.subscribe(subscriber);
    }
}
