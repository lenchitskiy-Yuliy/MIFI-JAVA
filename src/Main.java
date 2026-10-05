
public class Main {

  public static void main(String[] args) {
    WeatherParser parser = new WeatherParser();

    try {
      AppSettings settings = AppSettings.from(args);
      WeatherApi apiWeather = new WeatherApi(settings, "2540aacd-887e-4b3c-af8e-7087ec5a9954");

      // Выполнен GET к https://api.weather.yandex.ru/v2/forecast?lat=…&lon=… и в
      // запрос передан заголовок X-Yandex-Weather-Key
      String weatherJson = apiWeather.fetch();

      int factTemp = parser.getFactTemp(weatherJson);
      double mediumTempAvg = parser.getMediumTempAvg(weatherJson, settings.limit());

      // Весь ответ сервиса выведен в консоль «как есть»
      System.out.println(weatherJson);
      // Значение fact.temp корректно извлечено и выведено
      System.out.println("Текущая температура: " + factTemp + " °C");
      // По первым limit-элементам forecasts взяты parts.day.temp_avg и посчитано
      // среднее арифметическое; результат выведен и в запросе используется параметр
      // limit как количество суток
      System.out.printf("Средняя температура за %d дн.: %.1f °C%n", settings.limit(), mediumTempAvg);
    } catch (Exception exception) {
      System.err.println("Ошибка: " + exception.getMessage());
    }
  }
}
