package presentation.console;

public enum Color {
  RED("31"),
  GREEN("32"),
  YELLOW("33"),
  CYAN("36"),
  GRAY("90"),
  BOLD("1");

  private final String code;

  Color(String code) {
    this.code = code;
  }

  public String paint(String text) {
    return "\u001B[" + code + "m" + text + "\u001B[0m";
  }
}
