package io.github.jython234.juicenet.network.packet;

import java.nio.ByteBuffer;

/**
 * Represents any JuiceNet packet sent over
 * the network.
 *
 * @author jython234
 */
public abstract class JuiceNetPacket {
    public final byte[] encode() {
        ByteBuffer bb = ByteBuffer.allocate(getSize());

        return bb.array();
    }

    public final void decode(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
    }

    protected abstract void _encode(ByteBuffer bb);
    protected abstract void _decode(ByteBuffer bb);

    public abstract int getSize();
    public abstract short getID();
}
