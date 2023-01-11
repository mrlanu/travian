package io.lanu.travian.game.models.battle;

@FunctionalInterface
public interface ZipWith<A, B, C, R> {
    R apply(A a, B b, C c);
}
