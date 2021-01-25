package Cliente;

import java.io.*;
import java.net.Socket;

/**
 * Classe com o Cliente Stub
 */
public class ClientStub {
    private final Socket s;
    private final DataOutputStream dos;
    private final DataInputStream dis;

    /**
     * Construtor do ClientStub
     * @throws IOException      Exception IO
     */
    public ClientStub() throws IOException {
        this.s = new Socket("localhost", 12345);
        this.dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        this.dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
    }

    /**
     * Método responsável por encerrar uma
     * @throws IOException      Exception IO
     */
    public void endSession() throws IOException {
        dos.writeUTF("exit");
        dos.flush();

        s.shutdownOutput();
        s.shutdownInput();
        s.close();
    }

    /**
     * Efutua login num Utilizaor e recebe tipo de Utilizador que é
     * @param user              Username do Utilizador
     * @param password          Password do Utilizador
     * @return                  Código que identifica tipo de Utilizador em que se está a loggar (ou se não logou)
     * @throws IOException      Exception IO
     */
    public int login(String user, String password) throws IOException {
        dos.writeUTF("login");
        dos.flush();

        dos.writeUTF(user);
        dos.flush();

        dos.writeUTF(password);
        dos.flush();

        return dis.readInt();
    }

    /**
     * Atualiza localização de um Servidor
     * @param x                 Localização x nova do Utilizador
     * @param y                 Localização x nova do Utilizador
     * @return                  Booleano se mostra que conseguiu alterar Localização
     * @throws IOException      Exception IO
     */
    public boolean atualizarLocalizacao(int x, int y) throws IOException {
        dos.writeUTF("atualizarLocalizacao");
        dos.flush();

        dos.writeInt(x);
        dos.flush();

        dos.writeInt(y);
        dos.flush();

        return dis.readBoolean();
    }

    /**
     * Regista um novo Utilizador
     * @param user          Username do Utilizador a introduzir
     * @param password      Password do Utilizador a introduzir
     * @param locX          Localização x do Utilizador
     * @param locY          Localização y do Utilizador
     * @return              Booleano se mostra que conseguiu criar Utilizador
     * @throws IOException  Exception IO
     */
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

    /**
     * Método que efetua Logout
     * @return                  Booleano se mostra que conseguiu dar logout
     * @throws IOException      Exception IO
     */
    public boolean logout() throws IOException {
        dos.writeUTF("logout");
        dos.flush();

        return dis.readBoolean();
    }

    /**
     * Consulta numero de pessoas numa localização
     * @param x                 Localização x a verificar
     * @param y                 Localização y a verificar
     * @return                  Numero de Utilizadores nessa localização
     * @throws IOException      Exception IO
     */
    public int consultaNumeroPessoasLocalizacao(int x, int y) throws IOException {
        dos.writeUTF("consultaNumeroPessoasLocalizacao");
        dos.flush();

        dos.writeInt(x);
        dos.flush();

        dos.writeInt(y);
        dos.flush();

        return dis.readInt();
    }

    /**
     * Pede para notificar quando Localização está Livre
     * @param x                 Localização x a verificar
     * @param y                 Localização y a verificar
     * @return                  Booleano que mostra se consegue verificar localização
     * @throws IOException      Exception IO
     */
    public boolean consultaLocalizacaoLivre(int x, int y) throws IOException {
        dos.writeUTF("consultaLocalizacaoLivre");
        dos.flush();

        dos.writeInt(x);
        dos.flush();

        dos.writeInt(y);
        dos.flush();

        return dis.readBoolean();
    }

    /**
     * Utilizador notifica que está infetado
     * @return                  Booleano que mostra se Infeção foi bem notificada
     * @throws IOException      Exception IO
     */
    public boolean notificarInfecao() throws IOException {
        dos.writeUTF("notificarInfecao");
        dos.flush();

        return dis.readBoolean();
    }

    /**
     * Utilizador consulta Mapa de Localizações
     * @return                  String com dados necessários para construir o Map
     * @throws IOException      Exception IO
     */
    public String consultarMapaLocalizacoes() throws IOException {
        dos.writeUTF("consultarMapaLocalizacoes");
        dos.flush();

        return dis.readUTF();
    }
}
