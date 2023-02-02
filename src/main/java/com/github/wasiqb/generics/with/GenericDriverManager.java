package com.github.wasiqb.generics.with;

import static java.lang.System.getProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.function.Consumer;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GenericDriverManager {
    private static final String USER_DIR = getProperty ("user.dir");

    private AppiumDriverLocalService service;
    private WebDriverWait            wait;

    public <T extends AppiumDriver> T createDriver (final Platform platform) throws MalformedURLException {
        final T driver;
        if (platform.is (Platform.ANDROID)) {
            driver = createAndroidDriver ();
        } else {
            driver = createIOSDriver ();
        }
        this.wait = new WebDriverWait (driver, Duration.ofSeconds (10));
        return driver;
    }

    public <T extends AppiumDriver> void performAction (final T driver, final By locator,
        final Consumer<WebElement> action) {
        this.wait.until (ExpectedConditions.visibilityOfElementLocated (locator));
        action.accept (driver.findElement (locator));
    }

    public void startServer () {
        final var logFile = Path.of (USER_DIR, "logs", "appium.log")
            .toFile ();
        this.service = new AppiumServiceBuilder ().withIPAddress ("127.0.0.1")
            .usingPort (4723)
            .withLogFile (logFile)
            .withArgument (GeneralServerFlag.BASEPATH, "/wd/hub")
            .withArgument (GeneralServerFlag.USE_DRIVERS, String.join (",", "uiautomator2", "xcuitest"))
            .withArgument (GeneralServerFlag.SESSION_OVERRIDE)
            .build ();
        this.service.start ();
    }

    public void stopServer () {
        if (this.service != null) {
            this.service.stop ();
        }
    }

    private Capabilities buildAndroidCapabilities () {
        final var deviceName = "Pixel_6_Pro";
        final var options = new UiAutomator2Options ();
        options.setPlatformName ("Android")
            .setPlatformVersion ("11")
            .setDeviceName (deviceName)
            .setAvd (deviceName)
            .setAppWaitActivity ("com.wdiodemoapp.MainActivity")
            .setApp (Path.of (USER_DIR, "src/main/resources/apps/wdio-app.apk")
                .toString ())
            .setAutoGrantPermissions (true);
        return options;
    }

    private Capabilities buildIOSCapabilities () {
        final var options = new XCUITestOptions ();
        options.setPlatformName ("iOS")
            .setPlatformVersion ("16.2")
            .setDeviceName ("iPhone 14 Pro Max")
            .setApp (Path.of (USER_DIR, "src/main/resources/apps/wdio-app.app.zip")
                .toString ())
            .setAutoAcceptAlerts (true);
        return options;
    }

    private <T extends AppiumDriver> T createAndroidDriver () throws MalformedURLException {
        final var capabilities = buildAndroidCapabilities ();
        return ((T) new AndroidDriver (new URL ("http://localhost:4723/wd/hub"), capabilities));
    }

    private <T extends AppiumDriver> T createIOSDriver () throws MalformedURLException {
        final var capabilities = buildIOSCapabilities ();
        return (T) new IOSDriver (new URL ("http://localhost:4723/wd/hub"), capabilities);
    }
}
