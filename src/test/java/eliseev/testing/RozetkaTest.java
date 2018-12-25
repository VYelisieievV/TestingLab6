package eliseev.testing;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.fail;

public class RozetkaTest {
    private static String baseUrl;
    private StringBuffer RozetkaErrors = new StringBuffer();
    public static WebDriver driver;

    public static void TakeScreenShot() {

        try {
            String targetUrl = driver.getCurrentUrl();
            String targetImg = "RozetkaTest.png";
            String command = "/Users/User/phantomjs-2.1.1-windows/bin/phantomjs /Users/User/phantomjs-2.1.1-windows/examples/rasterize.js " + targetUrl + " " + targetImg;
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setProperty() {
        System.setProperty("webdriver.gecko.driver", "C:\\geckodriver-v0.23.0-win64\\geckodriver.exe");
    }

    @Before
    public void setUpTest() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "https://www.rozetka.com.ua";
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
    }

    @Test
    public void testFilter() throws Throwable {
        String minPrice = "10000";
        try {
            driver.get(baseUrl);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            WebElement searchLine = driver.findElement(By.xpath("/html/body/header/div/div/div[2]/div[3]/form/div[1]/div[2]/input"));
            if(searchLine.isEnabled())
                searchLine.sendKeys("Ноутбуки");
            searchLine.submit();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div[2]/div/div[2]/div/div[1]/div[1]/div/div[3]/div/div/div[1]/div[2]/a/span")).click();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            WebElement minField = driver.findElement(By.xpath("//*[@id='price[min]']"));
            WebElement okButton = driver.findElement(By.xpath("//*[@id='submitprice']"));

            minField.clear();
            if(minField.isEnabled())
                minField.sendKeys(minPrice);
            Thread.sleep(1000);
            okButton.click();
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);

            if (driver.findElement(By.xpath("//*[@id='price[min]']")).getAttribute("value").compareTo(minPrice) != 0)
            {
                RozetkaErrors.append("Minimum price field doesn't match the input.");
                throw  new Throwable();
            }

        }catch (Throwable e)
        {
            RozetkaErrors.append(e.toString());
            return;
        }

        List<WebElement> prices = driver.findElements(By.cssSelector(".g-price-uah"));
        int count = 0;
        for(WebElement price:prices){
            if (Integer.valueOf(price.getText().replaceAll("[^0-9]", "")) < Integer.parseInt(minPrice))
            {
                RozetkaErrors.append("\nElement "+(count+1)+" has price lower then filter;");
                count++;
            }
        }
        System.out.println("\n\n"+count+" of "+prices.size()+" elements failed test.");
        Thread.sleep(5000);
        TakeScreenShot();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String RozetkaErrorString = RozetkaErrors.toString();
        if (!"".equals(RozetkaErrors.toString())) {
            fail(RozetkaErrorString);
        }
    }
}
