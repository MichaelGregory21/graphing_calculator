import java.util.List;

import language_processing.Parser;
import language_processing.Token;
import language_processing.Tokenizer;

public class Main {

    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tarray = tokenizer.tokenize("\\sin\\left(\\frac{\\pi}{2}\\right) + x_1");
        Parser parser = new Parser(tarray);
        System.out.println(parser.parse().pretty());
    }
}
