package com.meikocn.api.utils;

import com.querydsl.core.types.dsl.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class PredicateUtils {

  private PredicateUtils() {}

  public static BooleanExpression getBooleanExpression(
      List<SearchCriteria> criteria, Class<?> classType) {
    BooleanExpression exp = Expressions.asBoolean(true).isTrue();
    for (SearchCriteria cr : criteria) {
      exp =
          exp.and(
              PredicateUtils.getPredicate(cr.getKey(), cr.getOperator(), cr.getValue(), classType));
    }
    return exp;
  }

  public static BooleanExpression getPredicate(
      String key, String operator, String value, Class<?> classType) {
    PathBuilder<?> entityPath =
        new PathBuilder<>(classType, HelperUtils.getEntityVariable(classType.getSimpleName()));
    Class<?> propertyType = HelperUtils.getPropertyType(classType, key);
    if (propertyType.isEnum()) {
      return getStringPredicate(key, operator, value, entityPath);
    }
    return switch (propertyType.getSimpleName().toLowerCase()) {
      case "uuid" -> getUUIDPredicate(key, operator, value, entityPath);
      case "boolean" -> getBooleanPredicate(key, value, entityPath);
      case "string" -> getStringPredicate(key, operator, value, entityPath);
      case "int", "integer" -> getNumberPredicate(key, operator, value, entityPath, Integer.class);
      case "long" -> getNumberPredicate(key, operator, value, entityPath, Long.class);
      case "double" -> getNumberPredicate(key, operator, value, entityPath, Double.class);
      case "localdate" -> getDatePredicate(key, operator, value, entityPath);
      case "localdatetime" -> getDateTimePredicate(key, operator, value, entityPath);
      default -> null;
    };
  }

  public static BooleanExpression getStringPredicate(
      String key, String operator, String value, PathBuilder<?> entityPath) {
    StringPath path = entityPath.getString(key);
    switch (operator) {
      case "=":
        return path.equalsIgnoreCase(value);
      case "-":
        return path.containsIgnoreCase(value);
      case "%":
        return path.startsWithIgnoreCase(value);
      case ":":
        return path.toLowerCase().in(Stream.of(value.split(";")).map(String::toLowerCase).toList());
      default:
        return null;
    }
  }

  public static BooleanExpression getUUIDPredicate(
      String key, String operator, String value, PathBuilder<?> entityPath) {
    ComparablePath<UUID> path = entityPath.getComparable(key, UUID.class);
    if (operator.equals("=")) {
      return path.eq(UUID.fromString(value));
    }
    return null;
  }

  public static BooleanExpression getBooleanPredicate(
      String key, String value, PathBuilder<?> entityPath) {
    BooleanPath path = entityPath.getBoolean(key);
    return path.stringValue().equalsIgnoreCase(value);
  }

  public static <T extends Number & Comparable<?>> BooleanExpression getNumberPredicate(
      String key, String operator, String value, PathBuilder<?> entityPath, Class<T> type) {
    NumberPath<T> path = entityPath.getNumber(key, type);
    return switch (operator) {
      case "=" -> path.eq(parseNumber(value, type));
      case "!=" -> path.ne(parseNumber(value, type));
      case ">" -> path.gt(parseNumber(value, type));
      case "<" -> path.lt(parseNumber(value, type));
      case ">=" -> path.goe(parseNumber(value, type));
      case "<=" -> path.loe(parseNumber(value, type));
      case ":" -> path.in(Stream.of(value.split(";")).map(v -> parseNumber(v, type)).toList());
      case "()" -> {
        String[] valueRange = HelperUtils.getValueRange(value);
        yield path.between(
            valueRange[0].equals(" ") ? null : parseNumber(valueRange[0], type),
            valueRange[1].equals(" ") ? null : parseNumber(valueRange[1], type));
      }
      default -> null;
    };
  }

  private static <T extends Number> T parseNumber(String value, Class<T> type) {
    if (type == Integer.class) {
      return type.cast(Integer.parseInt(value));
    } else if (type == Long.class) {
      return type.cast(Long.parseLong(value));
    } else if (type == Double.class) {
      return type.cast(Double.parseDouble(value));
    }
    throw new IllegalArgumentException("Unsupported type: " + type);
  }

  public static BooleanExpression getDatePredicate(
      String key, String operator, String value, PathBuilder<?> entityPath) {
    DatePath<LocalDate> path = entityPath.getDate(key, LocalDate.class);
    switch (operator) {
      case "=":
        return path.eq(LocalDate.parse(value));
      case ">":
        return path.gt(LocalDate.parse(value));
      case "<":
        return path.lt(LocalDate.parse(value));
      case ">=":
        return path.goe(LocalDate.parse(value));
      case "<=":
        return path.loe(LocalDate.parse(value));
      case "()":
        String[] valueRange = HelperUtils.getValueRange(value);
        return path.between(
            valueRange[0].equals(" ") ? null : LocalDate.parse(valueRange[0]),
            valueRange[1].equals(" ") ? null : LocalDate.parse(valueRange[1]));
      default:
        return null;
    }
  }

  public static BooleanExpression getDateTimePredicate(
      String key, String operator, String value, PathBuilder<?> entityPath) {
    DatePath<LocalDateTime> path = entityPath.getDate(key, LocalDateTime.class);
    switch (operator) {
      case "=":
        return path.eq(LocalDateTime.parse(value));
      case ">":
        return path.gt(LocalDateTime.parse(value));
      case "<":
        return path.lt(LocalDateTime.parse(value));
      case ">=":
        return path.goe(LocalDateTime.parse(value));
      case "<=":
        return path.loe(LocalDateTime.parse(value));
      case "()":
        String[] valueRange = HelperUtils.getValueRange(value);
        return path.between(
            valueRange[0].equals(" ") ? null : LocalDateTime.parse(valueRange[0]),
            valueRange[1].equals(" ") ? null : LocalDateTime.parse(valueRange[1]));
      default:
        return null;
    }
  }
}
