import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.pincode.utils.FWorker;
import com.pincode.utils.SeleniumUtils;

public class Runner1 {

	// File input and file output
	private static final String FILEFORREAD = "reader.txt";
	private static final String FILE = "rajnish.txt";
	
	// separator for output file
	private static final String SEPARATOR = "|";

	// Timeout for searching element on a page
	private static final int timeout = 10;
	
	/*
	 * Main method
	 */
	private static final Logger LOG = Logger.getLogger(SeleniumUtils.class);

	public static void main(String[] args) throws InterruptedException, IOException {
		SeleniumUtils seleniumUtils = new SeleniumUtils();
		FWorker fw = new FWorker(FILE);

		List<String> dataFormFile = FWorker.readline(FILEFORREAD);

		List<String> stateLabels = null;
		// List<String> distLabels = null;
		String strToShow = null;

		DesiredCapabilities dc = new DesiredCapabilities();
		dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		WebDriver driver = new FirefoxDriver(dc);
		driver.get("http://cept.gov.in/lbpsd/placesearch.aspx");
		// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// Include All
		driver.findElement(By.id("rbnIncludeAll")).click();
		Thread.sleep(1000);

		Iterator<String> iter = dataFormFile.iterator();
		stateLabels = new ArrayList<String>();
		List<List<String>> distLabels = new ArrayList<List<String>>();
		List<List<String>> vctLabels = new ArrayList<List<String>>();
		List<List<String>> ddlLocalityLabels = new ArrayList<List<String>>();

		String[] s;
		while (iter.hasNext()) {
			try {
				s = iter.next().split("\\|", -1);
				stateLabels.add(s[0]);
				
				List<String> tmpDist = new ArrayList<String>();
				tmpDist.add(s[1]);
				distLabels.add(tmpDist);
				
				List<String> tmpVct = new ArrayList<String>();
				tmpVct.add(s[2]);
				vctLabels.add(tmpVct);
				
				List<String> tmpDdl = new ArrayList<String>();
				tmpDdl.add(s[3]);
				ddlLocalityLabels.add(tmpDdl);
			} catch (IndexOutOfBoundsException e) {
			}
		}

		
		for (int i = 0; i<stateLabels.size(); i++) {
			new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='ddlState']/option[normalize-space(text())='" + stateLabels.get(i) + "']")));
			driver.findElement(By.xpath(".//*[@id='ddlState']/option[normalize-space(text())='" + stateLabels.get(i) + "']")).click();

			for (String dist : distLabels.get(i)) {
				new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='ddlDist']/option[normalize-space(text())='" + dist + "']")));
				driver.findElement(By.xpath(".//*[@id='ddlDist']/option[normalize-space(text())='" + dist + "']")).click();

				for (String vct : vctLabels.get(i)) {
					if (vct.contains("'")) {
						new WebDriverWait(driver, timeout)
								.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='ddlVCT']/option[normalize-space(text())=\"" + vct + "\"]")));
						driver.findElement(By.xpath(".//*[@id='ddlVCT']/option[normalize-space(text())=\"" + vct + "\"]")).click();
					} else {
						new WebDriverWait(driver, timeout)
								.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='ddlVCT']/option[normalize-space(text())='" + vct + "']")));
						driver.findElement(By.xpath(".//*[@id='ddlVCT']/option[normalize-space(text())='" + vct + "']")).click();
					}

					for (String ddlLocality : ddlLocalityLabels.get(i)) {
						if (ddlLocality.contains("'")) {
							LOG.info(1);
							new WebDriverWait(driver, timeout).until(
									ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='ddlLocality']/option[normalize-space(text())=\"" + ddlLocality + "\"]")));
							driver.findElement(By.xpath(".//*[@id='ddlLocality']/option[normalize-space(text())=\"" + ddlLocality + "\"]")).click();
						} else {
							new WebDriverWait(driver, timeout)
									.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='ddlLocality']/option[normalize-space(text())='" + ddlLocality + "']")));
							driver.findElement(By.xpath(".//*[@id='ddlLocality']/option[normalize-space(text())='" + ddlLocality + "']")).click();
						}

						// getting pin code
						try {
							new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@id='txtPincode']")));
							String pinCode = driver.findElement(By.xpath(".//*[@id='txtPincode']")).getAttribute("value");

							if (!pinCode.equals("")) {
								strToShow = stateLabels.get(i) + SEPARATOR + dist + SEPARATOR + vct + SEPARATOR + ddlLocality + SEPARATOR + pinCode;

								LOG.info(strToShow);
								fw.write(strToShow);
							}
//							driver.navigate().refresh();
							
						} catch (TimeoutException | NoSuchElementException e) {
							// "Server Error in '/lbpsd' Application
							LOG.warn("Server error appears -> " + strToShow);
							driver.navigate().back();
						}
						Thread.sleep(1000);
					}
				}
			}
		}

	}
}
