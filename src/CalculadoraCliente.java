import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CalculadoraCliente {

	public static void main(String[] args) {
		String host = (args.length > 0) ? args[0] : "localhost"; // Host do servidor

		Registry reg = null; // Referência para o registry remoto
		ICalculadora calc; // Referência (stub) para o objeto remoto

		try {
			reg = LocateRegistry.getRegistry(host, 1099); // Localiza o registry do servidor na porta escolhida
			calc = (ICalculadora) reg.lookup("calculadora"); // Obtém o stub do objeto remoto pelo nome registrado

		// Caso haja falha de rede ou nome não encontrado, mostra o erro e encerra o cliente
		} catch (RemoteException | NotBoundException e) {
			System.out.println(e);
			System.exit(0);
			return;
		}

		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("\n=== Calculadora RMI ===");
			System.out.println("1 - Operação simples (ex: 10 + 15)");
			System.out.println("2 - Expressão calculada no CLIENTE");
			System.out.println("3 - Expressão calculada no SERVIDOR");
			System.out.println("0 - Sair");
			System.out.print("Escolha: ");
			String op = in.nextLine().trim();

			// Trata erros de rede e de cálculo em cada operação
			try {
				if (op.equals("0")) { // Sair
					break; // Sai do laço
				} else if (op.equals("1")) { // Operação simples
					System.out.print("Digite (ex.: 10 + 15): "); // Solicita a operação
					String[] p = in.nextLine().trim().split("\\s+"); // Separa operando, operador, operando por espaços
					if (p.length != 3) { // Valida o formato
						System.out.println("Formato inválido. Use: número operador número"); // Mensagem de erro
						continue; // Volta ao menu
					}
					double a = Double.parseDouble(p[0]); // Converte o primeiro operando
					double b = Double.parseDouble(p[2]); // Converte o segundo operando
					double r; // Resultado
					switch (p[1]) { // Escolhe a chamada remota conforme o operador
						case "+": r = calc.soma(a, b); break; // Invoca soma no servidor
						case "-": r = calc.subtracao(a, b); break; // Invoca subtração no servidor
						case "*": r = calc.multiplicacao(a, b); break; // Invoca multiplicação no servidor
						case "/": r = calc.divisao(a, b); break; // Invoca divisão no servidor
						default: System.out.println("Operador inválido."); continue; // Operador desconhecido
					}
					System.out.println("Resultado = " + r); // Exibe o resultado
				} else if (op.equals("2") || op.equals("3")) { // Expressões com parênteses
					System.out.print("Digite a expressão (ex.: (10 + 15) * 4): ");
					String expr = in.nextLine(); // Lê a expressão
					double r;
					// Abordagem 1: interpretação no cliente
					if (op.equals("2")) {
						r = new Avaliador(expr, calc).avalia(); // Avaliador local com o STUB: cada operação vira uma chamada remota

					// Abordagem 2: cálculo no servidor
					} else {
						r = calc.avaliaExpressao(expr); // Envia a expressão inteira e recebe só o resultado
					}
					System.out.println("Resultado = " + r);
				} else { // Opção inexistente
					System.out.println("Opção inválida.");
				}
			} catch (RemoteException e) {
				System.out.println("Erro de comunicação: " + e);
			} catch (RuntimeException e) {
				System.out.println("Erro: " + e.getMessage());
			}
		}
		in.close();
	}
}