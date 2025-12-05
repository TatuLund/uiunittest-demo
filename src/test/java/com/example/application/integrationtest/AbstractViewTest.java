package com.example.application.integrationtest;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.flow.component.login.testbench.LoginOverlayElement;
import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.ScreenshotOnFailureExtension;
import com.vaadin.testbench.TestBench;

/**
 * Base class for ITs
 */
public abstract class AbstractViewTest extends BrowserTestBase {
    private static final int SERVER_PORT = 8080;

    private final String route;

    @RegisterExtension
    public ScreenshotOnFailureExtension screenshotOnFailureExtension = new ScreenshotOnFailureExtension(
            this, true);

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    public AbstractViewTest() {
        this("");
    }

    protected AbstractViewTest(String route) {
        this.route = route;
    }

    @BeforeEach
    public void setup() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        setDriver(TestBench.createDriver(new ChromeDriver(options)));
        getDriver().get(getURL(route));

        testBench().resizeViewPortTo(1600, 900);

        // Wait for frontend compilation complete before testing
        waitForDevServer();
    }

    public void blur() {
        executeScript(
                "!!document.activeElement ? document.activeElement.blur() : 0");
    }

    public void login(String user, String pass) {
        var loginForm = $(LoginOverlayElement.class).first();
        loginForm.getUsernameField().setValue(user);
        loginForm.getPasswordField().setValue(pass);
        blur();
        loginForm.getSubmitButton().click();
        waitForElementPresent(By.id("app-layout"));
    }

    public void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Returns deployment host name concatenated with route.
     *
     * @return URL to route
     */
    private static String getURL(String route) {
        return String.format("http://%s:%d/%s", getDeploymentHostname(),
                SERVER_PORT, route);
    }

    /**
     * Property set to true when running on a test hub.
     */
    private static final String USE_HUB_PROPERTY = "test.use.hub";

    /**
     * Returns whether we are using a test hub. This means that the starter is
     * running tests in Vaadin's CI environment, and uses TestBench to connect
     * to the testing hub.
     *
     * @return whether we are using a test hub
     */
    private static boolean isUsingHub() {
        return Boolean.TRUE.toString()
                .equals(System.getProperty(USE_HUB_PROPERTY));
    }

    /**
     * If running on CI, get the host name from environment variable HOSTNAME
     *
     * @return the host name
     */
    private static String getDeploymentHostname() {
        return isUsingHub() ? System.getenv("HOSTNAME") : "localhost";
    }

    protected void waitForElementPresent(final By by) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void scrollToElement(WebElement element) {
        Objects.requireNonNull(element,
                "The element to scroll to should not be null");
        getCommandExecutor().executeScript("arguments[0].scrollIntoView(true);",
                element);
    }
}
