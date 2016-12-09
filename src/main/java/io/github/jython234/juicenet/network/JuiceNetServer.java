package io.github.jython234.juicenet.network;

import io.github.jython234.juicenet.JuiceNetConstants;
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
import java.util.concurrent.SynchronousQueue;

/**
 * A JuiceNet Server implementation
 *
 * @author jython234
 */
public class JuiceNetServer {
    public final String serverJWT;
    public final long serverID;
    public final int bindPort;

    public final boolean broadcast;

    @Getter private boolean running = false;
    @Getter private boolean crashed = false;

    @Getter private Logger logger;
    private DatagramSocket socket;

    @Getter private ServerNetworkManager networkManager;

    public JuiceNetServer(int bindPort, boolean broadcast) {
        this.serverID = new Random().nextLong();
        this.bindPort = bindPort;
        this.logger = LoggerFactory.getLogger("JuiceNetServer");

        this.broadcast = broadcast;

        this.networkManager = new ServerNetworkManager(this);

        // Generate JWT:

        String jwtPayload = generateServerJSON();

        Key key = MacProvider.generateKey();

        this.serverJWT = Jwts.builder()
                .setSubject("server-ident")
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
            this.socket = new DatagramSocket(this.bindPort);
        } catch (SocketException e) {
            this.logger.error("Failed to create Socket!");
            this.logger.error("Perhaps another program is bound on port " + this.bindPort + "?");
            e.printStackTrace();

            this.running = false;
            this.crashed = true;
            return;
        }

        try {
            // this.socket.setSoTimeout(1);
            this.socket.setBroadcast(true);
        } catch (SocketException e) {
            this.logger.error("Failed to set socket options!");
            e.printStackTrace();

            this.socket.close();

            this.running = false;
            this.crashed = true;
            return;
        }

        this.logger.info("Listening for packets on port " + this.bindPort);
        while(this.running) {
            // Main server loop
            DatagramPacket pk = new DatagramPacket(new byte[2048], 2048);
            try {
                this.socket.receive(pk);
                this.networkManager.handleRawPacket(pk);
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

        root.put("library", JuiceNetConstants.LIBRARY);
        root.put("library-version", JuiceNetConstants.LIBRARY_VERSION_STRING);
        root.put("protocol-major", JuiceNetConstants.PROTOCOL_VERSION_MAJOR);
        root.put("protocol-minor", JuiceNetConstants.PROTOCOL_VERSION_MINOR);
        root.put("serverID", this.serverID);
        root.put("serverPort", this.bindPort);

        return root.toJSONString();
    }
}
