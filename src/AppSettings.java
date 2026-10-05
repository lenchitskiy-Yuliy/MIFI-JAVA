public record AppSettings(double latitude, double longitude, int limit) {
  private static final int DEFAULT_LIMIT = 1;

  public static AppSettings from(String[] args) {
    checkArguments(args);

    return new AppSettings(
        Double.parseDouble(args[0]),
        Double.parseDouble(args[1]),
        args.length == 3 ? Integer.parseInt(args[2]) : DEFAULT_LIMIT);
  }

  private static void checkArguments(String[] args) {
    if (args.length < 2 || args.length > 3) {
      throw new IllegalArgumentException("Использование: <широта> <долгота> [количество дней]");
    }
  }

  public AppSettings {
    if (limit <= 0) {
      throw new IllegalArgumentException("Количество дней должно быть положительным");
    }

    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Широта должна быть в диапазоне [-90, 90]");
    }

    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Долгота должна быть в диапазоне [-180, 180]");
    }
  }
}
