package mobile.base;

import io.appium.java_client.android.AndroidDriver;
import mobile.utils.DriverFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;


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
