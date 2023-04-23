package com.fedex.aggregation.service;

import static com.fedex.aggregation.service.util.StringUtil.getLongList;
import static com.fedex.aggregation.service.util.StringUtil.getStringSet;

import com.fedex.aggregation.service.util.StringUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtilUntilTest {
    private static final Long ORDER_ID_1 = 109347263L;
    private static final Long ORDER_ID_2 = 123456891L;

    @Test
    void testGetLongList() {
        var testString = Stream.of(ORDER_ID_1, ORDER_ID_2).map(Object::toString).collect(Collectors.joining(","));
        List<Long> listOfStrings = getLongList(testString);
        Assertions.assertThat(listOfStrings).containsOnly(ORDER_ID_1, ORDER_ID_2);
    }

    @Test
    void testGetStringSet() {
        var orderId1 = ORDER_ID_1.toString();
        var orderId2 = ORDER_ID_2.toString();
        var testString = String.join(",", List.of(orderId1, orderId2));
        Set<String> listOfStrings = getStringSet(testString);
        Assertions.assertThat(listOfStrings).containsOnly(orderId1, orderId2);
    }

    @Test
    void testGetStringFromList() {
        var orderId1 = ORDER_ID_1.toString();
        var orderId2 = ORDER_ID_2.toString();
        var testString = StringUtil.getString(List.of(ORDER_ID_1.toString(), ORDER_ID_2.toString()));
        Set<String> listOfStrings = getStringSet(testString);
        Assertions.assertThat(listOfStrings).containsOnly(orderId1, orderId2);
    }
}
