package Classes;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class MapasAplicacao {

    private Map<Integer, Localizacao> mapaLocalizacoes;
    private Map<String, Utilizador> mapaUtilizadores;
    private ReentrantLock lockMapasUtilizadores;
    private ReentrantLock lockMapasLocalizacoes;

    public MapasAplicacao (int dimensao) {

        this.mapaLocalizacoes = new TreeMap<Integer, Localizacao>();

        for (int linha = 0; linha<dimensao; linha++) {
            for (int coluna = 0; coluna<dimensao; coluna++) {
                int indiceMapa = linha*dimensao + coluna;
                Localizacao loc = new Localizacao (linha, coluna);
                mapaLocalizacoes.put(indiceMapa, loc);
            }
        }


        this.mapaUtilizadores = new TreeMap<String, Utilizador>();

        Utilizador admin1 = new Utilizador("admin1","admin1", true, 0, 0);
        mapaUtilizadores.put (admin1.getUsername(), admin1);
        mapaLocalizacoes.get(0).adicionaUtilizadorLocalizacao(admin1);

        Utilizador user1 = new Utilizador("user1","user1", false, 2, 2);
        mapaUtilizadores.put (user1.getUsername(), user1);
        mapaLocalizacoes.get(2*dimensao + 2).adicionaUtilizadorLocalizacao(user1);

        Utilizador user2 = new Utilizador("user2","user2", false, 5, 5);
        mapaUtilizadores.put (user2.getUsername(), user2);
        mapaLocalizacoes.get(5*dimensao + 5).adicionaUtilizadorLocalizacao(user2);

        Utilizador admin2 = new Utilizador("admin2","admin2", true, 9, 9);
        mapaUtilizadores.put (admin2.getUsername(), admin2);
        mapaLocalizacoes.get(5*dimensao + 5).adicionaUtilizadorLocalizacao(admin2);

        this.lockMapasUtilizadores = new ReentrantLock();
        this.lockMapasLocalizacoes = new ReentrantLock();
    }

    public Map<Integer, Localizacao> getMapaLocalizacoes() {
        return mapaLocalizacoes;
    }

    public Map<String, Utilizador> getMapaUtilizadores() {
        return mapaUtilizadores;
    }

    public ReentrantLock getLockMapasUtilizadores() {
        return lockMapasUtilizadores;
    }

    public ReentrantLock getLockMapasLocalizacoes() {
        return lockMapasLocalizacoes;
    }


    public Localizacao getLocalizacao (int locX, int locY, int dimensao) {
        return mapaLocalizacoes.get(locX*dimensao + locY);
    }

    public void pingTodasLocalizacoes() {
        for (Localizacao loc : mapaLocalizacoes.values())
            loc.enviaPing();
    }
}
