package Cliente;

import java.io.*;
import java.net.Socket;

public class ClientStub {
    private final Socket s;
    private final DataOutputStream dos;
    private final DataInputStream dis;

    public ClientStub() throws IOException {
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

    public int login(String user, String password) throws IOException {
        dos.writeUTF("login");
        dos.flush();

        dos.writeUTF(user);
        dos.flush();

        dos.writeUTF(password);
        dos.flush();

        return dis.readInt();
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

    public boolean registar(String user, String password, int locX, int locY) throws IOException {
        dos.writeUTF("registar");
        dos.flush();

        dos.writeUTF(user);
        dos.flush();

        dos.writeUTF(password);
        dos.flush();

        dos.writeInt(locX);
        dos.flush();

        dos.writeInt(locY);
        dos.flush();

        return dis.readBoolean();
    }

    public boolean logout() throws IOException {
        dos.writeUTF("logout");
        dos.flush();

        return dis.readBoolean();
    }

    public int consultaNumeroPessoasLocalizacao(int x, int y) throws IOException {
        dos.writeUTF("consultaNumeroPessoasLocalizacao");
        dos.flush();

        dos.writeInt(x);
        dos.flush();

        dos.writeInt(y);
        dos.flush();

        return dis.readInt();
    }

    public boolean consultaLocalizacaoLivre(int x, int y) throws IOException {
        dos.writeUTF("consultaLocalizacaoLivre");
        dos.flush();

        dos.writeInt(x);
        dos.flush();

        dos.writeInt(y);
        dos.flush();

        return dis.readBoolean();
    }

    public boolean notificarInfecao() throws IOException {
        dos.writeUTF("notificarInfecao");
        dos.flush();

        return dis.readBoolean();
    }

    public String consultarMapaLocalizacoes() throws IOException {
        dos.writeUTF("consultarMapaLocalizacoes");
        dos.flush();

        return dis.readUTF();
    }
}
