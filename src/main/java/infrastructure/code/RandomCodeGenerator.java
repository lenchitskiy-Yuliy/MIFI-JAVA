package infrastructure.code;

import application.port.CodeGenerator;
import java.security.SecureRandom;
import java.util.Random;

public final class RandomCodeGenerator implements CodeGenerator {
  private static final String ALPHABET =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  private final Random random = new SecureRandom();
  private final int length;

  public RandomCodeGenerator(int length) {
    this.length = length;
  }

  @Override
  public String next() {
    StringBuilder code = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
    }

    return code.toString();
  }
}
