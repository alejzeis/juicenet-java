package io.github.jython234.juicenet.network.server;

import io.github.jython234.juicenet.JuiceNetConstants;
import io.github.jython234.juicenet.network.UDPServerSocket;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.security.Key;
import java.util.Random;

/**
 * A JuiceNet Server implementation
 *
 * @author jython234
 */
public class JuiceNetServer {
    public final String serverJWT;
    public final long serverID;
    public final InetSocketAddress bindAddress;

    public final boolean broadcast;

    @Getter private boolean running = false;
    @Getter private boolean crashed = false;

    @Getter private Logger logger;
    private UDPServerSocket socket;

    @Getter private ServerNetworkManager networkManager;

    public JuiceNetServer(InetSocketAddress bindAddress, boolean broadcast) {
        this.serverID = new Random().nextLong();
        this.bindAddress = bindAddress;
        this.logger = LoggerFactory.getLogger("JuiceNetServer");

        this.broadcast = broadcast;

        this.networkManager = new ServerNetworkManager(this);

        // Generate JWT:

        String jwtPayload = generateServerJSON();

        Key key = MacProvider.generateKey();

        this.serverJWT = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, key)
                .setPayload(jwtPayload)
                .compact();
    }

    /**
     * Starts this JuiceNetServer instance.
     *
     * This method blocks as the server runs in
     * the current thread.
     */
    public void start() {
        if(isRunning())
            throw new UnsupportedOperationException("This server instance is already running!");

        this.running = true;
        run();
    }

    /**
     * Set the internal running boolean to false,
     * which then allows the server thread to cleanup
     * and exit.
     */
    public void stop() {
        if(!isRunning())
            throw new UnsupportedOperationException("This server instance is not running!");

        this.running = false;
    }

    private void run() {
        this.logger.info(JuiceNetConstants.LIBRARY + " version " + JuiceNetConstants.LIBRARY_VERSION_STRING + " on " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        try {
            this.socket = new UDPServerSocket(this.bindAddress);
        } catch (SocketException e) {
            this.logger.error("Failed to create Socket!");
            this.logger.error("Perhaps another program is bound on port " + this.bindAddress.getPort() + "?");
            e.printStackTrace();

            this.running = false;
            this.crashed = true;
            return;
        }

        this.logger.info("Listening for packets on " + this.bindAddress.toString());
        while(this.running) {
            // Main server loop
            try {
                this.networkManager.handleRawPacket(this.socket.blockingRecv());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void sendRawPacket(byte[] payload, SocketAddress address) throws IOException {
        DatagramPacket dp = new DatagramPacket(payload, payload.length, address);
        this.socket.send(dp);
    }

    @SuppressWarnings("unchecked")
    private String generateServerJSON() {
        JSONObject root = new JSONObject();
        JSONObject library = new JSONObject();
        JSONObject protocol = new JSONObject();
        JSONObject server = new JSONObject();
        JSONObject system = new JSONObject();

        system.put("os", System.getProperty("os.name"));
        system.put("arch", System.getProperty("os.arch"));
        system.put("version", System.getProperty("os.version"));

        library.put("name", JuiceNetConstants.LIBRARY);
        library.put("version", JuiceNetConstants.LIBRARY_VERSION_STRING);

        protocol.put("major", JuiceNetConstants.PROTOCOL_VERSION_MAJOR);
        protocol.put("minor", JuiceNetConstants.PROTOCOL_VERSION_MINOR);

        server.put("server-id", this.serverID);
        server.put("port", this.bindAddress.getPort());
        server.put("system", system);

        root.put("library", library);
        root.put("protocol", protocol);
        root.put("server", server);

        return root.toJSONString();
    }
}
