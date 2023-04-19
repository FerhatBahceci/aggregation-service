package com.fedex.aggregation.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StringUtil {

    public static Set<String> getSet(String string) {
        return Arrays.stream(string.split(",")).collect(Collectors.toSet());
    }

    public static List<Long> getList(String string) {
        return new ArrayList<>(Arrays.stream(string.split(",")).map(Long::valueOf).collect(Collectors.toSet()));
    }
}
