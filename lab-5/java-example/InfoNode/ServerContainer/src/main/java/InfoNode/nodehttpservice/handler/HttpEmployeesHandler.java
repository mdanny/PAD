package InfoNode.nodehttpservice.handler;

import InfoNode.nodeinfoservice.InfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

public class HttpEmployeesHandler implements HttpRequestHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        InfoService infoService = (InfoService) context.getAttribute("infoService");

        if (!method.equals("GET")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(out, infoService.getEmployees());

        final byte[] data = out.toByteArray();
        String e = new String(data);

        response.setStatusCode(HttpStatus.SC_OK);
        StringEntity body = new StringEntity(e, ContentType.create("application/json", Consts.UTF_8));
        response.setEntity(body);
        System.out.println("Sending bytes length is " + body.getContentLength());
    }
}
