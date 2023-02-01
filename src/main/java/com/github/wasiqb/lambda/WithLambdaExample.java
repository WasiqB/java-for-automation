package com.github.wasiqb.lambda;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class WithLambdaExample {
    private static final String URL = "https://the-internet.herokuapp.com/login";

    public static void main (final String[] args) {
        final var start = System.currentTimeMillis ();
        final var example = new WithLambdaExample ();
        try {
            example.login ("tomsmith", "SuperSecretPassword!");
            example.logout ();
        } finally {
            example.driver.quit ();
            final var end = System.currentTimeMillis ();
            System.out.println ("Total Time in ms: " + (end - start));
        }
    }

    private final WebDriver driver;
    private final By        loginButton    = By.tagName ("button");
    private final By        logout         = By.partialLinkText ("Logout");
    private final By        password       = By.id ("password");
    private final By        successMessage = By.id ("flash");
    private final By        userName       = By.id ("username");

    private WithLambdaExample () {
        this.driver = new ChromeDriver ();
        setTimeouts (t -> t.implicitlyWait (Duration.ofSeconds (10)));
        setTimeouts (t -> t.pageLoadTimeout (Duration.ofSeconds (30)));
        setTimeouts (t -> t.scriptTimeout (Duration.ofSeconds (30)));
        this.driver.get (URL);
    }

    public void login (final String userNameValue, final String passwordValue) {
        performAction (this.userName, e -> e.sendKeys (userNameValue));
        performAction (this.password, e -> e.sendKeys (passwordValue));
        performAction (this.loginButton, WebElement::click);
        final var message = getElementAttribute (this.successMessage, WebElement::getText);
        System.out.println (message);
    }

    private <T> T getElementAttribute (final By locator, final Function<WebElement, T> func) {
        return func.apply (this.driver.findElement (locator));
    }

    private void logout () {
        performAction (this.logout, WebElement::click);
        final var message = getElementAttribute (this.successMessage, WebElement::getText);
        System.out.println (message);
    }

    private void performAction (final By locator, final Consumer<WebElement> action) {
        action.accept (this.driver.findElement (locator));
    }

    private void setTimeouts (final Consumer<WebDriver.Timeouts> timeouts) {
        timeouts.accept (this.driver.manage ()
            .timeouts ());
    }
}
