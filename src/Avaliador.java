import java.rmi.RemoteException;

// Avaliador de expressões aritméticas (descida recursiva).
//  Cliente: recebe o stub RMI -> cada operação vira uma chamada remota
//  Servidor: recebe um objeto local -> tudo é calculado sem rede
public class Avaliador {

    private final String txt;
    private int pos = 0; // Posição atual de leitura no texto
    private final ICalculadora calc;

    public Avaliador(String expressao, ICalculadora calc) {
        this.txt = expressao.replaceAll("\\s+", ""); // Remove todos os espaços em branco
        this.calc = calc;
    }

    public double avalia() throws RemoteException { // Ponto de entrada: avalia a expressão completa
        double v = expr(); // Processa a expressão a partir da regra de mais alto nível
        if (pos != txt.length()) { // Se sobrou texto não consumido, a expressão é inválida
            throw new IllegalArgumentException("Expressão inválida na posição " + pos); // Informa o erro
        }
        return v; // Retorna o resultado final
    }

    // expr = termo { (+|-) termo }
    private double expr() throws RemoteException {
        double v = termo(); // Lê o primeiro termo

        while (pos < txt.length()) {
            char c = txt.charAt(pos); // Caractere atual
            if (c != '+' && c != '-') break; // Sai se não for + nem -
            char op = txt.charAt(pos++); // Lê o operador e avança
            double d = termo(); // Lê o próximo termo

            if (op == '+') {
                v = calc.soma(v, d);
            } else {
                v = calc.subtracao(v, d);
            }
        }
        return v; // Retorna o valor acumulado
    }

    private double termo() throws RemoteException { // termo = fator { (*|/) fator }
        double v = fator(); // Lê o primeiro fator
        while (pos < txt.length()) {
            char c = txt.charAt(pos); // Caractere atual
            if (c != '*' && c != '/') break; // Sai se não for * nem /
            char op = txt.charAt(pos++); // Lê o operador e avança
            double d = fator(); // Lê o próximo fator

            // Multiplicação ou divisão (remota ou local)
            if (op == '*') {
                v = calc.multiplicacao(v, d);
            } else {
                v = calc.divisao(v, d);
            }
        }
        return v; // Retorna o valor acumulado
    }

    // Regra: fator = número | '(' expr ')' | '-' fator
    private double fator() throws RemoteException {
        if (pos >= txt.length()) throw new IllegalArgumentException("Expressão incompleta"); // Fim inesperado
        char c = txt.charAt(pos); // Olha o caractere atual
        if (c == '-') { // Menos unário (ex: -5)
            pos += 1; // Consome o '-'
            return calc.subtracao(0, fator()); // 0 - fator (inverte o sinal)
        }
        if (c == '(') { // Início de subexpressão entre parênteses
            pos++; // Consome o '('
            double v = expr(); // Avalia a subexpressão
            if (pos >= txt.length()) {
                throw new IllegalArgumentException("Parêntese não fechado"); // Erro de sintaxe
            } else if (txt.charAt(pos) != ')') {
                throw new IllegalArgumentException("Parêntese não fechado"); // Erro de sintaxe
            }
            pos++; // Consome o ')'
            return v; // Retorna o valor da subexpressão
        }

        int ini = pos; // Marca o início do número
        while (pos < txt.length()) {
            char d = txt.charAt(pos); // Caractere atual
            if (!Character.isDigit(d) && d != '.') break; // Sai se não for dígito nem ponto
            pos++; // Avança enquanto for parte do número
        }

        if (ini == pos) throw new IllegalArgumentException("Número esperado na posição " + pos); // Nenhum dígito lido
        return Double.parseDouble(txt.substring(ini, pos)); // Converte o texto lido para double
    }
}