package Cliente;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class responsável pelo ClientListener
 */
public class ClientListener implements Runnable{
    private Thread thread;
    private boolean running;
    private final Socket s;
    private final DataInputStream dis;

    /**
     * Construtor do ClientListener
     * @throws IOException      Exception IO
     */
    public ClientListener() throws IOException {
        this.running = true;
        this.s = new Socket("localhost", 54321);
        this.s.shutdownOutput();
        this.dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
    }

    /**
     * Método que acaba sessão e fecha sockets e input
     * @throws IOException      Exception IO
     */
    public void endSession() throws IOException {
        s.shutdownInput();
        s.close();
    }

    @Override
    /**
     * Método Run
     */
    public void run() {

        thread = new Thread(() -> {
            try {
                while (running) {
                    String line = dis.readUTF();

                    switch (line) {
                        case "localizacao":
                            line = dis.readUTF();
                            String[] s = line.split("-");
                            System.out.println(
                                    "\n╔═════════════════════════════════════════════════════╗" +
                                    "\n║  Warning: A localização X:" + s[0] + " Y:" + s[1] + " encontra-se livre.  ║" +
                                    "\n╚═════════════════════════════════════════════════════╝");
                            break;

                        case "avisaContacto":
                            System.out.println(
                                    "\n╔══════════════════════════════════════════════════════════╗" +
                                    "\n║      WARNING: ESTEVE EM CONTACTO COM UM INFETADO!!!      ║" +
                                    "\n╚══════════════════════════════════════════════════════════╝");
                            break;

                        case "exit":
                            running = false;
                            break;
                    }
                }

            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

}
