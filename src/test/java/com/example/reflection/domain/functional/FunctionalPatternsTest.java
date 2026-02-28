package com.example.reflection.domain.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests that serve as executable documentation for {@link FunctionalPatterns}.
 * Each test demonstrates a specific functional programming concept in Java.
 */
class FunctionalPatternsTest {

    // ========== Callable ==========

    @Test
    void callable_executesAndReturnsValue() {
        // Callable<T> – no input, returns T, declares throws Exception
        Callable<String> callable = () -> "hello from callable";

        String result = FunctionalPatterns.callSafely(callable);

        assertThat(result).isEqualTo("hello from callable");
    }

    @Test
    void callable_wrapsCheckedExceptionAsRuntimeException() {
        // Callable is suited for operations that may throw a checked exception (I/O, JDBC, etc.)
        Callable<String> failing = () -> {
            throw new Exception("simulated io error");
        };

        assertThatThrownBy(() -> FunctionalPatterns.callSafely(failing))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Callable execution failed")
                .hasCauseInstanceOf(Exception.class);
    }

    // ========== Supplier ==========

    @Test
    void supplier_providesLazilyComputedValue() {
        // Supplier<T> – no input, returns T, no checked exception contract
        // Idiomatic use: Optional.orElseGet(Supplier), factory methods, lazy defaults
        Supplier<String> supplier = () -> "lazy value";

        String result = FunctionalPatterns.getOrDefault(supplier, "default");

        assertThat(result).isEqualTo("lazy value");
    }

    @Test
    void supplier_returnsDefaultWhenSupplierProvidesNull() {
        Supplier<String> nullSupplier = () -> null;

        String result = FunctionalPatterns.getOrDefault(nullSupplier, "default");

        assertThat(result).isEqualTo("default");
    }

    // ========== Function ==========

    @Test
    void function_transformsList() {
        // Function<T, R> transforms each element – here via a method reference
        List<String> words = List.of("hello", "world");

        List<String> result = FunctionalPatterns.transform(words, String::toUpperCase);

        assertThat(result).containsExactly("HELLO", "WORLD");
    }

    @Test
    void function_methodReferenceAndLambdaAreEquivalent() {
        // Method references are shorthand for lambdas; both create a Function instance
        List<Integer> numbers = List.of(1, 2, 3);

        List<String> viaMethodRef = FunctionalPatterns.transform(numbers, String::valueOf);
        List<String> viaLambda = FunctionalPatterns.transform(numbers, n -> String.valueOf(n));

        assertThat(viaMethodRef).isEqualTo(viaLambda);
    }

    // ========== Predicate ==========

    @Test
    void predicate_filtersList() {
        // Predicate<T> – a Function that returns boolean
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        List<Integer> evens = FunctionalPatterns.filter(numbers, n -> n % 2 == 0);

        assertThat(evens).containsExactly(2, 4);
    }

    @Test
    void predicate_composedWithAnd() {
        // Predicates compose without any extra helper code
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isGreaterThanThree = n -> n > 3;

        List<Integer> result = FunctionalPatterns.filter(numbers, isEven.and(isGreaterThanThree));

        assertThat(result).containsExactly(4, 6);
    }

    @Test
    void predicate_negated() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        Predicate<Integer> isEven = n -> n % 2 == 0;

        List<Integer> odds = FunctionalPatterns.filter(numbers, isEven.negate());

        assertThat(odds).containsExactly(1, 3, 5);
    }

    // ========== Consumer ==========

    @Test
    void consumer_processesEachElementAsSideEffect() {
        // Consumer<T> – takes input, returns nothing; used for side effects (logging, collecting)
        List<String> collected = new ArrayList<>();

        FunctionalPatterns.forEach(List.of("a", "b", "c"), collected::add);

        assertThat(collected).containsExactly("a", "b", "c");
    }

    // ========== Currying ==========

    @Test
    void curry_convertsBiFunctionIntoCurriedForm() {
        // Currying: f(a, b) becomes f(a)(b)
        // The result is a Function that returns another Function
        Function<Integer, Function<Integer, Integer>> curriedAdd =
                FunctionalPatterns.curry((a, b) -> a + b);

        assertThat(curriedAdd.apply(2).apply(3)).isEqualTo(5);
        assertThat(curriedAdd.apply(10).apply(7)).isEqualTo(17);
    }

    @Test
    void curry_partialApplicationFixesFirstArgument() {
        // Partial application: fix one argument, reuse the specialised function
        Function<String, Function<String, String>> curriedConcat =
                FunctionalPatterns.curry((prefix, s) -> prefix + s);

        Function<String, String> greet = curriedConcat.apply("Hello, ");

        assertThat(greet.apply("World")).isEqualTo("Hello, World");
        assertThat(greet.apply("Java")).isEqualTo("Hello, Java");
    }

    // ========== Function composition (pipeline) ==========

    @Test
    void pipeline_chainsTransformationsLeftToRight() {
        // Function.andThen composes left-to-right: trim first, then uppercase
        Function<String, String> trimThenUpper =
                FunctionalPatterns.pipeline(String::trim, String::toUpperCase);

        assertThat(trimThenUpper.apply("  hello  ")).isEqualTo("HELLO");
    }

    @Test
    void pipeline_builtInAndThenIsEquivalent() {
        // The built-in andThen on Function produces the same result
        Function<String, String> manual = FunctionalPatterns.pipeline(String::trim, String::toUpperCase);
        Function<String, String> builtin = ((Function<String, String>) String::trim).andThen(String::toUpperCase);

        String input = "  world  ";
        assertThat(manual.apply(input)).isEqualTo(builtin.apply(input));
    }
}
