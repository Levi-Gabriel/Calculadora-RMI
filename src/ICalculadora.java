import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface remota: define o contrato que o cliente enxerga e que o servidor implementa
// Permite que os métodos sejam invocados via rede
public interface ICalculadora extends Remote {

	public double soma(double a, double b) throws RemoteException; // soma (a + b)

	public double subtracao(double a, double b) throws RemoteException; // subtração (a - b)

	public double multiplicacao(double a, double b) throws RemoteException; // Operação remota de multiplicação (a * b)

	// lança ArithmeticException se b == 0
	public double divisao(double a, double b) throws RemoteException; // divisão (a / b)

	// O cliente envia a expressão inteira e o servidor calcula tudo
	public double avaliaExpressao(String expressao) throws RemoteException; // Retorna o resultado final da expressão
}