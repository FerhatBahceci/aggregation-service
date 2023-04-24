package com.fedex.aggregation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static com.fedex.aggregation.service.util.StringUtil.*;

public class StringUtilUntilTest {
    private static final Long ORDER_ID_1 = 109347263L;
    private static final Long ORDER_ID_2 = 123456891L;
    private static final List<String> ORDER_IDS = List.of(ORDER_ID_1.toString(), ORDER_ID_2.toString());

    @Test
    void testGetLongListFromString() {
        String concatenatedStrings = getConcatenatedStringFromList(ORDER_IDS);
        List<Long> listOfLongs = getLongListFromString(concatenatedStrings);
        Assertions.assertThat(listOfLongs).containsOnly(ORDER_ID_1, ORDER_ID_2);
    }

    @Test
    void testGetStringSetFromString() {
        String concatenatedStrings = getConcatenatedStringFromList(ORDER_IDS);
        Set<String> setOfStrings = getStringSetFromString(concatenatedStrings);
        Assertions.assertThat(setOfStrings).containsAll(ORDER_IDS);
    }

    @Test
    void testGetStringFromList() {
        String orderId1 = ORDER_ID_1.toString();
        String orderId2 = ORDER_ID_2.toString();
        List<String> listOfStrings = List.of(orderId1, orderId2);
        var testString = getConcatenatedStringFromList(listOfStrings);
        Set<String> setOfStrings = getStringSetFromString(testString);
        Assertions.assertThat(setOfStrings).containsOnly(orderId1, orderId2);
    }

    @Test
    void testNull() {
        String concatenatedStrings = getConcatenatedStringFromList(null);
        Set<String> setOfStrings = getStringSetFromString(null);
        List<Long> listOfLongs = getLongListFromString(null);

        Assertions.assertThat(concatenatedStrings).isNull();
        Assertions.assertThat(setOfStrings).isEmpty();
        Assertions.assertThat(listOfLongs).isEmpty();
    }

    @Test
    void testEmpty() {
        String concatenatedStrings = getConcatenatedStringFromList(List.of());
        Set<String> setOfStrings = getStringSetFromString("");
        List<Long> listOfLongs = getLongListFromString("");

        Assertions.assertThat(concatenatedStrings).isNull();
        Assertions.assertThat(setOfStrings).isEmpty();
        Assertions.assertThat(listOfLongs).isEmpty();
    }
}
