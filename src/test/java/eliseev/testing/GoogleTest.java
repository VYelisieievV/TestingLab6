package eliseev.testing;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.fail;

public class GoogleTest {
    private static String baseUrl;
    private StringBuffer SearchErrors = new StringBuffer();
    public static WebDriver driver;

    public static void TakeScreenShot(String inputWord, int number) {

        try {
            String targetUrl = driver.getCurrentUrl();
            String targetImg="TestGoogleSearch" + inputWord + number + ".png";
            String command = "/Users/User/phantomjs-2.1.1-windows/bin/phantomjs /Users/User/phantomjs-2.1.1-windows/examples/rasterize.js "+targetUrl + " " +targetImg;
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ScreenAllPages (String inputWord, int lastPageNumber)
    {
        int pageNumber = lastPageNumber;
        while (pageNumber>=1)
        {
            try{
                TakeScreenShot(inputWord ,pageNumber);
                Thread.sleep(3000);
                driver.findElement(By.xpath("//*[@id='pnprev']/span[1]")).click();
                driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);

                pageNumber--;

            }
            catch(NoSuchElementException e){
                break;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @BeforeClass
    public static void setProperty() {
        System.setProperty("webdriver.gecko.driver", "C:\\geckodriver-v0.23.0-win64\\geckodriver.exe");
    }

    @Before
    public void setUpTest() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "https://www.google.com.ua";
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
    }

    @Test
    public void testSearch() throws Throwable {
        String inputWord = "Конструктор";
        String [] searchTarget = {"Лего","Java","Yamaha"};
        int pageNumber;

        for (int i=0; i<searchTarget.length; i++){
            pageNumber = 1;
        try {
            driver.get(baseUrl);
            WebElement searchField = driver.findElement(By.xpath("/html/body/div/div[3]/form/div[2]/div/div[1]/div/div[1]/input"));
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);

            searchField.clear();
            if(searchField.isEnabled())
                searchField.sendKeys(inputWord);
            Thread.sleep(1000);
            searchField.submit();
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        }catch (Throwable e)
        {
            SearchErrors.append(e.toString());
            return;
        }
        boolean elementFound = true;

        while (elementFound)
        {
            try{
                driver.findElement(By.partialLinkText(searchTarget[i]));
                TakeScreenShot(searchTarget[i] ,pageNumber);
                break;
            }
            catch(NoSuchElementException e){
                try {
                    driver.findElement(By.xpath("//a[@id='pnnext']")).click();
                    pageNumber++;
                }
                catch (NoSuchElementException e1){
                    System.out.println("The last page, target item wasn't found.");
                    ScreenAllPages(searchTarget[i],pageNumber);
                    elementFound = false;
                }
            }
        }
        if (elementFound) System.out.println("Item "+searchTarget[i]+" was found on page number "+pageNumber);
        else System.out.println("No such item, check screenshots");
        }
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String SearchErrorString = SearchErrors.toString();
        if (!"".equals(SearchErrorString)) {
            fail(SearchErrorString);
        }
    }
}
