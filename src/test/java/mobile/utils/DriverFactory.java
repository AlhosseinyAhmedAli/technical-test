package mobile.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Builds and owns the AndroidDriver instance used by mobile tests.
 *
 * All environment-specific values (Appium server URL, device name, platform
 * version, app path/package) are read from system properties so the same
 * code runs unchanged against a local emulator, a CI device farm, or a
 * cloud provider (BrowserStack/Sauce Labs) -- only the launch command changes.
 *
 * Example (local emulator, app already installed):
 *   mvn test -Dtest=SaveArticleToReadingListTest \
 *       -DdeviceName="Pixel_6_API_34" \
 *       -DplatformVersion="14" \
 *       -DappPackage=org.wikipedia \
 *       -DappActivity=org.wikipedia.main.MainActivity
 *
 * Example (fresh install from a local .apk):
 *   mvn test -Dtest=SaveArticleToReadingListTest -DappPath=/path/to/wikipedia.apk
 */
public final class DriverFactory {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AndroidDriver initDriver() throws MalformedURLException {
        String appiumUrl = System.getProperty("appiumUrl", "http://127.0.0.1:4723");
        String deviceName = System.getProperty("deviceName", "emulator-5554");
        String platformVersion = System.getProperty("platformVersion", "14");
        String appPackage = System.getProperty("appPackage", "org.wikipedia");
        String appActivity = System.getProperty("appActivity", "org.wikipedia.main.MainActivity");
        String appPath = System.getProperty("appPath"); // optional: path to a local .apk

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(deviceName)
                .setPlatformVersion(platformVersion)
                .setAutomationName("UiAutomator2")
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setNoReset(true); // keep the app's storage between runs unless a fresh install is requested

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
