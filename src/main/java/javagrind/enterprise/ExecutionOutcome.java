package javagrind.enterprise;

import java.util.Objects;
import java.util.function.Function;

/**
 * Java 17 `sealed` algebraic data type modeling operation outcomes without null returns
 * or unchecked control-flow exceptions.
 */
@EnterpriseSkill(
        category = "Modern Java 17",
        name = "Sealed Interfaces & Algebraic Data Types",
        summary = "Type-safe Result<T> hierarchy using `sealed interface` + generic `record` variants."
)
public sealed interface ExecutionOutcome<T>
        permits ExecutionOutcome.Success, ExecutionOutcome.Failure, ExecutionOutcome.RateLimited {

    record Success<T>(T value, long latencyMs) implements ExecutionOutcome<T> {}

    record Failure<T>(String errorCode, String message) implements ExecutionOutcome<T> {}

    record RateLimited<T>(String clientId, long retryAfterMs) implements ExecutionOutcome<T> {}

    default <R> R fold(
            Function<Success<T>, R> onSuccess,
            Function<Failure<T>, R> onFailure,
            Function<RateLimited<T>, R> onRateLimited) {
        Objects.requireNonNull(onSuccess);
        Objects.requireNonNull(onFailure);
        Objects.requireNonNull(onRateLimited);
        if (this instanceof Success<T> s) {
            return onSuccess.apply(s);
        } else if (this instanceof Failure<T> f) {
            return onFailure.apply(f);
        } else if (this instanceof RateLimited<T> r) {
            return onRateLimited.apply(r);
        }
        throw new IllegalStateException("Unreachable sealed variant");
    }
}
