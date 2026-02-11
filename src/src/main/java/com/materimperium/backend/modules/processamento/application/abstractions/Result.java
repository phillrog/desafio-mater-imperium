package com.materimperium.backend.modules.processamento.application.abstractions;

import java.util.Collections;
import java.util.List;

public record Result<T>(
        T value,
        List<String> errors,
        boolean isSuccess
) {
    public static <T> Result<T> success(T value) {
        return new Result<>(value, Collections.emptyList(), true);
    }

    public static <T> Result<T> failure(String error) {
        return new Result<>(null, List.of(error), false);
    }

    public static <T> Result<T> failure(List<String> errors) {
        return new Result<>(null, errors, false);
    }
}