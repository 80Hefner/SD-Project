package Servidor;

import Classes.Localizacao;
import Classes.Utilizador;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;


public class Servidor {

    static final int dimensao = 10;

    public static void main(String[] args) throws IOException { //todo::::::: try catch aqui

        Map<Integer, Localizacao> mapaLocalizacoes = new TreeMap<Integer, Localizacao>();
        Map<String, Utilizador> mapaUtilizadores = new TreeMap<String, Utilizador>(); //todo criar classe para gerir mapas para limpar main
        ReentrantLock lockMapaUtilizadores = new ReentrantLock();

        for (int linha = 0; linha<dimensao; linha++) {
            for (int coluna = 0; coluna<dimensao; coluna++) {
                int indiceMapa = linha*dimensao + coluna;
                Localizacao loc = new Localizacao (linha, coluna);
                mapaLocalizacoes.put(indiceMapa, loc);
            }
        }

        Utilizador user1 = new Utilizador("user1","user1", true, 0, 0, new TreeSet<String>());
        mapaUtilizadores.put (user1.getUsername(), user1);
        mapaLocalizacoes.get(0).adicionaUtilizador(user1.getUsername());

        Utilizador user2 = new Utilizador("user2","user2", false, 2, 2, new TreeSet<String>());
        mapaUtilizadores.put (user2.getUsername(), user2);
        mapaLocalizacoes.get(2*dimensao + 2).adicionaUtilizador(user1.getUsername());

        Utilizador user3 = new Utilizador("user3","user3", false, 5, 5, new TreeSet<String>());
        mapaUtilizadores.put (user3.getUsername(), user3);
        mapaLocalizacoes.get(5*dimensao + 5).adicionaUtilizador(user3.getUsername());

        ServerSocket ss = new ServerSocket(12345);

        while (true) {
            Socket socket = ss.accept();
            Thread worker = new Thread (new ServerWorker(socket, mapaUtilizadores, mapaLocalizacoes, lockMapaUtilizadores));
            worker.start();
        }

    }

}


/* Podemos ter um:
        . Map com localizações. como são dimensoes n por n. se fosse quadrado linha 1, coluna 2. (copmeça em 0 ambas) era fazer get(1*n + 2-1)
        basicamente. Depois em cada uma, ter um conjunto de utilizadores ou string com o username, uma com os que já passaram e outra com os doentes.
        . Utilizador possuir um set com os outros users com os quais teve contacto.
 */

