package com.github.wasiqb.generics.without;

import java.net.MalformedURLException;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;

public class WithoutGenerics {
    private static SimpleDriverManager simpleDriver;

    public static void main (final String[] args) throws MalformedURLException {
        final var example = new WithoutGenerics ();
        simpleDriver = new SimpleDriverManager ();
        try {
            simpleDriver.startServer ();

            simpleDriver.createDriver (Platform.ANDROID);
            example.loginAndroid ("wasiq@gmail.com", "admin@12345");
            simpleDriver.teardown (Platform.ANDROID);

            simpleDriver.createDriver (Platform.IOS);
            example.loginIOS ("faisal@gmail.com", "Admin@12345");
            simpleDriver.teardown (Platform.IOS);
        } finally {
            simpleDriver.stopServer ();
        }
    }

    private final By email       = AppiumBy.accessibilityId ("input-email");
    private final By loginButton = AppiumBy.accessibilityId ("button-LOGIN");
    private final By loginTab    = AppiumBy.accessibilityId ("Login");
    private final By passcode    = AppiumBy.accessibilityId ("input-password");

    private void loginAndroid (final String userName, final String password) {
        simpleDriver.performAndroidAction (this.loginTab, WebElement::click);
        simpleDriver.performAndroidAction (this.email, e -> e.sendKeys (userName));
        simpleDriver.performAndroidAction (this.passcode, e -> e.sendKeys (password));
        simpleDriver.performAndroidAction (this.loginButton, WebElement::click);
    }

    private void loginIOS (final String userName, final String password) {
        simpleDriver.performIOSAction (this.loginTab, WebElement::click);
        simpleDriver.performIOSAction (this.email, e -> e.sendKeys (userName));
        simpleDriver.performIOSAction (this.passcode, e -> e.sendKeys (password));
        simpleDriver.performIOSAction (this.loginButton, WebElement::click);
    }
}
