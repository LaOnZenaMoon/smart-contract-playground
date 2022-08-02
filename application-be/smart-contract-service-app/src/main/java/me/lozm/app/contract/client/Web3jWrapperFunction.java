package me.lozm.app.contract.client;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface Web3jWrapperFunction<T, R> {

    R apply(T t) throws ExecutionException, InterruptedException;

}
