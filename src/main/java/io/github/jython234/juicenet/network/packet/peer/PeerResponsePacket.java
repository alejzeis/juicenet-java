package io.github.jython234.juicenet.network.packet.peer;

import io.github.jython234.juicenet.JuiceNetConstants;
import io.github.jython234.juicenet.network.packet.JuiceNetPacket;

import java.nio.ByteBuffer;

/**
 * ID_PEER_RESPONSE_PACKET implementation
 *
 * This packet is sent in response to an
 * ID_SEARCH_PEERS_PACKET if the peer's settings
 * are set to allow discovery. The JWT should contain information
 * about the peer.
 *
 * @author jython234
 */
// TODO: JWT EC support
public class PeerResponsePacket extends JuiceNetPacket {
    /** This packet's ID */
    public static final short ID = JuiceNetConstants.ID_PEER_RESPONSE_PACKET;

    /** Raw signed JWT */
    public String jwtString;

    @Override
    protected void _encode(ByteBuffer bb) {
        JuiceNetPacket.writeString(bb, jwtString);
    }

    @Override
    protected void _decode(ByteBuffer bb) {
        jwtString = JuiceNetPacket.readString(bb);
    }

    @Override
    public int getSize() {
        return 2 + jwtString.length();
    }

    @Override
    public short getID() {
        return JuiceNetConstants.ID_PEER_RESPONSE_PACKET;
    }
}
