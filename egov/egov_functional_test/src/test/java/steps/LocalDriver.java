package steps;

import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

public class LocalDriver {

    public WebDriver getApplicationDriver() {
        String browser = System.getProperty("browser");
        System.out.println("Browser to be tested on --" + browser);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        if (browser.equals("firefox")) {
            System.setProperty("webdriver.gecko.driver", "src/test/java/geckodriver");
            return new FirefoxDriver();
        }


        if (browser.equals("chrome")) {
            setChromeDriverBasedOnOperatingSystem();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("start-maximized");
            options.addArguments("allow-running-insecure-content");
            options.addArguments("--disable-extensions");
//            options.addArguments("disable-web-security");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            return new ChromeDriver(capabilities);


        }

        if (browser.equals("safari")) {

            DesiredCapabilities safariCapabilities = new DesiredCapabilities();
            safariCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//            SafariOptions safariOptions = new SafariOptions();
//            safariOptions.setUseCleanSession(true);
            return new SafariDriver(safariCapabilities);
        }


        throw new RuntimeException("Not a supported driver");
    }

    private void setChromeDriverBasedOnOperatingSystem() {
        if (isWindows()) {
            System.setProperty("webdriver.chrome.driver", "src/test/java/chromedriver.exe");
            return;
        }
        if (isMac()) {
            System.setProperty("webdriver.chrome.driver", "src/test/java/chromedriver");
            return;
        }

        if (isLinux()) {
            System.setProperty("webdriver.chrome.driver", "src/test/java/chromedriver_linux");
            return;

        }
    }

    private boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    private boolean isLinux() {
        return SystemUtils.IS_OS_LINUX;
    }

    private boolean isMac() {
        return SystemUtils.IS_OS_MAC;
    }


}