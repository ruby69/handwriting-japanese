package com.appskimo.app.japanese.support;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import org.greenrobot.eventbus.EventBus;

public class EventBusObserver {

    public static class AtCreateDestroy implements LifecycleObserver {
        private Object subscriber;

        public AtCreateDestroy(Object subscriber) {
            this.subscriber = subscriber;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate(){
            if (!EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().register(subscriber);
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    public static class AtResumePause implements LifecycleObserver {
        private Object subscriber;

        public AtResumePause(Object subscriber) {
            this.subscriber = subscriber;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume(){
            if (!EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().register(subscriber);
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    public static class AtStartStop implements LifecycleObserver {
        private Object subscriber;

        public AtStartStop(Object subscriber) {
            this.subscriber = subscriber;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onStart(){
            if (!EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().register(subscriber);
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onStop() {
            EventBus.getDefault().unregister(subscriber);
        }
    }
}
