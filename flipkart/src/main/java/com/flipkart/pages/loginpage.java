package com.flipkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class loginpage {
    private WebDriverWait wait;

    private By closePopup = By.xpath("//button[contains(@class,'_2KpZ6l _2doB4z')]");
    private By loginLink = By.xpath("//a[@class='_1TOQfO' and @title='Login']");
    private By mobileNumberField = By.xpath("//input[contains(@class,'r4vIwl') and contains(@class,'BV+Dqf')]");
    private By requestOtpButton = By.xpath("//button[contains(@class,'QqFHMw') and contains(@class,'twnTnD') and contains(@class,'_7Pd1Fp')]");

    public loginpage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void closeLoginPopup() {
        try {
            WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(closePopup));
            popup.click();
            System.out.println("Login popup closed.");
        } catch (Exception e) {
            System.out.println("No login popup displayed.");
        }
    }

    public void clickLoginLink() {
        WebElement login = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
        login.click();
        System.out.println("Login link clicked.");
    }

    public void enterMobileNumber(String mobileNumber) {
        WebElement mobileInput = wait.until(ExpectedConditions.visibilityOfElementLocated(mobileNumberField));
        mobileInput.clear();
        mobileInput.sendKeys(mobileNumber);
        System.out.println("Entered mobile number: " + mobileNumber);
    }

    public void clickRequestOtp() {
        WebElement otpBtn = wait.until(ExpectedConditions.elementToBeClickable(requestOtpButton));
        otpBtn.click();
        System.out.println("Clicked Request OTP button.");
    }
}
