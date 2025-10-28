package com.flipkart.tests;

import com.flipkart.pages.loginpage;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class loginflipkart {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    String mainWindowHandle;
    loginpage loginPage;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        actions = new Actions(driver);
        driver.get("https://www.flipkart.com/");
        mainWindowHandle = driver.getWindowHandle();
        loginPage = new loginpage(driver);

        // Close initial popup if any
        loginPage.closeLoginPopup();

        // Perform login
        loginPage.clickLoginLink();
        loginPage.enterMobileNumber("8825995074");
        loginPage.clickRequestOtp();

        System.out.println("Please enter OTP manually in the browser...");

        // Wait until login is successful by detecting profile icon or user menu
        try {
            WebElement profileIcon = new WebDriverWait(driver, Duration.ofMinutes(1))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div._1ruvv2")));
            System.out.println("✅ Login successful, proceeding with tests.");
        } catch (TimeoutException e) {
            System.out.println("❌ Login was not completed within 2 minutes. Tests will fail.");
        }
    }

    @Test(priority = 1)
    public void searchSamsungS25EdgeAndAdd() throws InterruptedException {
        searchProduct("Samsung S25 Edge");
        clickFirstProductAndAddToCart();
        goHome();
    }

    @Test(priority = 2, dependsOnMethods = "searchSamsungS25EdgeAndAdd")
    public void searchSamsungTabAndAdd() throws InterruptedException {
        searchProduct("Samsung Tab");
        clickFirstProductAndAddToCart();
        goHome();
    }

    @Test(priority = 3, dependsOnMethods = "searchSamsungTabAndAdd")
    public void searchSamsungAdapterAndAdd() throws InterruptedException {
        searchProduct("Samsung Adapter");
        clickFirstProductAndAddToCart();
        goHome();
    }

    @Test(priority = 4, dependsOnMethods = "searchSamsungAdapterAndAdd")
    public void logout() throws InterruptedException {
        try {
            // Hover over profile icon to show dropdown
            WebElement profileIcon = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div._1ruvv2")));
            actions.moveToElement(profileIcon).perform();

            // Click logout
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Logout']")));
            logoutBtn.click();

            System.out.println("✅ Logged out successfully.");
            Thread.sleep(2000); // short wait to confirm logout
        } catch (Exception e) {
            System.out.println("⚠️ Logout failed or element not found: " + e.getMessage());
        }
    }

    // Utility methods
    void searchProduct(String query) {
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
        searchBox.clear();
        searchBox.sendKeys(query);
        searchBox.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href*='/p/']")));
    }

    void clickFirstProductAndAddToCart() throws InterruptedException {
        List<WebElement> products = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("a[href*='/p/']"), 0));
        WebElement product = products.get(0);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", product);
        actions.moveToElement(product).perform();
        product.click();

        switchToNewTab();

        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Add to cart') or contains(.,'ADD TO CART')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);

        try {
            addToCartBtn.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartBtn);
        }

        Thread.sleep(1000);
        driver.close();
        switchToMainWindow();
    }

    void switchToNewTab() {
        String current = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(current))
                driver.switchTo().window(handle);
        }
    }

    void switchToMainWindow() {
        driver.switchTo().window(mainWindowHandle);
    }

    void goHome() {
        driver.navigate().to("https://www.flipkart.com");
        loginPage.closeLoginPopup(); // just in case
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
