package com.fedex.aggregation.service;

import static com.fedex.aggregation.service.util.StringUtil.getLongList;
import static com.fedex.aggregation.service.util.StringUtil.getStringSet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StringUtilUntilTest {


    @Test
    void testGetLongList() {
        var orderId1 = 123456789L;
        var orderId2 = 23456799L;
        var testString = List.of(orderId1, orderId2).stream().map(Object::toString).collect(Collectors.joining(","));
        List<Long> listOfStrings = getLongList(testString);
        Assertions.assertThat(listOfStrings).containsOnly(orderId1, orderId2);
    }

    @Test
    void testGetStringSet() {
        var orderId1 = "123456789";
        var orderId2 = "234567992";
        var testString = String.join(",", List.of(orderId1, orderId2));
        Set<String> listOfStrings = getStringSet(testString);
        Assertions.assertThat(listOfStrings).containsOnly(orderId1, orderId2);
    }
}
