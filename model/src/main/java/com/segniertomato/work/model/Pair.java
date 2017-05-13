package com.segniertomato.work.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;


public class Pair<F, S> {

    private final static Logger LOGGER = LogManager.getLogger();

    public final F first;
    public final S second;

    public Pair(F first, S second) {

        LOGGER.debug("constructor(F,S)");

        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {

        LOGGER.debug("equals(Object)");

        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {

        LOGGER.debug("hashCode()");
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {

        LOGGER.debug("toString()");
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
