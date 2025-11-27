package ru.ylab.tasks.task3.service.performance;

import java.util.function.Supplier;

/**
 * Сервис для измерения времени выполнения операций.
 * Используется для профилирования и оценки производительности отдельных участков кода.
 */
public class MetricService {

    /**
     * Измеряет время выполнения указанной операции и выводит результат в консоль.
     * @param task          операция, которую нужно выполнить (лямбда или метод)
     * @param operationName название операции (используется для вывода)
     * @param <T>           тип возвращаемого значения операции
     * @return результат выполнения task
     */
    public <T> T measureExecutionTime(Supplier<T> task, String operationName) {
        long start = System.currentTimeMillis();
        T result = task.get();
        long end = System.currentTimeMillis();

        long duration = end - start;
        System.out.println("Операция \"" + operationName + "\" выполнена за " + duration + " мс");
        return result;
    }
}
