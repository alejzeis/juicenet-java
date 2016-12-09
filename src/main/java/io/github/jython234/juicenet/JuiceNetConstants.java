package io.github.jython234.juicenet;

/**
 * Contains all JuiceNet constants, includes
 * Network flags, protocol versions and more.
 *
 * @author jython234
 */
public class JuiceNetConstants {
    // Library Constants

    /** The Name constant of the library, which is used in identification. */
    public static final String LIBRARY = "JuiceNet-java";
    /** The Library's version string (full) */
    public static final String LIBRARY_VERSION_STRING = "1.0-SNAPSHOT";

    // Protocol Constants

    /**
     * The Major protocol version this library implements.
     * Major protocols are not compatible with higher or lower
     * major protocol numbers, because they break network
     * compatibility by modifying the JuiceNet protocol,
     * or adding specifically important network handling code.
     */
    public static final int PROTOCOL_VERSION_MAJOR = 2;
    /**
     * The Minor protocol version this library implements.
     * Minor protocols can be compatible with other
     * minor protocol numbers without breaking any
     * network compatibility.
     */
    public static final int PROTOCOL_VERSION_MINOR = 0;

    /**
     * Packet ID of the JuiceNet Peer Search packet.
     */
    public static final short ID_SEARCH_PEERS_PACKET = 0x12AA;

    /**
     * Packet ID of the JuiceNet Peer Search Response packet.
     */
    public static final short ID_PEER_RESPONSE_PACKET = 0x12AB;

    /**
     *  Packet ID of the JuiceNet Client Handshake Packet
     */
    public static final short ID_CLIENT_HANDSHAKE_PACKET = 0x12BA;

    /**
     * Packet ID of the JuiceNet Server Handshake Packet.
     */
    public static final short ID_SERVER_HANDSHAKE_PACKET = 0x12BB;

    /**
     * Packet ID of the JuiceNet Container packet.
     */
    public static final short ID_CONTAINER_PACKET = 0x12CA;
}
