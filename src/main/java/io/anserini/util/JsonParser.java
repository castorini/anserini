package io.anserini.util;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class JsonParser {
  public static boolean isFieldAvailable(Object field) {
    if (field == null) {
      return false;
    }
    boolean isPresent;
    if (field.getClass() == OptionalLong.class) {
      isPresent = ((OptionalLong)field).isPresent();
    } else if (field.getClass() == OptionalDouble.class) {
      isPresent = ((OptionalDouble)field).isPresent();
    } else if (field.getClass() == OptionalInt.class) {
      isPresent = ((OptionalInt)field).isPresent();
    } else {
      isPresent = ((Optional)field).isPresent();
    }
    return isPresent;
  }
}
