package com.sssprog.instagramtest;

import junit.framework.AssertionFailedError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Observer;

/**
 * https://gist.github.com/ivacf/874dcb476bfc97f4d555
 */
public class RxAssertions {
 
    public static <T> ObservableAssertions<T> subscribeAssertingThat(Observable<T> observable) {
        return new ObservableAssertions<>(observable);
    }
 
    public static class ObservableAssertions<T> {
 
        private List<T> result;
        private Throwable error;
        private boolean completed;
 
        public ObservableAssertions(Observable<T> observable) {
            completed = false;
            result = new ArrayList<>();
            final CountDownLatch latch = new CountDownLatch(1);
            observable
                    .subscribe(new Observer<T>() {
                        @Override
                        public void onCompleted() {
                            completed = true;
                            latch.countDown();
                        }

                        @Override
                        public void onError(Throwable error) {
                            ObservableAssertions.this.error = error;
                            latch.countDown();
                        }

                        @Override
                        public void onNext(T item) {
                            result.add(item);
                        }
                    });
            while (latch.getCount() > 0) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
 
        public ObservableAssertions<T> assertCompleted() {
            if (!completed || error != null) {
                if (error != null) {
                    error.printStackTrace();
                }
                throw new AssertionFailedError("Observable has not completed successfully - cause: "
                        + (error != null ? error : "onComplete not called"));
            }
            return this;
        }
 
        public ObservableAssertions<T> assertError() {
            if (error == null) {
                throw new AssertionFailedError("Observable has not failed");
            }
            return this;
        }
 
        public ObservableAssertions<T> assertError(Throwable throwable) {
            assertError();
            if (!throwable.equals(error)) {
                throw new AssertionFailedError("Observable has failed with a different error," +
                        " expected is " + throwable + " but thrown was " + error);
            }
            return this;
        }

        public T single() {
            hasSize(1);
            return result.get(0);
        }

        public T first() {
            notEmpty();
            return result.get(0);
        }

        public ObservableAssertions<T> notEmpty() {
            if (result.isEmpty()) {
                throw new AssertionFailedError("result is empty");
            }
            return this;
        }
 
        public ObservableAssertions<T> hasSize(int numItemsExpected) {
            if (numItemsExpected != result.size()) {
                throw new AssertionFailedError("Observable has emitted " + result.size()
                        + " items but expected was " + numItemsExpected);
            }
            return this;
        }
 
        @SafeVarargs
        public final ObservableAssertions<T> emits(T... itemsExpected) {
            assertCompleted();
            assertEmittedEquals(itemsExpected);
            return this;
        }
 
        @SuppressWarnings("unchecked")
        public ObservableAssertions<T> emits(Collection<T> itemsExpected) {
            assertCompleted();
            assertEmittedEquals((T[]) itemsExpected.toArray());
            return this;
        }
 
        public ObservableAssertions<T> emitsNothing() {
            assertCompleted();
            if (result.size() > 0) {
                throw new AssertionFailedError("Observable has emitted " + result.size() + " items");
            }
            return this;
        }
 
        private void assertEmittedEquals(T[] itemsExpected) {
            hasSize(itemsExpected.length);
            for (int i = 0; i < itemsExpected.length; i++) {
                T expected = itemsExpected[i];
                T actual = result.get(i);
                if (!expected.equals(actual)) {
                    throw new AssertionFailedError("Emitted item in position " + i + " does not match," +
                            "  expected " + expected + " actual " + actual);
                }
            }
        }
 
    }
}
