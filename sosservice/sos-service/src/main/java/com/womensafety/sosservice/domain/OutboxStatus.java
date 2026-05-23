package com.womensafety.sosservice.domain;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED,
    RETRY,
    DLT
}
