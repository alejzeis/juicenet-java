package io.github.jython234.juicenet.network.packet;

import io.github.jython234.juicenet.JuiceNetConstants;

import java.nio.ByteBuffer;

/**
 * The base packet in which all other JuiceNet
 * packets are sent in. This is the base packet
 * which implements reliability and fragmentation.
 *
 * @author jython234
 */
public class ContainerPacket extends JuiceNetPacket {
    /** This packet's packet ID */
    public static final short ID = JuiceNetConstants.ID_CONTAINER_PACKET;

    // Fields

    // Reliability Fields

    /**
     * The ReliabilityType that this packet uses.
     *
     * All other reliability fields will only appear
     * depending on the ReliabilityType sent. Refer
     * to each field to find out which types it will
     * be sent under.
     */
    public ReliabilityType reliabilityType;
    /**
     * Packet's container ID. Used in all ReliabilityType's except UNRELIABLE.
     *
     * Each packet is given an ID which is then acknowledged to ensure
     * delivery.
     */
    public int packetId;
    /**
     * Packet's counter. Used in all ReliabilityType's except UNRELIABLE.
     *
     * Each packet is given a unique counter ID which is used to prevent
     * duplicate packets. Once a certain number has been received all other
     * packets with the same counter value are ignored.
     */
    public int packetCounter;
    /**
     * Packet's order ID. Used in RELIABLE_ORDERED.
     *
     * Each packet is given a unique orderId which is used to
     * process packet's in order of sending. Any packet which is sent
     * ordered will be put into a queue that fits into a window. Once a
     * certain time has passed (server and client must agree on this), the
     * queue will be processed. As a result, RELIABLE_ORDERED packets have
     * higher latency because they are processed later than
     * they are received. It is possible to disable the time and wait
     * forever until all packets in the ordered window have been received.
     */
    public int orderId;

    // Channel Fields
    /**
     * Packets can be sent through different Channels,
     * which are simply different handlers. A specific channel
     * can be dedicated to a specific type of packets and/or have
     * specific priority. The default channel is zero (0). JuiceNet
     * supports up to 255 different channels (not including default channel).
     */
    public byte channelId;

    // Fragmentation fields
    /**
     * True if the packet has been fragmented into segments,
     * false if not.
     *
     * The following Fragmentation fields only appear if this value
     * is true.
     */
    public boolean fragmented;

    /**
     * The ID for this master payload.
     *
     * When a packet is fragmented each fragment
     * receives the same fragmentation ID for the
     * master payload. This is used by the implementation
     * to link fragmented packets to specific master
     * payloads. This way, multiple fragmented master payloads
     * can be re-assembled at the same time.
     */
    public int fragmentationId;
    /**
     * The fragmentation index is what
     * tells the implementation where in the master payload
     * this fragment belongs.
     */
    public short fragmentationIndex;

    // Contained Data
    /**
     * Size (in bytes) of this container packet's
     * payload field.
     */
    public short payloadSize;
    /**
     * The container packet's payload.
     *
     * If the packet is fragmented then the payload is placed
     * into a queue to be re-assembled once all other container
     * packets for the master payload have arrived.
     */
    public byte[] payload;

    @Override
    protected void _encode(ByteBuffer bb) {
        bb.put(this.reliabilityType.toByte());

        if(this.reliabilityType != ReliabilityType.UNRELIABLE) {
            bb.putInt(this.packetId);
            bb.putInt(this.packetCounter);
        }
        if(this.reliabilityType == ReliabilityType.RELIABLE_ORDERED) {
            bb.putInt(this.orderId);
        }

        bb.put(this.channelId);

        bb.put(this.fragmented ? (byte) 1 : (byte) 0);
        if(this.fragmented) {
            bb.putInt(this.fragmentationId);
            bb.putShort(this.fragmentationIndex);
        }

        bb.putShort(this.payloadSize);
        bb.put(this.payload);
    }

    @Override
    protected void _decode(ByteBuffer bb) {
        this.reliabilityType = ReliabilityType.parse(bb.get());

        if(this.reliabilityType != ReliabilityType.UNRELIABLE) {
            this.packetId = bb.getInt();
            this.packetCounter = bb.getInt();
        }
        if(this.reliabilityType == ReliabilityType.RELIABLE_ORDERED) {
            this.orderId = bb.getInt();
        }

        this.channelId = bb.get();

        this.fragmented = bb.get() == 1;
        if(this.fragmented) {
            this.fragmentationId = bb.getInt();
            this.fragmentationIndex = bb.getShort();
        }

        this.payloadSize = bb.getShort();
        if(bb.remaining() < this.payloadSize)
            throw new RuntimeException("Invalid payloadSize (" + this.payloadSize + "): not enough bytes left! (" + bb.remaining() + ")");
        this.payload = new byte[this.payloadSize];
        bb.get(this.payload);
    }

    @Override
    public int getSize() {
        int len = 7 + this.payload.length;
        // 2 + 1 + 1 + 1 + 2 + payload Len (Header ID + reliability type + channel ID + fragmented (boolean) + payload len field + payload len)
        if(this.reliabilityType != ReliabilityType.UNRELIABLE) {
            len += 8; // 4 + 4 (packetId + packetCounter)
        }

        if(this.reliabilityType == ReliabilityType.RELIABLE_ORDERED) {
            len += 4; // 4 (orderId)
        }

        if(this.fragmented) {
            len += 6; // 4 + 2 (fragmentation ID + fragmentation index)
        }

        return len;
    }

    @Override
    public short getID() {
        return JuiceNetConstants.ID_CONTAINER_PACKET;
    }

    public enum ReliabilityType {
        UNRELIABLE((byte) 0),
        RELIABLE((byte) 1),
        RELIABLE_ORDERED((byte) 2);

        byte id;

        ReliabilityType(byte id) {
            this.id = id;
        }

        public static ReliabilityType parse(byte id) {
            switch (id) {
                case 0:
                    return UNRELIABLE;
                case 1:
                    return RELIABLE;
                case 2:
                    return RELIABLE_ORDERED;
                default:
                    throw new IllegalArgumentException("Invalid Reliability ID!");
            }
        }

        public byte toByte() {
            return id;
        }
    }
}
