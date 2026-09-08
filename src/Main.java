import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      double currentResult;

      while (true) {
        if (!scanner.hasNext()) {
          break;
        }

        currentResult = scanner.nextDouble();

        while (scanner.hasNext()) {
          String command = scanner.next();

          if (command.equalsIgnoreCase("S")) {
            return;
          }

          if (command.equalsIgnoreCase("C")) {
            break;
          }

          if (!scanner.hasNext()) {
            return;
          }

          double secondOperand = scanner.nextDouble();

          switch (command) {
            case "+":
              currentResult += secondOperand;
              break;
            case "-":
              currentResult -= secondOperand;
              break;
            case "*":
              currentResult *= secondOperand;
              break;
            case "/":
              if (secondOperand == 0) {
                System.out.println("Ошибка: деление на ноль невозможно");
                continue;
              }

              currentResult /= secondOperand;
              break;
            default:
              System.out.println("Ошибка: неподдерживаемая операция " + command);
              continue;
          }

          System.out.println(currentResult);
        }
      }
    }
  }
}
