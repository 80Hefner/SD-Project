package Cliente;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 12345);
        Thread workerClient = new Thread( new ClientWorker(socket) );
        workerClient.start();
    }

}
