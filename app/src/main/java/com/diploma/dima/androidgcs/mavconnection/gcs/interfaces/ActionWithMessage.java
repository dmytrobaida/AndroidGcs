package com.diploma.dima.androidgcs.mavconnection.gcs.interfaces;

public interface ActionWithMessage<T> {
    void handle(T message);
}
