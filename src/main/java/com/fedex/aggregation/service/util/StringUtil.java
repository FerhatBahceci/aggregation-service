package com.fedex.aggregation.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class StringUtil {

    public static Set<String> getStringSetFromString(String string) {
       return nonNull(string) && !string.isBlank() ? Arrays.stream(string.split(",")).collect(Collectors.toSet()) : Set.of();
    }
    public static String getConcatenatedStringFromList(List<String> strings) {
        return nonNull(strings) && !strings.isEmpty() ?  String.join(",", strings) : null;
    }

    public static List<Long> getLongListFromString(String string) {
        return nonNull(string) && !string.isBlank() ? new ArrayList<>(Arrays.stream(string.split(",")).map(Long::valueOf).collect(Collectors.toSet())) : List.of();
    }
}
