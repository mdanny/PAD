package InfoNode.nodecontainer;

import InfoNode.nodehttpservice.NodeController;

import java.io.IOException;

/**
 * if exec-maven-plugin installed
 * the app can be started in terminal with:
 * mvn exec:java -Dexec.args="5555"
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );

        new NodeController(8080).start();
    }
}
