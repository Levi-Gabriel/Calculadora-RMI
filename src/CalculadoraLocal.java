import java.rmi.RemoteException; // Necessária porque a interface declara essa exceção nos métodos

// Implementação puramente local de ICalculadora: sem rede e sem log.
// O servidor a usa para calcular expressões internamente (abordagem 2) e também para
// executar as operações básicas chamadas remotamente.
public class CalculadoraLocal implements ICalculadora {

    public double soma(double a, double b) throws RemoteException { // Soma local
        return a + b;
    }

    public double subtracao(double a, double b) throws RemoteException { // Subtração local
        return a - b;
    }

    public double multiplicacao(double a, double b) throws RemoteException { // Multiplicação local
        return a * b;
    }

    public double divisao(double a, double b) throws RemoteException { // Divisão local
        if (b == 0) { // Verifica divisão por zero
            throw new ArithmeticException("Divisão por zero"); // Erro repassado ao chamador
        }
        return a / b;
    }

    public double avaliaExpressao(String expressao) throws RemoteException {
        return new Avaliador(expressao, this).avalia();
    }
}