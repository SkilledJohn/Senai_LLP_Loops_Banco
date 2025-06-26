import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BancoInterativo {
    public static void main(String[] args) {
        Banco banco = new Banco();
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== SISTEMA BANCÁRIO ===");
            System.out.println("1. Abrir Conta");
            System.out.println("2. Depositar");
            System.out.println("3. Sacar");
            System.out.println("4. Aplicar Rendimento (Poupança)");
            System.out.println("5. Listar Contas");
            System.out.println("0. Sair");
            System.out.print("Opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Tipo (1-Corrente / 2-Poupança): ");
                    int tipo = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Número da conta: ");
                    String numero = scanner.nextLine();
                    System.out.print("Titular: ");
                    String titular = scanner.nextLine();
                    
                    if (tipo == 1) {
                        System.out.print("Taxa de manutenção: ");
                        banco.abrirConta(new ContaCorrente(numero, titular, scanner.nextDouble()));
                    } else {
                        System.out.print("Taxa de rendimento (%): ");
                        banco.abrirConta(new ContaPoupanca(numero, titular, scanner.nextDouble() / 100));
                    }
                    scanner.nextLine();
                    break;

                case 2:
                    System.out.print("Número da conta: ");
                    String contaDep = scanner.nextLine();
                    System.out.print("Valor: ");
                    banco.realizarOperacao(contaDep, "depositar", scanner.nextDouble());
                    scanner.nextLine();
                    break;

                case 3:
                    System.out.print("Número da conta: ");
                    String contaSaq = scanner.nextLine();
                    System.out.print("Valor: ");
                    banco.realizarOperacao(contaSaq, "sacar", scanner.nextDouble());
                    scanner.nextLine();
                    break;

                case 4:
                    System.out.print("Número da poupança: ");
                    ContaBancaria conta = banco.buscarConta(scanner.nextLine());
                    if (conta instanceof ContaPoupanca) {
                        ((ContaPoupanca) conta).aplicarRendimento();
                    } else {
                        System.out.println("Erro: Conta não é poupança!");
                    }
                    break;

                case 5:
                    banco.listarContas();
                    break;

                case 0:
                    System.out.println("Encerrando sistema...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        scanner.close();
    }
}

class ContaBancaria {
    private String numeroConta;
    private double saldo;
    private String titular;

    public ContaBancaria(String numeroConta, String titular) {
        this.numeroConta = numeroConta;
        this.titular = titular;
    }

    public void depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
            System.out.printf("Depósito: +R$%.2f | Saldo: R$%.2f\n", valor, saldo);
        } else {
            System.out.println("Valor inválido!");
        }
    }

    public void sacar(double valor) {
        if (valor > 0 && saldo >= valor) {
            saldo -= valor;
            System.out.printf("Saque: -R$%.2f | Saldo: R$%.2f\n", valor, saldo);
        } else {
            System.out.println("Saldo insuficiente ou valor inválido!");
        }
    }

    public String getNumeroConta() { return numeroConta; }
    public double getSaldo() { return saldo; }
    public String getTitular() { return titular; }
}

class ContaCorrente extends ContaBancaria {
    private double taxaManutencao;

    public ContaCorrente(String numeroConta, String titular, double taxaManutencao) {
        super(numeroConta, titular);
        this.taxaManutencao = taxaManutencao;
    }

    @Override
    public void sacar(double valor) {
        double total = valor + taxaManutencao;
        if (getSaldo() >= total) {
            super.sacar(valor);
            super.sacar(taxaManutencao);
            System.out.printf("Taxa: -R$%.2f | ", taxaManutencao);
        } else {
            System.out.println("Saldo insuficiente para saque + taxa!");
        }
    }
}

class ContaPoupanca extends ContaBancaria {
    private double taxaRendimento;

    public ContaPoupanca(String numeroConta, String titular, double taxaRendimento) {
        super(numeroConta, titular);
        this.taxaRendimento = taxaRendimento;
    }

    public void aplicarRendimento() {
        double rendimento = getSaldo() * taxaRendimento;
        depositar(rendimento);
        System.out.printf("Rendimento: +R$%.2f (%.2f%%)\n", rendimento, taxaRendimento * 100);
    }
}

class Banco {
    private List<ContaBancaria> contas = new ArrayList<>();

    public void abrirConta(ContaBancaria conta) {
        contas.add(conta);
        System.out.printf("Conta %s aberta para %s\n", conta.getNumeroConta(), conta.getTitular());
    }

    public ContaBancaria buscarConta(String numero) {
        for (ContaBancaria conta : contas) {
            if (conta.getNumeroConta().equals(numero)) return conta;
        }
        System.out.println("Conta não encontrada!");
        return null;
    }

    public void realizarOperacao(String numero, String tipo, double valor) {
        ContaBancaria conta = buscarConta(numero);
        if (conta != null) {
            if (tipo.equalsIgnoreCase("depositar")) conta.depositar(valor);
            else if (tipo.equalsIgnoreCase("sacar")) conta.sacar(valor);
            else System.out.println("Operação inválida!");
        }
    }

    public void listarContas() {
        System.out.println("\n=== CONTAS CADASTRADAS ===");
        for (ContaBancaria conta : contas) {
            System.out.printf("%s | %s | Saldo: R$%.2f\n", 
                conta.getNumeroConta(), conta.getTitular(), conta.getSaldo());
        }
    }
}
