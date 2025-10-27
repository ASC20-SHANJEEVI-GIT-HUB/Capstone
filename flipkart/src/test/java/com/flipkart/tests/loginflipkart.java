package com.flipkart.tests;

import com.flipkart.pages.loginpage;
import io.github.bonigarcia.wdm.WebDriverManager;
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
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Headless Chrome options for Jenkins/CI
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");          // run in headless mode
        options.addArguments("--disable-gpu");       // avoid GPU errors
        options.addArguments("--no-sandbox");        // required for some CI environments
        options.addArguments("--remote-allow-origins=*"); // keep your original option
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        actions = new Actions(driver);

        driver.get("https://www.flipkart.com/");
        mainWindowHandle = driver.getWindowHandle();
        loginPage = new loginpage(driver);

        // Login steps
        loginPage.closeLoginPopup();
        loginPage.clickLoginLink();
        loginPage.enterMobileNumber("9786232501");
        loginPage.clickRequestOtp();
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

        Thread.sleep(2000);
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
        loginPage.closeLoginPopup();
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
