package Cliente;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListener{
    private final Socket s;
    private final DataInputStream dis;

    public ClientListener() throws IOException {
        this.s = new Socket("localhost", 12346);
        this.dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
    }


    public void run() {


        try {
            while (true) {
                String line = dis.readUTF();

                switch (line) {
                    case "localizacao":
                        line = dis.readUTF();
                        String[] s = line.split("-");
                        System.out.println("A localização X: " + s[0] + " Y: " + s[1] + " encontra-se desocupada.");
                }
            }


        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        s.shutdownInput();
        s.close();
    }
}
