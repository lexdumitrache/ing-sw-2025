package Networking.TCP;

import Controller.Server.Server;
import Networking.ConnectionHandler;
import Networking.Messages.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnectionHandler extends Thread implements ConnectionHandler {
    private Server server;
    private ServerSocket serverSocket;
    private boolean done;

    public TCPConnectionHandler(Server server, String hostname, String port) throws IOException {
        int PORT;
        try{
            PORT = Integer.parseInt(port);
        } catch(NumberFormatException e){
            PORT = 1234;
        }

        assert(server != null);

        this.server = server;

        this.serverSocket = new ServerSocket();
        this.serverSocket.bind(new InetSocketAddress(hostname, PORT));

        this.start();
        System.out.println("TCP connection handler started at port " + PORT);
    }

    @Override
    public synchronized void setDone() {
        this.done = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            System.err.println("ConnectionHandler.setDone(): Error occurred while closing server socket.");
        }
    }

    public synchronized boolean isDone() {
        return this.done;
    }

    @Override
    public void run() {
        while (!this.isDone()) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                if (this.isDone()) {
                    return;
                }
                System.err.println("ConnectionHandler: Could not accept new connections... Guessing the socket is closed. Killing Thread... ");

                this.setDone();
                return;
            }

            System.out.println("Controller.Server @" + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort() + ": accepted new connection, generating network...");

            TCPNetwork network = null;
            try {
                network = new TCPNetwork(socket);
            } catch (IOException e) {
                System.err.println("ConnectionHandler: Could not start TCPNetwork on newly created connection... weird...");
                continue;
            }

            new Handler<Server>(this.server, network).start();

            this.server.connect(network);
        }
    }
}

