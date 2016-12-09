package io.github.jython234.juicenet.network;

import io.github.jython234.juicenet.JuiceNetConstants;
import io.github.jython234.juicenet.network.packet.peer.PeerResponsePacket;
import lombok.Getter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Handles raw packets from the UDP socket
 * and handles sessions.
 *
 * @author jython234
 */
public class ServerNetworkManager {
    public final JuiceNetServer server;

    public ServerNetworkManager(JuiceNetServer server) {
        this.server = server;
    }

    public void handleRawPacket(DatagramPacket packet) throws IOException {
        packet.setData(Arrays.copyOf(packet.getData(), packet.getLength()));

        short pid = (short) ((packet.getData()[1] << 8) | packet.getData()[0]); // Read 2 bytes to short

        switch(pid) {
            case JuiceNetConstants.ID_SEARCH_PEERS_PACKET:
                if(this.server.broadcast){
                    PeerResponsePacket prp = new PeerResponsePacket();
                    prp.jwtString = this.server.serverJWT;
                    this.server.sendRawPacket(prp.encode(), packet.getSocketAddress());
                }
                break;
        }
    }
}
