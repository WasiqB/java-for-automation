package com.github.wasiqb.generics.without;

import static java.lang.System.getProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.function.Consumer;

import io.appium.java_client.Setting;
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

public class SimpleDriverManager {
    private static final String USER_DIR = getProperty ("user.dir");

    private AndroidDriver            androidDriver;
    private IOSDriver                iosDriver;
    private AppiumDriverLocalService service;
    private WebDriverWait            wait;

    public void createDriver (final Platform platform) throws MalformedURLException {
        if (platform.is (Platform.ANDROID)) {
            createAndroidDriver ();
        } else {
            createIOSDriver ();
        }
    }

    public void performAndroidAction (final By locator, final Consumer<WebElement> action) {
        this.wait.until (ExpectedConditions.visibilityOfElementLocated (locator));
        action.accept (this.androidDriver.findElement (locator));
    }

    public void performIOSAction (final By locator, final Consumer<WebElement> action) {
        this.wait.until (ExpectedConditions.visibilityOfElementLocated (locator));
        action.accept (this.iosDriver.findElement (locator));
    }

    public void startServer () {
        final var logFile = Path.of (USER_DIR, "logs", "appium.log")
            .toFile ();
        this.service = new AppiumServiceBuilder ().withIPAddress ("127.0.0.1")
            .usingPort (4723)
            .withLogFile (logFile)
            .withArgument (GeneralServerFlag.BASEPATH, "/wd/hub")
            .withArgument (GeneralServerFlag.USE_DRIVERS, "uiautomator2")
            .withArgument (GeneralServerFlag.SESSION_OVERRIDE)
            .build ();
        this.service.start ();
    }

    public void stopServer () {
        if (this.service != null) {
            this.service.stop ();
        }
    }

    public void teardown (final Platform platform) {
        if (platform.is (Platform.ANDROID)) {
            this.androidDriver.quit ();
        } else {
            this.iosDriver.quit ();
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

    private void createAndroidDriver () throws MalformedURLException {
        final var capabilities = buildAndroidCapabilities ();
        this.androidDriver = new AndroidDriver (new URL ("http://localhost:4723/wd/hub"), capabilities);
        this.androidDriver.setSetting (Setting.IGNORE_UNIMPORTANT_VIEWS, true);
        this.wait = new WebDriverWait (this.androidDriver, Duration.ofSeconds (10));
    }

    private void createIOSDriver () throws MalformedURLException {
        final var capabilities = buildIOSCapabilities ();
        this.iosDriver = new IOSDriver (new URL ("http://localhost:4723/wd/hub"), capabilities);
        this.wait = new WebDriverWait (this.iosDriver, Duration.ofSeconds (10));
    }
}
