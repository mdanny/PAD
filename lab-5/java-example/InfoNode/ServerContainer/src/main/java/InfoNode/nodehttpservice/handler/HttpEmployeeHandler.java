package InfoNode.nodehttpservice.handler;

import InfoNode.nodeinfoservice.InfoService;
import InfoNode.nodeinfoservice.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

public class HttpEmployeeHandler implements HttpRequestHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        InfoService infoService = (InfoService) context.getAttribute("infoService");

        if (method.equals("PUT")) {
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

                if (entity != null) {
                    entity = new BufferedHttpEntity(entity);
                }

                byte[] entityContent = EntityUtils.toByteArray(entity);
                System.out.println("Incoming entity content (bytes): " + entityContent.length);

                ObjectMapper mapper = new ObjectMapper();
                Employee e = mapper.readValue(EntityUtils.toString(entity), Employee.class);

                infoService.add(e);
                System.out.println("Incoming entity content: " + e.toString());

                response.setStatusCode(HttpStatus.SC_CREATED);

            }
            return;
        }

        if (method.equals("GET")) {
            StringEntity body = null;
            try {
                System.out.println("Incoming entity content: " + infoService.getEmployees().size());
                int id = Integer.parseInt(
                        new URIBuilder(request.getRequestLine().getUri())
                                .getQueryParams().stream().filter(v -> v.getName().equalsIgnoreCase("id")).findFirst()
                                .get().getValue());
                if (infoService.getEmployees().size() > 0 && infoService.getEmployees().size() >= id) {

                    ObjectMapper mapper = new ObjectMapper();
                    String e = mapper.writeValueAsString(infoService.getEmployees().toArray()[id - 1]);

                    body = new StringEntity(e, ContentType.create("application/json", Consts.UTF_8));

                    response.setEntity(body);
                    response.setStatusCode(HttpStatus.SC_OK);

                    System.out.println("Sending bytes length is " + body.getContentLength());
                } else {
                    response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                }

            } catch (URISyntaxException e) {

                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            }
            return;
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
}
