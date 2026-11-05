package architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = {"app", "domain", "application", "infrastructure", "presentation"},
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
  @ArchTest
  static final ArchRule layers =
      layeredArchitecture()
          .consideringOnlyDependenciesInLayers()
          .layer("App")
          .definedBy("app..")
          .layer("Presentation")
          .definedBy("presentation..")
          .layer("Infrastructure")
          .definedBy("infrastructure..")
          .layer("Application")
          .definedBy("application..")
          .layer("Domain")
          .definedBy("domain..")
          .whereLayer("App")
          .mayNotBeAccessedByAnyLayer()
          .whereLayer("Presentation")
          .mayOnlyBeAccessedByLayers("App")
          .whereLayer("Infrastructure")
          .mayOnlyBeAccessedByLayers("App")
          .whereLayer("Application")
          .mayOnlyBeAccessedByLayers("App", "Presentation", "Infrastructure")
          .whereLayer("Domain")
          .mayOnlyBeAccessedByLayers("App", "Presentation", "Infrastructure", "Application");
}
