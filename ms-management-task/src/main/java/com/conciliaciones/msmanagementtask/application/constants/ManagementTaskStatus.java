package com.conciliaciones.msmanagementtask.application.constants;

public final class ManagementTaskStatus {
    private ManagementTaskStatus() {}

    public static final String PENDING = "PENDING";
    public static final String SCHEDULED = "PENDING";
    public static final String PROCESS = "PROCESS";
    public static final String EXECUTED = "EXECUTED";
    public static final String CANCELLED = "CANCELLED";
    public static final String FAILED = "FAILED";
    public static final String WAITING = "PENDING";

    public static final String PLAN_STARTED = "PROCESS";
    public static final String PLAN_EXECUTING = "PROCESS";
    public static final String PLAN_EXECUTED = "EXECUTED";
    public static final String PLAN_CANCELLED = "CANCELLED";
    public static final String PLAN_FAILED = "FAILED";
}