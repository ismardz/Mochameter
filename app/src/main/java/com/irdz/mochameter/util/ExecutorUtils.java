package com.irdz.mochameter.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {

    private static final Integer MAX_THREADS = 10;
    private static final long TIMEOUT_SECONDS = 10;

    /**
     * Execute a list of callables asynchronaly
     * @param callables list of threads to execute
     * @param <T> class representing the object returned by the callable
     * @return list of futures with result of each Callable
     */
    public static <T> List<T> runCallables(final Callable<T>... callables) throws RuntimeException {
        return runCallables(Optional.ofNullable(callables.length)
                .filter(size -> size < MAX_THREADS)
                .orElse(MAX_THREADS),
                callables);
    }
    /**
     * Execute a list of callables asynchronaly
     * @param callables list of threads to execute
     * @param maximumExecutions maximum number of paralel threads
     * @param <T> class representing the object returned by the callable
     * @return list of futures with result of each Callable
     */
    public static <T> List<T> runCallables(
            final int maximumExecutions,
            final Callable<T>... callables
    ) throws RuntimeException {
        List<T> results = new ArrayList<>();
        List<Future<T>> futures = null;
        if (callables.length > 0) {
            ExecutorService executor = Executors.newFixedThreadPool(maximumExecutions);
            try {
                futures = executor.invokeAll(Arrays.asList(callables));
            } catch (InterruptedException e) {
                Log.e(TAG,"Error running callables", e);
                Thread.currentThread().interrupt();
            }
            awaitTerminationAfterShutdown(executor);
            for (Future<T> future : futures) {
                try {
                    results.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG,"Error running callables", e);
                    if(e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    throw new RuntimeException("Error thrown by one of the threads", e.getCause());
                }
            }
        }
        return Optional.ofNullable(results)
                .orElse(Collections.emptyList());
    }
    private static void awaitTerminationAfterShutdown(final ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Log.e(TAG,"Error running callables", e);
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
