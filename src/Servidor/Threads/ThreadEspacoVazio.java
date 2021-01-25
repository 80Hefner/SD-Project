package Servidor.Threads;

import Classes.Localizacao;
import Classes.Utilizador;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Classe responsável Thread que verifica qunado uma Localização fica vazia
 */
public class ThreadEspacoVazio implements Runnable{
    private Socket socketEscrita;
    private Localizacao loc;
    private Utilizador utilizador;

    /**
     * Construtor da ThreadEspacoVazio
     * @param socketEscrita     Socket para enviar avisos para um Utilizaodor (Client)
     * @param loc               Localização em estudo
     * @param utilizador        Utilizador que pediu verificação da Localização
     */
    public ThreadEspacoVazio (Socket socketEscrita, Localizacao loc, Utilizador utilizador) {
        this.socketEscrita = socketEscrita;
        this.loc = loc;
        this.utilizador = utilizador;
    }

    @Override
    /**
     * Método run
     */
    public void run() {
        try{
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socketEscrita.getOutputStream()));

            loc.getLockLocalizacao().lock();

            while (loc.consultaNumeroAtualUtilizadores() != 0 && utilizador.isLogado()) {
                loc.getCondLocalizacao().await();
            }

            loc.getLockLocalizacao().unlock();

            if (utilizador.isLogado()) {
                out.writeUTF("localizacao");
                out.flush();
                out.writeUTF(loc.getLocalizacaoX()+"-"+ loc.getLocalizacaoY());
                out.flush();
            }

        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

}
