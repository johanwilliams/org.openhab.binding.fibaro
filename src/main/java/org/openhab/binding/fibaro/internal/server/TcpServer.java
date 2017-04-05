package org.openhab.binding.fibaro.internal.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(TcpServer.class);

    protected TcpClientRequestListener tcpClientRequestListener = null;
    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;

    public TcpServer(TcpClientRequestListener tcpClientRequestListener, int port) {
        this.tcpClientRequestListener = tcpClientRequestListener;
        this.serverPort = port;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();

        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    logger.debug("Tcp server stopped.");
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            try {
                // processClientRequest(clientSocket);
                tcpClientRequestListener.processClientRequest(clientSocket);
            } catch (IOException e) {
                // log exception and go on to next request.
            }
        }

        logger.debug("Tcp server stopped.");
    }

    private void processClientRequest(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream();

        long time = System.currentTimeMillis();

        output.write(
                ("HTTP/1.1 200 OK\n\n<html><body>" + "Singlethreaded Server: " + time + "</body></html>").getBytes());
        output.close();
        input.close();
        logger.debug("Request processed: " + time);
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            logger.debug("Tcp server started on port " + serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }

}
