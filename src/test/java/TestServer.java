import io.github.jython234.juicenet.network.server.JuiceNetServer;

import java.net.InetSocketAddress;

/**
 * Created by jython234 on 12/11/16.
 */
public class TestServer {
    public static void main(String[] args) {
        JuiceNetServer server = new JuiceNetServer(new InetSocketAddress(4205), true);
        server.start();
    }
}
