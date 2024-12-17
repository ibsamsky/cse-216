# using python 3.12.7

from abc import ABC, abstractmethod
from typing import Callable, Set
from itertools import permutations


class Group[T](ABC):
    @abstractmethod
    def binary_operation(self, one: T, other: T) -> T: ...

    @abstractmethod
    def identity(self) -> T: ...

    @abstractmethod
    def inverse_of(self, tt_function: T) -> T: ...


# equivalent to Generic[T] syntax, not compatible with <=3.11
class BijectionGroup[T](Group[Callable[[T], T]]):
    __domain: Set[T] | None = None

    @staticmethod
    def bijection_group(domain: Set[T]) -> Group[Callable[[T], T]]:
        group = BijectionGroup[T]()
        group.__domain = domain
        return group

    @staticmethod
    def bijections_of(domain: Set[T]) -> Set[Callable[[T], T]]:
        # for each permutation of domain, create functions
        # domain[i] -> perm[i] for each i in domain
        domain_ordered = [*domain]
        return {
            *map(
                lambda p: lambda x: p[domain_ordered.index(x)],
                permutations(domain_ordered),
            )
        }

    def binary_operation(
        self, one: Callable[[T], T], other: Callable[[T], T]
    ) -> Callable[[T], T]:
        def composed(x: T) -> T:
            return one(other(x))

        return composed

    def identity(self) -> Callable[[T], T]:
        def id(t: T) -> T:
            return t

        return id

    def inverse_of(self, tt_function: Callable[[T], T]) -> Callable[[T], T]:
        if self.__domain is None:
            raise ValueError("Domain not set")

        ordered_domain = [*self.__domain]
        f_vals = [tt_function(x) for x in ordered_domain]
        return [
            f
            for f in self.bijections_of(self.__domain)
            if all(f(v) == x for v, x in zip(f_vals, ordered_domain))
        ][0]
