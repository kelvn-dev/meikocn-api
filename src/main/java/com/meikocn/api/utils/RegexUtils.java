package com.meikocn.api.utils;

public class RegexUtils {
  /**
   * @note ^ asserts the start of the string.
   * @note [\\w\\s\\-()]+ matches one or more word characters (alphanumeric and underscore),
   *     whitespace characters, hyphens, parentheses, or underscores.
   * @note \\.matches a literal dot character.
   * @note [a-zA-Z]{2,4} matches two to four alphabetic characters.
   * @note $ asserts the end of the string.
   */
  public static final String SIMPLE_FILENAME_PATTERN = "^[\\w\\s\\-()]+\\.[a-zA-Z]{2,4}$";

  /**
   * @note "([\\w.]+?)": This part captures a group of word characters (alphanumeric and underscore)
   *     and dots. The +? makes the match non-greedy, meaning it will match the smallest possible
   *     sequence of characters. This group is enclosed in parentheses for capturing.
   * @note (:|-|>=|<=|<|>|=|!=|%|\(\)): This part matches one of the specified symbols: -, :, <=,
   *     >=, <, >, =, !=, %, or (). The symbols are enclosed in parentheses for grouping.
   * @note ([\\w\\s\\(\\):@;,._-]+?): This part captures a group of word characters, whitespace
   *     characters, and specific symbols like (), :, @, ,, ;, ., _, and -. The +? matokes the match
   *     non-greedy. This group is enclosed in parentheses for capturing.
   * @note .*: This part matches 0+ chars other than line breaks
   * @note In summary, this regular expression captures patterns with three components. The first
   *     component captures a sequence of word characters and dots, the second component captures
   *     one of the specified symbols, and the third component captures all characters till the end
   */
  public static final String FILTER_REQUEST_PATTERN = "([\\w.]+?)(:|-|>=|<=|<|>|=|!=|%|\\(\\))(.*)";
}
