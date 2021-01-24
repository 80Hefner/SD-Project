package Classes;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {

    private String username;
    private String password;
    private boolean admin;
    private int localizacaoX;
    private int localizacaoY;
    private boolean logado;
    private boolean infetado;
    private Map<String,Utilizador> utilizadoresComQuemContactou;
    private ReentrantLock lockUtilizador;

    public Utilizador(String username, String password, int dimensao) {
        this.username = username;
        this.password = password;

        Random r = new Random(); //Atribuída uma localização aleatória quando Utilizador é criado
        this.localizacaoX = r.nextInt(dimensao);
        this.localizacaoY = r.nextInt(dimensao);

        this.admin = false;
        this.logado = false;
        this.infetado = false;
        this.utilizadoresComQuemContactou = new TreeMap<String,Utilizador>();
        this.lockUtilizador = new ReentrantLock();
    }

    public Utilizador(String username, String password, boolean admin, int localizacaoX, int localizacaoY, TreeMap<String,Utilizador> utilizadoresComQuemContactou) {
        this.admin = admin;
        this.username = username;
        this.password = password;
        this.localizacaoX = localizacaoX;
        this.localizacaoY = localizacaoY;
        this.utilizadoresComQuemContactou = utilizadoresComQuemContactou;
        this.logado = false;
        this.infetado = false;
        this.lockUtilizador = new ReentrantLock();
    }

/*
    public void alteraLocalizacao (int novoX, int novoY, Set<String> novosContactos ) {
        lockUtilizador.lock();
        try {
            this.localizacaoX = novoX;
            this.localizacaoY = novoY;
            this.utilizadoresComQuemContactou.addAll(novosContactos);
        } finally {
            lockUtilizador.unlock();
        }
    }
 */

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getLocalizacaoX() {
        return localizacaoX;
    }

    public int getLocalizacaoY() {
        return localizacaoY;
    }

    public void login() {this.logado = true;}

    public void logout() {this.logado = false;}


    public void adicionaUtilizador (Utilizador utilizador) {
        if ( !this.utilizadoresComQuemContactou.containsKey( utilizador.getUsername()) ) {
            this.utilizadoresComQuemContactou.put( utilizador.getUsername(), utilizador );
        }
    }
}
