import java.rmi.AccessException; // Exceção lançada quando o acesso ao registry é negado
import java.rmi.AlreadyBoundException; // Exceção lançada ao tentar fazer bind de um nome já registrado
import java.rmi.RemoteException; // Exceção base para falhas de comunicação RMI
import java.rmi.registry.LocateRegistry; // Classe para criar/obter o registry de nomes do RMI
import java.rmi.registry.Registry; // Representa o registry (serviço de nomes) do RMI
import java.rmi.server.UnicastRemoteObject; // Permite exportar um objeto para receber chamadas remotas

// Objeto remoto: recebe as chamadas RMI, registra no log e delega o cálculo ao CalculadoraLocal
public class Calculadora implements ICalculadora {

	private static final long serialVersionUID = 1L; // Identificador de versão de serialização

	private static int chamadas = 0; // Contador de chamadas remotas recebidas (apenas para log)

	private final CalculadoraLocal local = new CalculadoraLocal();

	// Registram a chamada no console do servidor

	public double soma(double a, double b) throws RemoteException { // Soma remota
		System.out.println("Método soma chamado " + chamadas++);
		return local.soma(a, b); // Calcula e devolve ao cliente
	}

	public double subtracao(double a, double b) throws RemoteException { // Subtração remota
		System.out.println("Método subtracao chamado " + chamadas++);
		return local.subtracao(a, b);
	}

	public double multiplicacao(double a, double b) throws RemoteException { // Multiplicação remota
		System.out.println("Método multiplicacao chamado " + chamadas++);
		return local.multiplicacao(a, b);
	}

	public double divisao(double a, double b) throws RemoteException { // Divisão remota
		System.out.println("Método divisao chamado " + chamadas++);
		return local.divisao(a, b);
	}

	// Abordagem 2: expressão inteira calculada no servidor
	public double avaliaExpressao(String expressao) throws RemoteException {
		System.out.println("Método avaliaExpressao chamado " + chamadas++ + ": " + expressao);
		return local.avaliaExpressao(expressao); // Avalia tudo localmente e retorna só o resultado final
	}

	public static void main(String[] args) throws AccessException, RemoteException, AlreadyBoundException {
		Calculadora calculadora = new Calculadora(); // Cria a instância do objeto remoto
		Registry reg = null; // Referência para o registry

		// Exporta o objeto na porta escolhida e obtém o stub
		ICalculadora stub = (ICalculadora) UnicastRemoteObject.exportObject(calculadora, 1100);

		try { // Tenta criar um novo registry
			System.out.println("Creating registry...");
			reg = LocateRegistry.createRegistry(1099); // Cria o registry na porta desejada

		// Se já existir um registry nessa port, tenta obter o registry existente
		// Caso falhe, encerra o servidor
		} catch (Exception e) {
			try {
				reg = LocateRegistry.getRegistry(1099);
			} catch (Exception e1) {
				System.exit(0);
			}
		}
		reg.rebind("calculadora", stub); // Registra o stub sob o nome "calculadora" (sobrescreve se já existir)
		System.out.println("Servidor da calculadora pronto.");
	}
}