import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static final ObjectMapper jsonMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);;

    public static void main(String... args) {
        // TODO: dependency injection
        DbDriver dbDriver = new DbDriver();

        new WebServer(dbDriver).run();
    }
}
