package Servidor.Threads;

import Classes.Utilizador;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ThreadAvisaContacto implements Runnable {

    private Socket socketEscrita;
    private Utilizador utilizador;

    public ThreadAvisaContacto (Socket socketEscrita, Utilizador utilizador) {
        this.socketEscrita = socketEscrita;
        this.utilizador = utilizador;
    }

    @Override
    public void run() {
        try{
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socketEscrita.getOutputStream()));

            utilizador.getLockUtilizador().lock();

            if (utilizador.isAvisaContactoInfetado()) {
                out.writeUTF("avisaContacto");
                out.flush();
                utilizador.setAvisaContactoInfetado(false);
            }

            while (!utilizador.isAvisaContactoInfetado()) {
                utilizador.getCondUtilizador().await();
                if (utilizador.isAvisaContactoInfetado()) {
                    out.writeUTF("avisaContacto");
                    out.flush();
                    utilizador.setAvisaContactoInfetado(false);
                }
            }

            utilizador.getLockUtilizador().unlock();

        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }


}
