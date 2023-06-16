package icu.uun.starter;

@FunctionalInterface
public interface BuildRegistry<T> {
    void register(T registry);
}
