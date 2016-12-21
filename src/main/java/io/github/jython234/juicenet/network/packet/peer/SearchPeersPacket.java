package io.github.jython234.juicenet.network.packet.peer;

import io.github.jython234.juicenet.JuiceNetConstants;
import io.github.jython234.juicenet.network.packet.JuiceNetPacket;
import io.jsonwebtoken.Jwts;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * ID_SEARCH_PEERS_PACKET implementation
 *
 * This packet is sent to UDP broadcast by a
 * client or server. If another client or server
 * receives this packet and their settings are set
 * to allow discovery, then they should respond
 * with an ID_PEER_RESPONSE_Packet to the sender with
 * their peer information JWT.
 *
 * @author jython234
 */
// TODO: JWT EC support
public class SearchPeersPacket extends JuiceNetPacket {
    /** This packet's ID */
    public static final short ID = JuiceNetConstants.ID_SEARCH_PEERS_PACKET;

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
        return 4 + jwtString.getBytes(Charset.forName("UTF-8")).length;
    }

    @Override
    public short getID() {
        return JuiceNetConstants.ID_SEARCH_PEERS_PACKET;
    }
}
