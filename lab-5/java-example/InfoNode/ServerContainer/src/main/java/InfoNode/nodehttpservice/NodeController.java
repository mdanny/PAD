package InfoNode.nodehttpservice;

import InfoNode.nodehttpservice.handler.HttpEmployeeHandler;
import InfoNode.nodehttpservice.handler.HttpEmployeesHandler;
import org.apache.http.protocol.*;

import java.io.IOException;

public class NodeController {

    private int workingPort;

    public NodeController(int workingPort) {
        this.workingPort = workingPort;
    }

    public void start() throws IOException {

        // Set up the HTTP protocol processor
        HttpProcessor httpProcessor = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("DIS - InfoNode/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();

        // Set up request handlers
        UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
        reqistry.register("/employees/", new HttpEmployeesHandler());
        reqistry.register("/employee/*", new HttpEmployeeHandler());

        // Set up the HTTP service
        HttpService httpService = new HttpService(httpProcessor, reqistry);

        // Start HTTP listener
        new RequestListener(workingPort, httpService).start();

    }

}
