package Cliente;

import Servidor.Servidor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private final BufferedReader systemIn;
    private ClientStub currentUser;
    private boolean admin;
    //todo criar classe ClientListener

    public Client() throws IOException {
        this.systemIn = new BufferedReader(new InputStreamReader(System.in));
        this.currentUser = null;
        this.admin = false;
    }

    public static void main(String[] args) {
        try {
            new Client().executaMenuLogin();
        }
        catch (Exception e) {
            System.out.println("Programa terminou com erro\n" + e.getMessage());
        }
    }



    // Execução Menus
    private void executaMenuLogin() throws IOException {
        boolean continua = true;

        while (continua) {
            try {
                showMenuLogin();
                int opcao = Integer.parseInt(systemIn.readLine());

                switch (opcao) {
                    case 0: // Sair
                        continua = false;
                        System.out.println("Programa terminado com sucesso.");
                        break;

                    case 1: // Login
                        currentUser = new ClientStub();
                        int respostaLogin = login();

                        if (respostaLogin != -1) {
                            System.out.println("Login com sucesso.");
                            if (respostaLogin == 1) admin = true;
                            executaMenuPrincipal();
                        }
                        else {
                            System.out.println("Login sem sucesso.");
                            currentUser.endSession();
                            currentUser = null;
                        }
                        break;

                    case 2: // Registar
                        currentUser = new ClientStub();
                        boolean respostaRegisto = registar();

                        if (respostaRegisto) {
                            System.out.println("Registo com sucesso.");
                            executaMenuPrincipal();
                        }
                        else {
                            System.out.println("Registo sem sucesso.");
                            currentUser.endSession();
                            currentUser = null;
                        }
                        break;

                    default:
                        System.out.println("Input incorreto.");
                        break;
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Input inválido.");
            }
        }
    }

    private void executaMenuPrincipal() throws IOException {
        boolean continua = true;
        // todo boolean infetado

        while (continua) {
            try {
                showMenuPrincipal();
                int opcao = Integer.parseInt(systemIn.readLine());

                switch (opcao) {
                    case 0: // Logout
                        boolean respostaLogout = currentUser.logout();

                        if (respostaLogout) {
                            continua = false;
                            System.out.println("Logout efetuado com sucesso.");
                            currentUser.endSession();
                            currentUser = null;
                            admin = false;
                        }
                        else {
                            System.out.println("Logout efetuado sem sucesso.");
                        }
                        break;

                    case 1: // Atualizar localização
                        boolean respostaAtualizarLocalizacao = atualizarLocalizacao();

                        if (respostaAtualizarLocalizacao) {
                            System.out.println("Localização atualizada com sucesso.");
                        } else {
                            System.out.println("Localização não atualizada.");
                        }
                        break;

                    case 2: // Consulta Numero de Pessoas numa Localização
                        int numeroPessoasLocalizacao = consultaNumeroPessoasLocalizacao();

                        if (numeroPessoasLocalizacao != -1) {
                            System.out.println("Localização pedida possui " + numeroPessoasLocalizacao + " Utilizadores no momento!");
                        } else {
                            System.out.println("Localização pedida não pode ser verificada.");
                        }
                        break;

                    case 6: // Consulta Mapa de Localizações
                        if (!admin) {
                            System.out.println("Input incorreto.");
                            break;
                        }
                        String mapaLocalizacoesUtilizadores = consultarMapaLocalizacoes();
                        //System.out.println("MAPA TESTE " + mapaLocalizacoesUtilizadores);
                        showMapaLocalizacoes(mapaLocalizacoesUtilizadores);
                        break;

                    default:
                        System.out.println("Input incorreto.");
                        break;
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Input inválido.");
            }
        }
    }

    // Opções Menu Principal
    private boolean atualizarLocalizacao() throws IOException, NumberFormatException {
        System.out.println("Nova Localização");
        System.out.print("X: ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y: ");
        int y = Integer.parseInt(systemIn.readLine());

        return (currentUser.atualizarLocalizacao(x, y));
    }

    private int consultaNumeroPessoasLocalizacao() throws IOException, NumberFormatException {
        System.out.println("Localização a verificar");
        System.out.print("X: ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y: ");
        int y = Integer.parseInt(systemIn.readLine());

        return (currentUser.consultaNumeroPessoasLocalizacao(x, y));
    }

    private String consultarMapaLocalizacoes() throws IOException {
        return (currentUser.consultarMapaLocalizacoes());
    }

    private void showMapaLocalizacoes (String mapa) throws NumberFormatException {

        List<String> informacoesMapa = Arrays.asList(mapa.split(":"));
        int dimensao = Integer.parseInt(informacoesMapa.get(0));

        for (int linha = 0; linha<dimensao; linha++) {

            for (int coluna = 0; coluna<dimensao; coluna++) {

                List<String> informacoesIndice = Arrays.asList(informacoesMapa.get(linha*dimensao+coluna+1).split("-"));
                int nrUtilizadores = Integer.parseInt(informacoesIndice.get(0));
                int nrInfetados = Integer.parseInt(informacoesIndice.get(1));

                System.out.print("  " + nrUtilizadores + "|" + nrInfetados);

            }

            System.out.println();
        }
    }

    // Opções Menu Login
    private int login() throws IOException {
        System.out.print("Nome de utilizador: ");
        String user = systemIn.readLine();

        System.out.print("Password: ");
        String password = systemIn.readLine();

        return (currentUser.login(user, password));
    }

    private boolean registar() throws IOException, NumberFormatException {
        System.out.print("Nome de utilizador: ");
        String user = systemIn.readLine();

        System.out.print("Password: ");
        String password = systemIn.readLine();

        System.out.println("Localização");
        System.out.print("X: ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y: ");
        int y = Integer.parseInt(systemIn.readLine());

        return (currentUser.registar(user, password, x, y));
    }



    // Show Menus
    private void showMenuLogin() {
        System.out.println("\n *** Menu Login *** ");
        System.out.println("1 - Login");
        System.out.println("2 - Registar");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");
    }

    private void showMenuPrincipal() {
        System.out.println("\n *** Menu Principal *** ");
        System.out.println("1 - Atualizar Localização");
        System.out.println("2 - Consulta Número de Pessoas numa Localizacao");
        if (admin)
            System.out.println("6 - Consulta Mapa de Localizações");
        System.out.println("0 - Logout");
        System.out.print("Opção: ");
    }

}
