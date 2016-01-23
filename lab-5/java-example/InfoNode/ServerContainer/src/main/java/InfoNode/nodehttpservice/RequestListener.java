package InfoNode.nodehttpservice;

import InfoNode.nodeinfoservice.InfoService;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.protocol.HttpService;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestListener extends Thread {

    private final HttpConnectionFactory<DefaultBHttpServerConnection> connFactory;
    private final ServerSocket serversocket;
    private final HttpService httpService;
    private final InfoService infoService;

    public RequestListener(
            final int port,
            final HttpService httpService) throws IOException {

        this.connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
        this.serversocket = new ServerSocket(port);
        this.httpService = httpService;
        infoService = new InfoService();
        this.setDaemon(false);
    }

    @Override
    public void run() {
        System.out.println("Listening on port " + this.serversocket.getLocalPort());
        while (!Thread.interrupted()) {
            try {
                // Set up HTTP connection
                Socket socket = this.serversocket.accept();
                System.out.println("Incoming connection from " + socket.getInetAddress());
                HttpServerConnection conn = this.connFactory.createConnection(socket);

                // Start worker thread
                new Worker(this.httpService, conn, infoService).start();

            } catch (InterruptedIOException ex) {
                break;
            } catch (IOException e) {
                System.err.println("I/O error initialising connection thread: "
                        + e.getMessage());
                break;
            }
        }
    }
}