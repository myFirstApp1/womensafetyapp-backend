package com.tl.womensafety.common.dto;

/**
 * Emergency contact for a user.
 */
public record Contact(
        String name,
        String phoneNumber,
        String relation,
        boolean primary
) {}
