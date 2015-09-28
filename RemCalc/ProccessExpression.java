/**
* Proccess a mathematical expression treating addition,
* subtraction, multiplication, division and exponentiation.
* Also treating backets.
*/
public class ProccessExpression {
    private static ProccessExpression instance;

    private ProccessExpression() {
    }

    /**
    * returns the class singleton instance
    */
    public static synchronized ProccessExpression getInstance() {
        if(instance == null) {
            instance = new ProccessExpression();
        }
        return instance;
    }


    int pos = -1, c;
    String str;
            
    private void eatChar() {
        c = (++pos < str.length()) ? str.charAt(pos) : -1;
    }

    private void eatSpace() {
        while (Character.isWhitespace(c)) eatChar();
    }

    private double parse() {
        eatChar();
        double v = parseExpression();
        if (c != -1) throw new RuntimeException("Unexpected: " + (char)c);
        return v;
    }

    // Grammar:
    // expression = term | expression `+` term | expression `-` term
    // term = factor | term `*` factor | term `/` factor | term brackets
    // factor = brackets | number | factor `^` factor
    // brackets = `(` expression `)`

    private double parseExpression() {
        double v = parseTerm();
        for (;;) {
            eatSpace();
            if (c == '+') { // addition
                eatChar();
                v += parseTerm();
            } else if (c == '-') { // subtraction
                eatChar();
                v -= parseTerm();
            } else {
                return v;
            }
        }
    }

    private double parseTerm() {
        double v = parseFactor();
        for (;;) {
            eatSpace();
            if (c == '/') { // division
                eatChar();
                v /= parseFactor();
            } else if (c == '*' || c == '(') { // multiplication
                if (c == '*') eatChar();
                v *= parseFactor();
            } else {
                return v;
            }
        }
    }

    private double parseFactor() {
        double v;
        boolean negate = false;
        eatSpace();
        if (c == '+' || c == '-') { // unary plus & minus
            negate = c == '-';
            eatChar();
            eatSpace();
        }
        if (c == '(') { // brackets
            eatChar();
            v = parseExpression();
            if (c == ')') eatChar();
        } else { // numbers
            StringBuilder sb = new StringBuilder();
            while ((c >= '0' && c <= '9') || c == '.') {
                sb.append((char)c);
                eatChar();
            }
            if (sb.length() == 0) throw new RuntimeException("Unexpected: " + (char)c);
            v = Double.parseDouble(sb.toString());
        }
        eatSpace();
        if (c == '^') { // exponentiation
            eatChar();
            v = Math.pow(v, parseFactor());
        }
        if (negate) v = -v; // unary minus is applied after exponentiation; e.g. -3^2=-9
        return v;
    }

    /**
    * method responsable to proccess the mathematical expression
    */
    public double eval(final String str) {
        instance.str = str;
        double result = instance.parse();
        instance.str = null;
        return result;
    }
}