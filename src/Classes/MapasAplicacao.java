package Classes;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class MapasAplicacao {

    private Map<Integer, Localizacao> mapaLocalizacoes;
    private Map<String, Utilizador> mapaUtilizadores;
    private ReentrantLock lockMapasUtilizadores;

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

        Utilizador user1 = new Utilizador("user1","user1", true, 0, 0, new TreeMap<String,Utilizador>());
        mapaUtilizadores.put (user1.getUsername(), user1);
        mapaLocalizacoes.get(0).adicionaUtilizadorLocalizacao(user1);

        Utilizador user2 = new Utilizador("user2","user2", false, 2, 2, new TreeMap<String,Utilizador>());
        mapaUtilizadores.put (user2.getUsername(), user2);
        mapaLocalizacoes.get(2*dimensao + 2).adicionaUtilizadorLocalizacao(user2);

        Utilizador user3 = new Utilizador("user3","user3", false, 5, 5, new TreeMap<String,Utilizador>());
        mapaUtilizadores.put (user3.getUsername(), user3);
        mapaLocalizacoes.get(5*dimensao + 5).adicionaUtilizadorLocalizacao(user3);

        this.lockMapasUtilizadores = new ReentrantLock();
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


    public Localizacao getLocalizacao (int locX, int locY, int dimensao) {
        return mapaLocalizacoes.get(locX*dimensao + locY);
    }

}
