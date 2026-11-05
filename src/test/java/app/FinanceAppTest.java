package app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import presentation.console.Terminal;

class FinanceAppTest {
  @TempDir Path data;

  @Test
  void savesWalletOnExitAndLoadsItOnLogin() throws Exception {
    run(
        """
        register alice secret
        add-category Зарплата
        add-category "Коммунальные услуги"
        income Зарплата 20000
        budget "Коммунальные услуги" 2500
        expense "Коммунальные услуги" 3000
        exit
        """);

    String output =
        run(
            """
            login alice secret
            report
            """);

    assertTrue(output.contains("Баланс: 17 000.00"));
    assertTrue(output.contains("Превышен бюджет Коммунальные услуги: перерасход 500.00"));
    assertTrue(Pattern.compile("Итого +20 000\\.00").matcher(output).find());
  }

  private String run(String script) throws Exception {
    StringWriter output = new StringWriter();
    Terminal terminal = new Terminal(new PrintWriter(output, true), false);

    new FinanceApp(new AppSettings(data), terminal)
        .run(new BufferedReader(new StringReader(script)));
    return output.toString();
  }
}
