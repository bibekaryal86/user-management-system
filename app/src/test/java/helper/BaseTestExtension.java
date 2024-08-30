package helper;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class BaseTestExtension implements BeforeAllCallback, AfterAllCallback {

  private static boolean isSetupDone = false;
  private static int flywayCleanCount = 0;
  private static int allTestClassesCount = TestClassesFinder.findTestClasses().size();

  @Override
  public void beforeAll(final ExtensionContext extensionContext) {
    if (!isSetupDone) {
      TestData.setSystemEnvPropertyTestData();
      isSetupDone = true;
    }
  }

  @Override
  public void afterAll(final ExtensionContext extensionContext) {
    allTestClassesCount--;

    if (allTestClassesCount == 0) {
      flywayCleanCount++;
      ApplicationContext applicationContext =
          SpringExtension.getApplicationContext(extensionContext);
      Flyway flyway = applicationContext.getBean(Flyway.class);
      flyway.clean();
    }

    if (flywayCleanCount > 1) {
      throw new IllegalStateException(
          String.format("FlywayCleanCount is more than 1: [%s]", flywayCleanCount));
    }

    if (allTestClassesCount < 0) {
      throw new IllegalStateException(
          String.format("AllTestClassesCount is less than 0: [%s]", allTestClassesCount));
    }
  }
}
