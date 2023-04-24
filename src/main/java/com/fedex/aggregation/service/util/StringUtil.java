package com.fedex.aggregation.service.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class StringUtil {

    public static List<Long> getLongListFromString(String string) {
        Set<String> s = getStringSetFromString(string);
        s = s.stream().filter(StringUtils::isNumeric).collect(Collectors.toSet());
        return nonNull(string) && !string.isBlank() ? s.stream().map(Long::valueOf).collect(Collectors.toList()) : List.of();
    }

    public static Set<String> getStringSetFromString(String string) {
        return nonNull(string) && !string.isBlank() ? Arrays.stream(string.split(",")).collect(Collectors.toSet()) : Set.of();
    }

    public static String getConcatenatedStringFromList(List<String> strings) {
        return nonNull(strings) && !strings.isEmpty() ? String.join(",", strings) : null;
    }


}
