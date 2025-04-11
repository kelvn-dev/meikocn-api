package com.meikocn.api.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import org.springframework.util.StringUtils;

public class ConflictException extends VinposException {

  public ConflictException(Class clazz, String... searchParamsMap) {
    super(
        ConflictException.generateMessage(
            clazz.getSimpleName(), toMap(String.class, String.class, searchParamsMap)));
  }

  private static String generateMessage(String entity, Map<String, String> searchParams) {
    return StringUtils.capitalize(entity)
        .concat(" already exists with parameters ")
        .concat(searchParams.toString());
  }

  private static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object... entries) {
    if (entries.length % 2 == 1) {
      throw new IllegalArgumentException("Invalid entries");
    }
    return IntStream.range(0, entries.length / 2)
        .map(i -> i * 2)
        .collect(
            HashMap::new,
            (m, i) -> m.put(keyType.cast(entries[i]), valueType.cast(entries[i + 1])),
            Map::putAll);
  }
}
