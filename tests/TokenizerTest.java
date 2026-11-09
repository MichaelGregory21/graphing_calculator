package tests;

import org.junit.jupiter.api.Test;

import error_handling.ParseException;
import language_processing.Token;
import language_processing.Tokenizer;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TokenizerTest {

    @Test void testValidNumber() {
        Tokenizer tokenizer = new Tokenizer();

        List<Token> tokens = tokenizer.tokenize("123");
        Token token = new Token("NUMBER", "123", null);
        assertEquals(token, tokens.get(0));
        tokens.clear();

        tokens = tokenizer.tokenize("3.14");
        token = new Token("NUMBER", "3.14", null);
        assertEquals(token, tokens.get(0));
        tokens.clear();

        tokens = tokenizer.tokenize(".5");
        token = new Token("NUMBER", "0.5", null);
        assertEquals(token, tokens.get(0));
        tokens.clear();
    }

    @Test void testInvalidNumber() {
        Tokenizer tokenizer = new Tokenizer();

        assertThrows(ParseException.class, () -> {
            tokenizer.tokenize("3.");
        });

        assertThrows(ParseException.class, () -> {
            tokenizer.tokenize("..5");
        });
        
        assertThrows(ParseException.class, () -> {
            tokenizer.tokenize("0.51.3");
        });
    }

    @Test void testValidVariable() {
        Tokenizer tokenizer = new Tokenizer();

        List<Token> tokens = tokenizer.tokenize("x");
        Token token = new Token("VARIABLE", "x", null);
        assertEquals(token, tokens.get(0));
        tokens.clear();

        tokens = tokenizer.tokenize("y_1");
        token = new Token("VARIABLE", "y", "1");
        assertEquals(token, tokens.get(0));
        tokens.clear();

        tokens = tokenizer.tokenize("z_{12}");
        token = new Token("VARIABLE", "z", "12");
        assertEquals(token, tokens.get(0));
        tokens.clear();
    }

    @Test void testInvalidVariable() {
        Tokenizer tokenizer = new Tokenizer();

        assertThrows(ParseException.class, () -> {
            tokenizer.tokenize("x_");
        });

        assertThrows(ParseException.class, () -> {
            tokenizer.tokenize("y_{1");
        });

        assertThrows(ParseException.class, () -> {
            tokenizer.tokenize("z_{1+2}");
        });
    }

    @Test void testValidKeyword() {
        Tokenizer tokenizer = new Tokenizer();

        List<Token> tokens = tokenizer.tokenize("\\sin");
        Token token = new Token("SIN", "\\sin", null);
        assertEquals(token, tokens.get(0)); 

        List<Token> tokens2 = tokenizer.tokenize("\\frac");
        Token token2 = new Token("FRAC", "\\frac", null);    
        assertEquals(token2, tokens2.get(0));

        List<Token> tokens3 = tokenizer.tokenize("\\left(");
        Token token3 = new Token("LEFT_PAREN", "\\left(", null);    
        assertEquals(token3, tokens3.get(0));
    }

    @Test void testInvalidKeyword() {
        Tokenizer tokenizer = new Tokenizer();

        assertThrows(ParseException.class, () -> {
            tokenizer.tokenize("\\unknown");
        });
    }

    @Test void testMixedInput() {
        Tokenizer tokenizer = new Tokenizer();

        List<Token> tokens = tokenizer.tokenize("\\sin\\left(\\frac{\\pi}{2}\\right) + x_1");
        assertEquals(new Token("SIN", "\\sin", null), tokens.get(0));
        assertEquals(new Token("LEFT_PAREN", "\\left(", null), tokens.get(1));
        assertEquals(new Token("FRAC", "\\frac", null), tokens.get(2));
        assertEquals(new Token("PLAIN_LEFT_BRACE", "{", null), tokens.get(3));
        assertEquals(new Token("PI", "\\pi", null), tokens.get(4));
        assertEquals(new Token("PLAIN_RIGHT_BRACE", "}", null), tokens.get(5));
        assertEquals(new Token("PLAIN_LEFT_BRACE", "{", null), tokens.get(6));
        assertEquals(new Token("NUMBER", "2", null), tokens.get(7));
        assertEquals(new Token("PLAIN_RIGHT_BRACE", "}", null), tokens.get(8));
        assertEquals(new Token("RIGHT_PAREN", "\\right)", null), tokens.get(9));
        assertEquals(new Token("PLUS", "+", null), tokens.get(10));
        assertEquals(new Token("VARIABLE", "x", "1"), tokens.get(11));

        List<Token> tokens2 = tokenizer.tokenize("3.14 + y_{12}");
        assertEquals(new Token("NUMBER", "3.14", null), tokens2.get(0));
        assertEquals(new Token("PLUS", "+", null), tokens2.get(1));
        assertEquals(new Token("VARIABLE", "y", "12"), tokens2.get(2));
    }
}
