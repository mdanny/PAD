package InfoNode.nodehttpservice;

import InfoNode.nodeinfoservice.InfoService;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;

public class Worker extends Thread {

    private final HttpService httpservice;
    private final HttpServerConnection conn;
    private final InfoService infoService;

    public Worker(
            final HttpService httpservice,
            final HttpServerConnection conn,
            final InfoService infoService) {
        super();
        this.httpservice = httpservice;
        this.conn = conn;
        this.infoService = infoService;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("New connection thread");
        HttpContext context = new BasicHttpContext(null);
        context.setAttribute("infoService", infoService);
        try {
            while (!Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
            }
        } catch (ConnectionClosedException ex) {
            System.err.println("Client closed connection");
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
        } catch (HttpException ex) {
            System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
        } finally {
            try {
                this.conn.shutdown();
            } catch (IOException ignore) {
            }
        }
    }
}
