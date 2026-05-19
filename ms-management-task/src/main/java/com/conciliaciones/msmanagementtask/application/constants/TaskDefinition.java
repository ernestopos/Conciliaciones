package com.conciliaciones.msmanagementtask.application.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum TaskDefinition {

    START_UPLOAD_DATA(1, "START_UPLOAD_DATA", "startUploadDataTask", "setData"),
    START_VALIDATE_DATA(2, "START_VALIDATE_DATA", "startValidateDataTask", "setData"),
    START_PROCESS_DATA(3, "START_PROCESS_DATA", "startProcessDataTask", "setData"),
    START_POLICY_RULES(4, "START_POLICY_RULES", "startPolicyRulesTask", "setData"),
    START_PAYMENT_PROCESS(5, "START_PAYMENT_PROCESS", "startPaymentProcessTask", "setData");

    private final int order;
    private final String parameterName;
    private final String beanName;
    private final String methodName;

    public Integer getOrder(){
        return this.order;
    }

    public static List<TaskDefinition> ordered() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(TaskDefinition::getOrder))
                .toList();
    }

    public TaskDefinition next() {
        return ordered().stream()
                .filter(task -> task.order == this.order + 1)
                .findFirst()
                .orElse(null);
    }

    public TaskDefinition previous() {
        return ordered().stream()
                .filter(task -> task.order == this.order - 1)
                .findFirst()
                .orElse(null);
    }

    public static TaskDefinition fromParameterName(String parameterName) {
        return Arrays.stream(values())
                .filter(task -> task.parameterName.equals(parameterName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de tarea no soportado: " + parameterName));
    }
}
