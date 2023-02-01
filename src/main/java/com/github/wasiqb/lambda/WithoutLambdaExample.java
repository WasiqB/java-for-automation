package com.github.wasiqb.lambda;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WithoutLambdaExample {
    private static final String URL = "https://the-internet.herokuapp.com/login";

    public static void main (final String[] args) {
        final var start = System.currentTimeMillis ();
        final var example = new WithoutLambdaExample ();
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

    private WithoutLambdaExample () {
        this.driver = new ChromeDriver ();
        this.driver.manage ()
            .timeouts ()
            .implicitlyWait (Duration.ofSeconds (10));
        this.driver.manage ()
            .timeouts ()
            .pageLoadTimeout (Duration.ofSeconds (30));
        this.driver.manage ()
            .timeouts ()
            .scriptTimeout (Duration.ofSeconds (30));
        this.driver.get (URL);
    }

    public void login (final String userNameValue, final String passwordValue) {
        this.driver.findElement (this.userName)
            .sendKeys (userNameValue);
        this.driver.findElement (this.password)
            .sendKeys (passwordValue);
        this.driver.findElement (this.loginButton)
            .click ();
        final var message = this.driver.findElement (this.successMessage)
            .getText ();
        System.out.println (message);
    }

    private void logout () {
        this.driver.findElement (this.logout)
            .click ();
        final var message = this.driver.findElement (this.successMessage)
            .getText ();
        System.out.println (message);
    }
}
