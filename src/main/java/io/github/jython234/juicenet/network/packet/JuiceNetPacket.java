package io.github.jython234.juicenet.network.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * Represents any JuiceNet packet sent over
 * the network.
 *
 * @author jython234
 */
public abstract class JuiceNetPacket {
    public final byte[] encode() {
        ByteBuffer bb = ByteBuffer.allocate(getSize());
        bb.order(ByteOrder.LITTLE_ENDIAN);

        bb.putShort(getID());
        _encode(bb);

        return bb.array();
    }

    public final void decode(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        if(bb.getShort() != getID())
            throw new RuntimeException("Data ID does not match packet ID!");

        _decode(bb);
    }

    protected abstract void _encode(ByteBuffer bb);
    protected abstract void _decode(ByteBuffer bb);

    public abstract int getSize();
    public abstract short getID();

    public static String readString(ByteBuffer bb) {
        int len = bb.getShort();
        byte[] data = new byte[len];
        return new String(data, Charset.forName("UTF-8"));
    }

    public static void writeString(ByteBuffer bb, String str) {
        byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
        bb.putShort((short) bytes.length);
        bb.put(bytes);
    }
}
