package com.github.wasiqb.generics.with;

import java.net.MalformedURLException;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;

public class WithGenerics {

    public static void main (final String[] args) throws MalformedURLException {
        final var example = new WithGenerics ();
        final AndroidDriver androidDriver;
        final IOSDriver iosDriver;
        try {
            example.driverManager.startServer ();

            androidDriver = example.driverManager.createDriver (Platform.ANDROID);
            example.login ("wasiq@gmail.com", "admin@12345", androidDriver);
            androidDriver.quit ();

            iosDriver = example.driverManager.createDriver (Platform.IOS);
            example.login ("faisal@gmail.com", "Admin@12345", iosDriver);
            iosDriver.quit ();
        } finally {
            example.driverManager.stopServer ();
        }
    }

    private final GenericDriverManager driverManager;

    private final By email       = AppiumBy.accessibilityId ("input-email");
    private final By loginButton = AppiumBy.accessibilityId ("button-LOGIN");
    private final By loginTab    = AppiumBy.accessibilityId ("Login");
    private final By passcode    = AppiumBy.accessibilityId ("input-password");

    private WithGenerics () {
        this.driverManager = new GenericDriverManager ();
    }

    private <D extends AppiumDriver> void login (final String userName, final String password, final D driver) {
        this.driverManager.performAction (driver, this.loginTab, WebElement::click);
        this.driverManager.performAction (driver, this.email, e -> e.sendKeys (userName));
        this.driverManager.performAction (driver, this.passcode, e -> e.sendKeys (password));
        this.driverManager.performAction (driver, this.loginButton, WebElement::click);
    }
}
