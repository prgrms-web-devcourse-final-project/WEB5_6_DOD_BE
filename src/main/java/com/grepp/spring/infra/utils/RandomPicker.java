package com.grepp.spring.infra.utils;

import java.util.List;
import java.util.Random;

public class RandomPicker {

    private static final Random RANDOM = new Random();

    public static <T> T pickRandom(List<T> members) {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("리스트가 비어 있어서 랜덤 추출이 불가능합니다.");
        }
        int randomIndex = RANDOM.nextInt(members.size());
        return members.get(randomIndex);
    }
}
