package com.appskimo.app.japanese;

public final class On<T> {

    private ReadyListener readyListener;
    private SuccessListener<T> successListener;
    private CompleteListener<T> completeListener;
    private FailureListener failureListener;

    public void ready(){
        if (readyListener != null) {
            readyListener.onReady();
        }
    }

    public void success(T result){
        if (successListener != null) {
            successListener.onSuccess(result);
        }
    }

    public void complete(T result){
        if (completeListener != null) {
            completeListener.onComplete(result);
        }
    }

    public void failure(Throwable t){
        if (failureListener != null) {
            failureListener.onFailure(t);
        }
    }


    public On<T> addReadyListener(ReadyListener readyListener) {
        this.readyListener = readyListener;
        return this;
    }

    public On<T> addSuccessListener(SuccessListener<T> successListener) {
        this.successListener = successListener;
        return this;
    }

    public On<T> addCompleteListener(CompleteListener<T> completeListener) {
        this.completeListener = completeListener;
        return this;
    }

    public On<T> addFailureListener(FailureListener failureListener) {
        this.failureListener = failureListener;
        return this;
    }


    public interface ReadyListener {
        void onReady();
    }

    public interface SuccessListener<T> {
        void onSuccess(T result);
    }

    public interface CompleteListener<T> {
        void onComplete(T result);
    }

    public interface FailureListener {
        void onFailure(Throwable t);
    }

}
