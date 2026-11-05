package infrastructure.security;

import application.port.PasswordHasher;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Pbkdf2PasswordHasher implements PasswordHasher {
  private static final int ITERATIONS = 65536;

  @Override
  public String hash(String password) {
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    return encode(salt) + ":" + encode(hash(password, salt));
  }

  @Override
  public boolean matches(String password, String hash) {
    String[] parts = hash.split(":");
    byte[] salt = Base64.getDecoder().decode(parts[0]);
    return MessageDigest.isEqual(Base64.getDecoder().decode(parts[1]), hash(password, salt));
  }

  private static byte[] hash(String password, byte[] salt) {
    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256);

    try {
      return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    } catch (GeneralSecurityException exception) {
      throw new IllegalStateException("Не удалось захешировать пароль", exception);
    }
  }

  private static String encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }
}
