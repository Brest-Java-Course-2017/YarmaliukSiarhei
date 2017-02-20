package com.epam.trainning.util;


import java.util.Objects;

public final class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) obj;

        return Objects.equals(p.first, first) && Objects.equals(p.second, second);
    }

    @Override
    public String toString() {
        return "[ first: " + first.toString() + "; second: " + second.toString() + " ]";
    }
}
