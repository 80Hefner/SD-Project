package Cliente;

import java.io.*;
import java.net.Socket;

public class ClientStub {
    private final Socket s;
    private final DataOutputStream dos;
    private final DataInputStream dis;

    public ClientStub () throws IOException {
        this.s = new Socket("localhost", 12345);
        this.dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        this.dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
    }

    public void endSession() throws IOException {
        dos.writeUTF("exit");
        dos.flush();

        s.shutdownOutput();
        s.shutdownInput();
        s.close();
    }

    public boolean login(String user, String password) throws IOException {
        dos.writeUTF("login");
        dos.flush();

        dos.writeUTF(user);
        dos.flush();

        dos.writeUTF(password);
        dos.flush();

        return dis.readBoolean();
    }

    public boolean atualizarLocalizacao(int x, int y) throws IOException {
        dos.writeUTF("atualizarLocalizacao");
        dos.flush();

        dos.writeInt(x);
        dos.flush();

        dos.writeInt(y);
        dos.flush();

        return dis.readBoolean();
    }

    public boolean registar(String user, String password) throws IOException {
        dos.writeUTF("registar");
        dos.flush();

        dos.writeUTF(user);
        dos.flush();

        dos.writeUTF(password);
        dos.flush();

        return dis.readBoolean();
    }

    public boolean logout() throws IOException {
        dos.writeUTF("logout");
        dos.flush();

        return dis.readBoolean();
    }


}
