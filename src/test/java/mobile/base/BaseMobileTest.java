package mobile.base;

import io.appium.java_client.android.AndroidDriver;
import mobile.utils.DriverFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Handles driver setup/teardown so individual test classes only contain
 * scenario logic, not session-management boilerplate.
 *
 * A fresh driver session per test method keeps tests independent of one
 * another (no shared app state leaking between scenarios).
 */
public abstract class BaseMobileTest {

    protected AndroidDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        driver = DriverFactory.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
