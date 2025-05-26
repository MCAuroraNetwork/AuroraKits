package club.aurorapvp.aurorakits.util;

public class StringUtil {

  public static String convertToNormalTypedLanguage(String camelCaseString) {
    StringBuilder normalTypedLanguage = new StringBuilder();

    for (int i = 0; i < camelCaseString.length(); i++) {
      char currentChar = camelCaseString.charAt(i);

      if (Character.isUpperCase(currentChar) && i != 0) {
        normalTypedLanguage.append(" ");
      }

      if (Character.isDigit(currentChar) && i != 0) {
        normalTypedLanguage.append(" ");
      }

      normalTypedLanguage.append(currentChar);
    }

    return normalTypedLanguage.toString();
  }
}
