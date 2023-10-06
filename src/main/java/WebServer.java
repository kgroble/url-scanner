import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.options.SecurityDetails;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

public class WebServer implements Runnable {

    private static final ObjectMapper jsonMapper = Main.jsonMapper;

    private final DbDriver dbDriver;

    public WebServer(DbDriver dbDriver)  {
        this.dbDriver = dbDriver;
    }

    @Override
    public void run() {
        // TODO: configurable port
        Spark.port(8080);

        Spark.get("/scan", this::scan);
        Spark.get("/screenshot/:id", this::downloadScreenshot);
    }

    // TODO: headers for options on the headless browser, e.g. firefox vs. chromium vs. ...; pass-through headers; etc.
    private String scan(Request request, Response response) throws JsonProcessingException {
        String targetUrl = request.queryParams("target");
        UrlScanner scanner = new UrlScanner(targetUrl, dbDriver);
        UrlScanner.ScanResult result = scanner.scan();

        String screenshotUrl = String.format("%s/screenshot/%s", request.host(), result.screenshotId());

        response.type("application/json");
        return jsonMapper.writeValueAsString(new ScanResponse(result.ipAddress(), result.asn(), result.securityDetails(),
                result.redirects(), screenshotUrl, result.pageSource()));
    }

    private HttpServletResponse downloadScreenshot(Request request, Response response) {
        UUID screenshotId = UUID.fromString(request.params("id"));
        DbDriver.ScreenshotData screenshotData = dbDriver.fetchScreenshot(screenshotId);

        HttpServletResponse raw = response.raw();

        if (screenshotData == null) {
            response.status(404);
            return raw;
        }

        response.header("Content-Disposition", "attachment; filename=" + screenshotData.filename());
        response.type("application/force-download");
        try {
            raw.getOutputStream().write(screenshotData.content());
            raw.getOutputStream().flush();
            raw.getOutputStream().close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return raw;
    }

    private record ScanResponse(String ipAddress, String asn, SecurityDetails sslInfo, List<String> redirects,
                                String screenshotUrl, String pageSource) {}
}
