package com.example;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MyClass {
    private static final String TAG = "MyClass";

    Observer<String> mObserver = new Observer<String>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
//            Log.d(TAG, "Item: " + s);
        }

        @Override
        public void onNext(@NonNull String s) {

        }

        @Override
        public void onError(@NonNull Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };

    public static void main(String args[]) {
    }
}
