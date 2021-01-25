package Servidor;

import Classes.MapasAplicacao;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe Servidor
 */
public class Servidor {

    static final int dimensao = 10;

    /**
     * Main Method de um Servidor
     */
    public static void main(String[] args) {

        try {

            MapasAplicacao mapasAplicacao = new MapasAplicacao(dimensao);
            ServerSocket ss = new ServerSocket(12345);
            ServerSocket ss2 = new ServerSocket(54321);

            while (true) {
                Socket socket = ss.accept();
                Socket socket2 = ss2.accept();
                Thread worker = new Thread (new ServerWorker(socket, socket2, mapasAplicacao));
                worker.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}


