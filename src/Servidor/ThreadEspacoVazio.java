package Servidor;

import Classes.Localizacao;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ThreadEspacoVazio implements Runnable{
    private Socket socketEscrita;
    private Localizacao loc;

    public ThreadEspacoVazio (Socket socketEscrita, Localizacao loc) {
        this.socketEscrita = socketEscrita;
        this.loc = loc;
    }

    @Override
    public void run() {
        try{
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socketEscrita.getOutputStream()));

            loc.getLockLocalizacao().lock();

            while (loc.consultaNumeroAtualUtilizadores() != 0) {
                loc.getCondLocalizacao().await();
            }

            loc.getLockLocalizacao().unlock();

            out.writeUTF("localizacao");
            out.flush();
            out.writeUTF(loc.getLocalizacaoX()+"-"+ loc.getLocalizacaoY());
            out.flush();

        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

}
