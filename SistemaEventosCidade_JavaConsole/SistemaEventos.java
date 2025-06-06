import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SistemaEventos {
    private List<Evento> eventos = new ArrayList<>();
    private List<Evento> eventosConfirmados = new ArrayList<>();
    private Usuario usuario;
    private Scanner scanner = new Scanner(System.in);
    private final String ARQUIVO = "events.data";

    public void iniciar() {
        carregarEventos();
        cadastrarUsuario();
        menuPrincipal();
        salvarEventos();
    }

    private void cadastrarUsuario() {
        System.out.print("Digite seu nome: ");
        String nome = scanner.nextLine();
        System.out.print("Digite seu e-mail: ");
        String email = scanner.nextLine();
        System.out.print("Digite sua idade: ");
        int idade = scanner.nextInt(); scanner.nextLine();

        usuario = new Usuario(nome, email, idade);
        System.out.println("Usuário cadastrado: " + usuario);
    }

    private void menuPrincipal() {
        int opcao;
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Cadastrar evento");
            System.out.println("2. Listar eventos disponíveis");
            System.out.println("3. Confirmar participação");
            System.out.println("4. Ver eventos confirmados");
            System.out.println("5. Cancelar participação");
            System.out.println("6. Ver eventos que já ocorreram");
            System.out.println("0. Sair");
            opcao = scanner.nextInt(); scanner.nextLine();

            switch (opcao) {
                case 1 -> cadastrarEvento();
                case 2 -> listarEventosDisponiveis();
                case 3 -> confirmarParticipacao();
                case 4 -> listarConfirmados();
                case 5 -> cancelarParticipacao();
                case 6 -> listarOcorridos();
            }
        } while (opcao != 0);
    }

    private void cadastrarEvento() {
        System.out.print("Nome do evento: ");
        String nome = scanner.nextLine();
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();
        System.out.print("Categoria (Show, Festa, Esporte, etc.): ");
        String categoria = scanner.nextLine();
        System.out.print("Data e hora (dd/MM/yyyy HH:mm): ");
        String data = scanner.nextLine();
        LocalDateTime horario = LocalDateTime.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        eventos.add(new Evento(nome, endereco, categoria, horario, descricao));
        System.out.println("Evento cadastrado com sucesso!");
    }

    private void listarEventosDisponiveis() {
        eventos.sort(Comparator.comparing(Evento::getHorario));
        for (int i = 0; i < eventos.size(); i++) {
            Evento ev = eventos.get(i);
            System.out.println((i + 1) + ". " + ev);
            if (ev.estaOcorrendo()) System.out.println(">>> Está ocorrendo agora!");
        }
    }

    private void confirmarParticipacao() {
        listarEventosDisponiveis();
        System.out.print("Digite o número do evento para confirmar: ");
        int i = scanner.nextInt() - 1; scanner.nextLine();
        if (i >= 0 && i < eventos.size()) {
            eventosConfirmados.add(eventos.get(i));
            System.out.println("Participação confirmada!");
        }
    }

    private void listarConfirmados() {
        if (eventosConfirmados.isEmpty()) {
            System.out.println("Nenhuma participação confirmada.");
        } else {
            System.out.println("Eventos confirmados:");
            for (Evento ev : eventosConfirmados) System.out.println("- " + ev);
        }
    }

    private void cancelarParticipacao() {
        listarConfirmados();
        System.out.print("Digite o número do evento a cancelar: ");
        int i = scanner.nextInt() - 1; scanner.nextLine();
        if (i >= 0 && i < eventosConfirmados.size()) {
            eventosConfirmados.remove(i);
            System.out.println("Participação cancelada.");
        }
    }

    private void listarOcorridos() {
        System.out.println("Eventos que já ocorreram:");
        for (Evento ev : eventos) {
            if (ev.jaOcorreu()) System.out.println("- " + ev);
        }
    }

    private void salvarEventos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Evento ev : eventos) {
                writer.write(ev.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    private void carregarEventos() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                eventos.add(Evento.fromDataString(linha));
            }
        } catch (IOException e) {
            System.out.println("Arquivo de eventos não encontrado, iniciando novo cadastro...");
        }
    }
}