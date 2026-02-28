package com.example.reflection.domain.functional;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Demonstrates functional programming paradigms available in Java.
 *
 * <h2>Why not pass functions directly?</h2>
 * <p>Java is statically typed and object-oriented. Functions must be described by a type.
 * Java uses <em>functional interfaces</em> – interfaces with a single abstract method (SAM) –
 * as that type. Lambda expressions and method references are concise syntax for implementing
 * these interfaces; under the hood they produce an instance of the functional interface.</p>
 *
 * <h2>Key built-in functional interfaces</h2>
 * <ul>
 *   <li>{@link Runnable}   – {@code () -> void}         no input, no output, no checked exception</li>
 *   <li>{@link Callable}   – {@code () -> T throws E}   no input, returns T, <em>may throw checked exception</em></li>
 *   <li>{@link Supplier}   – {@code () -> T}            no input, returns T, no checked exception</li>
 *   <li>{@link Consumer}   – {@code T -> void}          takes input, produces side effect</li>
 *   <li>{@link Function}   – {@code T -> R}             transforms one type to another</li>
 *   <li>{@link Predicate}  – {@code T -> boolean}       tests a condition</li>
 *   <li>{@link BiFunction} – {@code (T, U) -> R}        takes two inputs</li>
 * </ul>
 *
 * <h2>Callable vs Supplier</h2>
 * <p>Both produce a value with no arguments. The key difference:</p>
 * <ul>
 *   <li>{@link Callable} declares {@code throws Exception} – use it for I/O or concurrent tasks
 *       ({@code ExecutorService.submit(Callable)}).</li>
 *   <li>{@link Supplier} has no checked-exception contract – use it for pure computations,
 *       lazy defaults ({@code Optional.orElseGet}), and dependency injection.</li>
 * </ul>
 *
 * <h2>Currying</h2>
 * <p>Currying converts {@code f(a, b)} into {@code f(a)(b)}: a function that takes one
 * argument and returns another function awaiting the remaining argument. This enables
 * <em>partial application</em> – fixing one argument and reusing the specialised function.</p>
 */
public final class FunctionalPatterns {

    private FunctionalPatterns() {
    }

    /**
     * Executes a {@link Callable} safely, wrapping any checked exception in a
     * {@link RuntimeException}.
     *
     * <p>Callable is the right choice when the supplied computation may throw a checked
     * exception (e.g. file I/O, network calls, JDBC). It is also the interface expected by
     * {@code ExecutorService.submit()} for concurrent tasks.</p>
     */
    public static <T> T callSafely(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Callable execution failed", e);
        }
    }

    /**
     * Returns the value from the {@link Supplier}, or {@code defaultValue} when the supplier
     * returns {@code null}.
     *
     * <p>Supplier is the right choice for lazy evaluation when no checked exception is expected:
     * {@code Optional.orElseGet(Supplier)}, factory methods, and deferred computation. Unlike
     * Callable it cannot signal a checked exception, keeping call-sites simpler.</p>
     */
    public static <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        T value = supplier.get();
        return value != null ? value : defaultValue;
    }

    /**
     * Applies a {@link Function} to every element in the list and returns the results.
     *
     * <p>Method references (e.g. {@code String::toUpperCase}) and lambdas are interchangeable
     * ways to supply the function; both compile to an instance of {@code Function<T,R>}.</p>
     */
    public static <T, R> List<R> transform(List<T> items, Function<T, R> mapper) {
        return items.stream().map(mapper).toList();
    }

    /**
     * Returns the subset of elements that satisfy the {@link Predicate}.
     *
     * <p>Predicates compose via {@link Predicate#and}, {@link Predicate#or}, and
     * {@link Predicate#negate} without requiring extra helper methods.</p>
     */
    public static <T> List<T> filter(List<T> items, Predicate<T> predicate) {
        return items.stream().filter(predicate).toList();
    }

    /**
     * Applies a {@link Consumer} to every element in the list.
     *
     * <p>Consumer represents a side-effecting operation (logging, collecting, printing)
     * that consumes its argument but produces no return value.</p>
     */
    public static <T> void forEach(List<T> items, Consumer<T> action) {
        items.forEach(action);
    }

    /**
     * Curries a {@link BiFunction}, returning a {@link Function} that accepts the first
     * argument and returns a new {@code Function} awaiting the second argument.
     *
     * <p>Currying is fully supported in Java via nested lambdas. Partial application lets
     * you specialise a general function once and reuse the result many times:</p>
     *
     * <pre>{@code
     * Function<Integer, Function<Integer, Integer>> add = curry((a, b) -> a + b);
     * Function<Integer, Integer> add5 = add.apply(5);   // first argument fixed
     * add5.apply(3);   // 8
     * add5.apply(10);  // 15
     * }</pre>
     */
    public static <A, B, R> Function<A, Function<B, R>> curry(BiFunction<A, B, R> biFunction) {
        return a -> b -> biFunction.apply(a, b);
    }

    /**
     * Composes two functions into a single pipeline: applies {@code first}, then {@code second}.
     *
     * <p>Equivalent to {@code first.andThen(second)}. Java's {@link Function} interface provides
     * {@code andThen} (left-to-right) and {@code compose} (right-to-left) as built-in
     * combinators.</p>
     */
    public static <T, R, V> Function<T, V> pipeline(Function<T, R> first, Function<R, V> second) {
        return first.andThen(second);
    }
}
