import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BijectionGroup {
    // heap's algorithm
    private static <T> List<T[]> permutations(T[] arr) {
        List<T[]> result = new ArrayList<>();
        permutations(0, arr, result);
        return result;
    }

    private static <T> void permutations(int idx, T[] arr, List<T[]> result) {
        if (idx == arr.length - 1) {
            result.add(arr.clone());
        }

        for (int i = idx; i <= arr.length - 1; i++) {
            swap(arr, i, idx);
            permutations(idx + 1, arr, result);
            swap(arr, i, idx);
        }
    }

    private static <T> void swap(T[] arr, int i, int j) {
        if (i == j) return;
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // type Bijection<T> = Function<T, T>;
    public static <T> Set<Function<T, T>> bijectionsOf(Set<T> domain) {
        List<T> orderedDomain = domain.stream().sorted().collect(Collectors.toList());

        // SAFETY: orderedDomain is List<T>, so orderedDomain.toArray() is T[].
        @SuppressWarnings("unchecked") T[] orderedDomainArray = (T[]) orderedDomain.toArray();

        return permutations(orderedDomainArray).stream().map(perm -> (Function<T, T>) t -> perm[orderedDomain.indexOf(t)]).collect(Collectors.toSet());
    }

    public static void main(String... args) {
        Set<Integer> a_few = Stream.of(1, 2, 3).collect(Collectors.toSet());

        Set<Function<Integer, Integer>> bijections = bijectionsOf(a_few);
        bijections.forEach(aBijection -> {
            a_few.forEach(n -> System.out.printf("%d --> %d; ", n, aBijection.apply(n)));
            System.out.println();
        });


        Group<Function<Integer, Integer>> g = bijectionGroup(a_few);
        Function<Integer, Integer> f1 = bijectionsOf(a_few).stream().findFirst().get(); // happens to be identity for me
        Function<Integer, Integer> f2 = g.inverseOf(f1);
        Function<Integer, Integer> id = g.identity();

    }

    private static <T> Group<Function<T, T>> bijectionGroup(Set<T> domain) {
        return new Group<Function<T, T>>() {
            @Override
            public Function<T, T> binaryOperation(Function<T, T> one, Function<T, T> other) {
                // f, g -> f(g(x))
                return one.compose(other);
            }

            @Override
            public Function<T, T> identity() {
                return Function.identity();
            }

            @Override
            public Function<T, T> inverseOf(Function<T, T> ttFunction) {
                List<T> orderedDomain = domain.stream().sorted().collect(Collectors.toList());

                List<T> fValues = orderedDomain.stream().map(ttFunction).collect(Collectors.toList());
                return bijectionsOf(domain).stream().filter(biject -> {
                    List<T> gValues = fValues.stream().map(biject).collect(Collectors.toList());
                    return gValues.equals(orderedDomain);
                }).findFirst().get();
            }
        };
    }
}

