package io.github.jython234.juicenet.network;

import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Base UDP Socket class used by the
 * Server implementation.
 *
 * @author jython234
 */
public class UDPServerSocket implements Closeable {
    public final InetSocketAddress bindAddress;

    private DatagramSocket socket;

    /**
     * Creates a new UDPServerSocket instance and binds to the
     * provided <code>bindAddress</code>.
     * @param bindAddress The address which the socket will bind to.
     * @throws SocketException If there was an error while creating the socket.
     */
    public UDPServerSocket(InetSocketAddress bindAddress) throws SocketException {
        this.bindAddress = bindAddress;

        this.socket = new DatagramSocket(bindAddress.getPort());

        this.socket.setBroadcast(true);
        this.socket.setReceiveBufferSize(2048);
        this.socket.setSendBufferSize(2048);
    }

    public DatagramPacket blockingRecv() throws IOException {
        DatagramPacket pkt = new DatagramPacket(new byte[2048], 2048);
        socket.receive(pkt);

        pkt.setData(Arrays.copyOf(pkt.getData(), pkt.getLength())); // Trim null bytes
        return pkt;
    }

    public void send(DatagramPacket pkt) throws IOException {
        socket.send(pkt);
    }

    @Override
    public void close() {
        this.socket.close();
    }
}
