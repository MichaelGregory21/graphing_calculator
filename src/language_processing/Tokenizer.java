/**
 * Converts a raw string into a list of tokens, including numbers, variables,
 * and items from a fixed dictionary. The tokenizer identifies and separates
 * tokens without parsing or interpreting their syntactic relationships.
 */
package language_processing;

import error_handling.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tokenizer {

    private final Map<String, String> DICTIONARY = Map.ofEntries(
        Map.entry("\\sin", "SIN"),
        Map.entry("\\cos", "COS"),
        Map.entry("\\tan", "TAN"),
        Map.entry("\\sinh", "HYPSIN"),
        Map.entry("\\cosh", "HYPCOS"),
        Map.entry("\\tanh", "HYPTAN"),
        Map.entry("\\csc", "CSC"),
        Map.entry("\\sec", "SEC"),
        Map.entry("\\cot", "COT"),
        Map.entry("\\csch", "HYPCSC"),
        Map.entry("\\sech", "HYPSEC"),
        Map.entry("\\coth", "HYPCOT"),

        Map.entry("\\ln", "LN"),
        Map.entry("\\log", "LOG"),
        Map.entry("\\exp", "EXP"),

        Map.entry("\\sqrt", "SQRT"),

        Map.entry("\\frac", "FRAC"),

        Map.entry("\\max", "MAX"),
        Map.entry("\\min", "MIN"),

        Map.entry("\\floor", "FLOOR"),
        Map.entry("\\ceiling", "CEILING"),
        Map.entry("\\round", "ROUND"),

        Map.entry("\\abs", "ABS"),

        Map.entry("\\pi", "PI"),
        Map.entry("\\e", "EULER"),
        Map.entry("\\infty", "INFTY"),
        Map.entry("\\R", "REALS"),
        Map.entry("\\Z", "INTEGERS"),
        Map.entry("\\N", "NATURALS"),
        Map.entry("\\emptyset", "EMPTYSET"),

        Map.entry("\\and", "AND"),
        Map.entry("\\or", "OR"),
        Map.entry("\\not", "NOT"),
        Map.entry("\\cup", "UNION"),
        Map.entry("\\cap", "INTERSECTION"),
        Map.entry("\\in", "IN"),
        Map.entry("\\if", "IF"),
        Map.entry(":", "IF"),

        Map.entry("+", "PLUS"),
        Map.entry("-", "MINUS"),
        Map.entry("*", "MULTIPLY"),
        Map.entry("/", "DIVIDE"),
        Map.entry("^", "POWER"),
        Map.entry("!", "FACTORIAL"),

        Map.entry("=", "EQUALS"),
        Map.entry("\\ne", "NOT_EQUALS"),
        Map.entry("<", "LESS_THAN"),
        Map.entry(">", "GREATER_THAN"),
        Map.entry("\\le", "LESS_EQUAL"),
        Map.entry("\\ge", "GREATER_EQUAL"),

        Map.entry("\\left", "DEFINITION"),
        Map.entry("\\right", "REV_DEFINITION"),

        Map.entry("\\left(", "LEFT_PAREN"),
        Map.entry("\\right)", "RIGHT_PAREN"),
        Map.entry("\\left[", "LEFT_BRACKET"),
        Map.entry("\\right]", "RIGHT_BRACKET"),
        Map.entry("\\left\\{", "LEFT_BRACE"),
        Map.entry("\\right\\}", "RIGHT_BRACE"),
        Map.entry("\\left|", "LEFT_ABS"),
        Map.entry("\\right|", "RIGHT_ABS"),

        Map.entry("[", "PLAIN_LEFT_BRACKET"),
        Map.entry("]", "PLAIN_RIGHT_BRACKET"),
        Map.entry("{", "PLAIN_LEFT_BRACE"),
        Map.entry("}", "PLAIN_RIGHT_BRACE"),
        
        Map.entry(",", "COMMA"),
        Map.entry("_", "BASE"),
        Map.entry("...", "ELLIPSIS")
    );

    private int index;
    private List<Token> tokenArray = new ArrayList<>();

    private boolean isAlpha(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private boolean isNum(char c) {
        return Character.isDigit(c);
    }

    private char current(String source) {
        return source.charAt(this.index);
    }

    private char peek(String source) {
        if (this.index + 1 >= source.length()) {
            throw new ParseException(
                "Cannot peek past end of input at position: " + this.index
            );
        }

        return source.charAt(this.index + 1);
    }

    private void advance() {
        this.index++;
    }

    private void advance(int amt) {
        this.index += amt;
    }

    private void consume(String type, String text, String subscript){
        tokenArray.add(new Token(type, text, subscript));
    }

    private void tokenizeDigit(String source) {
        if (this.index >= source.length()) {
            return;
        }

        if (this.isNum(this.current(source))) {
            int start = this.index;

            while (this.index < source.length() && Character.isDigit(this.current(source))) {

                this.advance();

                if (this.index < source.length() && this.current(source) == '.') {
                    this.advance();

                    if (this.index >= source.length() || !Character.isDigit(this.current(source))) {
                        throw new ParseException("Invalid number at position: " + start + ": a decimal point must be followed by digits");
                    }

                    while (this.index < source.length() && Character.isDigit(this.current(source))) {
                        this.advance();
                    }
                }

                if (this.index < source.length() && this.current(source) == '.') {
                    throw new ParseException("Invalid number at position: " + start + ": multiple decimal points");
                }
            }
            this.consume("NUMBER", source.substring(start, this.index), null);
            return;
        }
        if (this.current(source) == '.' && this.index + 1 < source.length() && Character.isDigit(this.peek(source))) {
            this.tokenizeDigit("0" + source);            
        }
    }

    private void tokenizeKeyword(String source) {
        if (this.index >= source.length()) {
            return;
        }

        String remaining = source.substring(this.index);
        String longestMatch = null;
        
        for (String keyword : DICTIONARY.keySet()) {
            if (remaining.startsWith(keyword)) {
                if (longestMatch == null || keyword.length() > longestMatch.length()) {
                    longestMatch = keyword;
                }
            }
        }

        if (longestMatch != null) {
            this.consume(DICTIONARY.get(longestMatch), longestMatch, null);
            this.advance(longestMatch.length());
            return;
        }
    }

    private void tokenizeVariable(String source) {
        if (this.index >= source.length()) {
            return;
        }
        // Variable must be letter
        if (!isAlpha(this.current(source))) {
            return;
        }
        String var = String.valueOf(this.current(source));
        String subscript = "";
        // If variable has no subscript, consume this character alone
        if (this.index + 1 >= source.length() || this.peek(source) != '_') {
            this.consume("VARIABLE", var, null);
            this.advance();
            return;
        }
        // If subscript character ends string, then throw an error
        if (this.index + 2 >= source.length()) {
            throw new ParseException("Invalid subscript at position: " + this.index);
        }  
        this.advance(2);
        // If subscript starts with a brace, then append to subscript until brace is closed
        if (this.current(source) == '{') {
            int openBrace = this.index;
            while (this.peek(source) != '}' && this.index < source.length()) {
                this.advance();
                if (!isAlpha(this.current(source)) && !isNum(this.current(source))) {
                    throw new ParseException(
                        "Subscript is not alphanumeric at position: " + this.index
                    );
                }
                subscript += String.valueOf(this.current(source));
            }

            if (this.peek(source) != '}') {
                throw new ParseException(
                    "Brace at position: " + openBrace + " is never closed"
                );
            }
        // If subscript not enclosed in braces, then assume next character is entire subscript
        } else {
            subscript += String.valueOf(this.current(source));
        }

        this.advance();
        this.consume("VARIABLE", var, (subscript.equals("")) ? null : subscript);
    }

    public List<Token> tokenize(String source) {
        this.index = 0;
        tokenArray.clear();
        source = source.replaceAll("\\s+", "");
        while (this.index < source.length()) {
            int currentIndex = this.index;
            this.tokenizeDigit(source);
            this.tokenizeKeyword(source);
            this.tokenizeVariable(source);          
            if (currentIndex == this.index) {
                throw new ParseException("Unrecognized token " + this.current(source) + " at position: " + this.index);
            }
        }
        tokenArray.add(new Token("END_OF_INPUT", null, null));
        return tokenArray;
    }

}
