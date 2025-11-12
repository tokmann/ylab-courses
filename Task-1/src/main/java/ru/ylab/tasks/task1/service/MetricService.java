package ru.ylab.tasks.task1.service;

import java.util.function.Supplier;

public class MetricService {

    public <T> T measureExecutionTime(Supplier<T> task, String operationName) {
        long start = System.currentTimeMillis();
        T result = task.get();
        long end = System.currentTimeMillis();

        long duration = end - start;
        System.out.println("Операция \"" + operationName + "\" выполнена за " + duration + " мс");
        return result;
    }
}
