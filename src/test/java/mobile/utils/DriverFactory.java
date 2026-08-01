package mobile.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public final class DriverFactory {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AndroidDriver initDriver() throws MalformedURLException {
        String appiumUrl = System.getProperty("appiumUrl", "http://127.0.0.1:4723");
        String deviceName = System.getProperty("deviceName", "emulator-5554");
        String platformVersion = System.getProperty("platformVersion", "17");
        String appPackage = System.getProperty("appPackage", "org.wikipedia");
        String appActivity = System.getProperty("appActivity", "org.wikipedia.main.MainActivity");
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(deviceName)
                .setPlatformVersion(platformVersion)
                .setAutomationName("UiAutomator2")
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setNoReset(true);

        if (appPath != null && !appPath.isBlank()) {
            options.setApp(appPath);
            options.setNoReset(false);
        } else {
            // App is assumed to be already installed on the device/emulator.
            options.setAppPackage(appPackage);
            options.setAppActivity(appActivity);
        }

        AndroidDriver driver = new AndroidDriver(new URL(appiumUrl), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        DRIVER.set(driver);
        return driver;
    }

    public static AndroidDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
