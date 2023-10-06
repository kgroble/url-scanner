import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SecurityDetails;
import io.ipinfo.api.IPinfo;
import io.ipinfo.api.model.IPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class UrlScanner {

    private static final Logger log = LoggerFactory.getLogger("UrlScanner");

    // REVIEW: scoping, threading
    private static Playwright playwright = Playwright.create();
    private static IPinfo ipInfo;
    static {
        try (InputStream input = new FileInputStream("etc/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            if (prop.containsKey("ipInfoToken")) {
               ipInfo = new IPinfo.Builder().setToken(prop.getProperty("ipInfoToken")).build();
            }
        } catch (IOException ex) {
            log.error("exception initializing IPInfo client", ex);
        }
    }

    private final String url;
    private final DbDriver dbDriver;

    public UrlScanner(String url, DbDriver dbDriver) {
        this.url = url;
        this.dbDriver = dbDriver;
    }

    public ScanResult scan() {
        Browser browser = playwright.firefox().launch();
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        Response response = page.navigate(url);

        String ipAddress = response.serverAddr().ipAddress;

        LinkedList<String> redirects = new LinkedList<>();
        Request redirect = response.request();
        while (redirect != null) {
            redirects.addFirst(redirect.url());
            redirect = redirect.redirectedFrom();
        }

        String asn = null;

        if (ipInfo != null) {
            try {
                IPResponse ipResponse = ipInfo.lookupIP(ipAddress);
                if (ipResponse.getAsn() != null) { // paid IPInfo plan only
                    asn = ipResponse.getAsn().getAsn();
                } else if (ipResponse.getOrg() != null) {
                    asn = ipResponse.getOrg();
                }

            } catch (Exception ex) {
                log.warn("error looking up ASN for IP {}", ipAddress, ex);
            }
        }

        byte[] screenshotContent = page.screenshot();
        String screenshotName = String.format("%s-%s.png", Instant.now(), url);
        DbDriver.ScreenshotData screenshotData = new DbDriver.ScreenshotData(screenshotName, screenshotContent);
        UUID screenshotId = dbDriver.storeScreeenshot(screenshotData);

        return new ScanResult(ipAddress, asn, response.securityDetails(), redirects, screenshotId, page.content());
    }

    public record ScanResult(String ipAddress, String asn, SecurityDetails securityDetails,
                             List<String> redirects, UUID screenshotId, String pageSource) {
    }
}
