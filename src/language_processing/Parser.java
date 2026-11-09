/**
 * Recursive-descent parser for the math-language tokenizer output
 */

package language_processing;

import error_handling.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser {
    private final Map<String, Integer> PRECEDENCE = Map.ofEntries(
        Map.entry("OR", 10),
        Map.entry("AND", 20),
        Map.entry("EQUALS", 30),
        Map.entry("NOT_EQUALS", 30),
        Map.entry("LESS_THAN", 30),
        Map.entry("GREATER_THAN", 30),
        Map.entry("LESS_EQUAL", 30),
        Map.entry("GREATER_EQUAL", 30),
        Map.entry("IN", 30),
        Map.entry("UNION", 35),
        Map.entry("INTERSECTION", 35),
        Map.entry("PLUS", 40),
        Map.entry("MINUS", 40),
        Map.entry("MULTIPLY", 50),
        Map.entry("DIVIDE", 50),
        Map.entry("IMPLICIT_MULTIPLY", 50),
        Map.entry("POWER", 60),
        Map.entry("FACTORIAL", 70)
    );

    private record VariableKey(String name, String subscript) {}

    private static final Set<String> CONSTANTS = Set.of(
        "EULER", "PI", "INFTY", "REALS", "INTEGERS", "NATURALS", "EMPTYSET"
    );

    private static final Set<String> FUNCTIONS = Set.of(
        "SIN", "COS", "TAN", "SEC", "CSC", "COT",
        "HYPSIN", "HYPCOS", "HYPTAN", "HYPSEC", "HYPCSC", "HYPCOT",
        "LN", "LOG", "EXP"
    );

    private List<Token> tokens;
    private HashMap<String, Node> symbolTable;
    private int index = 0;

    private static final double NUMERIC_TOLERANCE = 1e-12;

// ---------------------------------------------------------------------
// CONSTRUCTORS & PUBLIC ENTRY POINT
// ---------------------------------------------------------------------

    public Parser(List<Token> tokens, HashMap<String, Node> symbolTable) {
        this.tokens = new ArrayList<>(tokens);

        if (this.tokens.isEmpty() || !this.tokens.getLast().type().equals("END_OF_INPUT")) {
            this.tokens.add(new Token("END_OF_INPUT", null, null));
        }

        this.symbolTable = (symbolTable == null) ? new HashMap<>() : symbolTable;
    }

    public Parser(List<Token> tokens) {
        this(tokens, null);
    }

    /**
     * Public entry point. Attempts to parse list of tokens, first as regular definition, then as reversed definition, and finally, as ordinary object.
     * REGULAR DEFINITION: A mathematical object assigned to a global variable from right to left, e.g., f(x)<-x^2, A(x,y)={a in R: x-a<y}
     * REVERSE DEFINITION: A mathematical object assigned to a global variable from left to right, e.g., x^2->f(x)
     * ORDINARY OBJECT: A mathematical object or relation of objects, e.g. x^2+2x+1, x+y=x-y, a<b
     * @return
     */
    public Node parse() {

    /*
     * CASE 1:
     * Forward definition.
     *
     * The left-hand side must be a valid definition header,
     * rather than an arbitrary expression containing equality.
     */
    if (this.isForwardDefinitionCandidate()) {

        Node definition = this.parseDefinition();

        Token operator = this.current();

        if (!operator.type().equals("EQUALS") &&
            !operator.type().equals("DEFINITION")) {

            throw new ParseException(
                "Expected a definition operator after the definition header"
            );
        }

        this.advance();

        if (this.isAtEnd()) {
            throw new ParseException(
                "Expected an expression after the definition operator"
            );
        }

        Node body = this.parseExpression(0);

        this.expect("END_OF_INPUT");

        return this.buildDefinition(definition, body, operator);
    }

    /*
     * CASE 2:
     * Reverse definition.
     *
     * Locate the reverse definition operator at the top level,
     * without modifying the original token list.
     */
    int reverseDefinitionIndex =
        this.findTopLevelToken("REV_DEFINITION");

    if (reverseDefinitionIndex != -1) {

        int endIndex = this.tokens.size();

        if (endIndex > 0 &&
            this.tokens.getLast().type().equals("END_OF_INPUT")) {

            endIndex--;
        }

        // Both sides of the operator must contain tokens.
        if (reverseDefinitionIndex == 0) {
            throw new ParseException(
                "Expected an expression before the reverse definition operator"
            );
        }

        if (reverseDefinitionIndex == endIndex - 1) {
            throw new ParseException(
                "Expected a definition after the reverse definition operator"
            );
        }

        List<Token> expressionTokens = new ArrayList<>(
            this.tokens.subList(0, reverseDefinitionIndex)
        );

        List<Token> definitionTokens = new ArrayList<>(
            this.tokens.subList(reverseDefinitionIndex + 1, endIndex)
        );

        // Parse the right-hand side as a definition header.
        Parser definitionParser =
            new Parser(definitionTokens, this.symbolTable);

        Node definition = definitionParser.parseDefinition();

        definitionParser.expect("END_OF_INPUT");

        // Parse the left-hand side as an ordinary expression.
        Parser expressionParser =
            new Parser(expressionTokens, this.symbolTable);

        Node body = expressionParser.parseExpression(0);

        expressionParser.expect("END_OF_INPUT");

        Token operator = this.tokens.get(reverseDefinitionIndex);

        return this.buildDefinition(definition, body, operator);
    }

    /*
     * CASE 3:
     * Ordinary mathematical object.
     *
     * This includes equations and conditions with free variables.
     */
    Node result = this.parseExpression(0);

    this.expect("END_OF_INPUT");

    return result;
}
// ---------------------------------------------------------------------
// TOKEN NAVIGATION
// ---------------------------------------------------------------------

    private Token current() {
        return this.tokens.get(this.index);
    }

    private Token advance() {
        return this.tokens.get(this.index++);
    }

    private Token accept(String type) {
        if (this.current().type().equals(type)) {
            return this.advance();
        }
        return null;
    }

    private Token expect(String type) {
        Token token = this.accept(type);
        if (token == null) {
            throw new ParseException("Expected token of type " + type + " but found " + this.current().type() + " at position " + this.index);
        }
        return token;
    }

    private boolean isAtEnd() {
        return this.current().type().equals("END_OF_INPUT");
    }

// ---------------------------------------------------------------------
// DEFINITION PARSING
// ---------------------------------------------------------------------

    private boolean isForwardDefinitionCandidate() {
        if (!this.current().type().equals("VARIABLE")) {
            return false;
        }

        int i = this.index + 1;

        // A definition without parameters: f <- ...
        if (i < this.tokens.size() &&
            this.tokens.get(i).type().equals("DEFINITION")) {
            return true;
        }

        // A definition with parameters: f(x,y) <- ...
        if (i >= this.tokens.size() ||
            !this.tokens.get(i).type().equals("LEFT_PAREN")) {
            return false;
        }

        int depth = 1;
        i++;

        while (i < this.tokens.size() && depth > 0) {
            String type = this.tokens.get(i).type();

            if (type.equals("END_OF_INPUT")) {
                return false;
            }

            if (type.equals("LEFT_PAREN")) {
                depth++;
            } else if (type.equals("RIGHT_PAREN")) {
                depth--;
            }

            i++;
        }

        if (depth != 0 || i >= this.tokens.size()) {
            return false;
        }

        return this.tokens.get(i).type().equals("DEFINITION");
    }

    private Node buildDefinition(Node definition, Node body, Token operator) {
        List<Node> params = definition.children() == null
            ? new ArrayList<>()
            : definition.children();

        String type = this.classifyBody(body);

        Map<String, Object> nameAttributes = new HashMap<>();
        nameAttributes.put(
            "subscript",
            definition.attributes() == null
                ? null
                : definition.attributes().get("subscript")
        );

        Node name = new Node(
            "NAME",
            definition.value(),
            null,
            nameAttributes
        );

        Node freeVars = new Node("FREE_VARIABLES", null, params, null);
        Node bodyType = new Node("TYPE", type, List.of(body), null);

        if (operator.type().equals("EQUALS") && type.equals("CONDITION")) {
            throw new ParseException(
                "Cannot define a condition with an equals sign at position " + this.index
            );
        }

        return new Node(
            "DEFINITION",
            operator.type(),
            List.of(name, freeVars, bodyType),
            null
        );
    }

    /**
     * Parses a global variable with a collection of zero or more parameters, e.g. f(x,y), A, or g(x)
     * @return a Node representing a definition together with a list of parameters
     */
    private Node parseDefinition() {
        Token name = this.expect("VARIABLE");              
        List<Node> params = new ArrayList<>();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("subscript", name.subscript());

        if (this.accept("LEFT_PAREN") != null) {

            if (this.current().type().equals("RIGHT_PAREN")) {
                throw new ParseException("A definition cannot have an empty parameter list at position " + this.index);
            }

            Set<VariableKey> uniqueParams = new HashSet<>();

            while (true) {
                Token param = this.expect("VARIABLE");

                VariableKey key = new VariableKey(param.text(), param.subscript());

                if (!uniqueParams.add(key)) {
                    throw new ParseException("Duplicate parameter " + this.formatVariableName(param) + " in definition at position " + this.index);
                }

                params.add(this.parameterNode(param));

                if (this.accept("COMMA") == null) {
                    break;
                }

                if (this.current().type().equals("RIGHT_PAREN")) {
                    throw new ParseException("Expected a parameter after comma at position " + this.index);
                }
            }
            this.expect("RIGHT_PAREN");
        }
        
        return new Node("DEFINITION", name.text(), params, attributes);
    }

// ---------------------------------------------------------------------
// EXPRESSION PARSING
// ---------------------------------------------------------------------
    
    private Node parseExpression(int minPrecedence, boolean allowSeparator) {
        Node left = this.parsePrefix();

        while (true) {
            Token tok = this.current();

            if (tok.type().equals("IF")) {
                if (!allowSeparator || minPrecedence > 0) {
                    break;
                }
                this.advance();
                Node condition = this.parseCondition();
                left = new Node("RESTRICTED_EXPRESSION", null, List.of(left, condition), null);
                continue;
            }

            if (tok.type().equals("FACTORIAL")) {
                if (PRECEDENCE.get(tok.type()) < minPrecedence) {
                    break;
                }
                this.advance();
                left = new Node("FACTORIAL", null, List.of(left), null);
                continue;
            }

            if (tok.type().equals("LEFT_BRACKET")) {
                this.advance();
                Node index = this.parseExpression(0, false);
                this.expect("RIGHT_BRACKET");
                left = new Node("INDEX", null, List.of(left, index), null);
                continue;
            }

            boolean implicit = this.startsAtom(tok) && this.canEndAtom(left);
            String opType = implicit ? "IMPLICIT_MULTIPLY" : tok.type();

            int prec = this.PRECEDENCE.getOrDefault(opType, -1);
            if (prec < minPrecedence) {
                break;
            }

            if (implicit) {
                Node right = this.parseExpression(prec + 1, false);
                left = new Node("MULTIPLY", "implicit", List.of(left, right), null);
                continue;
            }

            if (tok.type().equals("POWER")) {
                this.advance();
                this.expect("PLAIN_LEFT_BRACE");

                if (this.current().type().equals("PLAIN_RIGHT_BRACE")) {
                    throw new ParseException("Exponent cannot be empty at position " + this.index);
                }

                Node exponent = this.parseExpression(0, false);
                this.expect("PLAIN_RIGHT_BRACE");

                left = new Node("POWER", "^", List.of(left, exponent), null);
                continue;
            }

            this.advance();
            Node right = this.parseExpression(minPrecedence + 1, false);
            left = new Node(opType, tok.text(), List.of(left, right), null);
            
        }
        return left;
    }

    private Node parseExpression(int minPrecedence) {
        return this.parseExpression(minPrecedence, true);
    }

    private Node parseCondition() {
        Node condition = this.parseExpression(0, false);
        if (!this.isCondition(condition)) {
            throw new ParseException("Expected a condition after separator");
        }
        return condition;
    }

    private Node parsePrefix() {
        Token tok = this.current();

        // UNARY OPERATORS
        if (tok.type().equals("UNARY_PLUS") ||
            tok.type().equals("MINUS") ||
            tok.type().equals("NOT")) {

            this.advance();

            int precedence = tok.type().equals("UNARY_PLUS")
                ? PRECEDENCE.get("PLUS")
                : PRECEDENCE.get(tok.type());

            Node child = this.parseExpression(precedence, false);

            String kind = switch (tok.type()) {
                case "UNARY_PLUS" -> "UNARY_PLUS";
                case "MINUS" -> "NEGATE";
                case "NOT" -> "NOT";
                default -> throw new ParseException("Unexpected unary operator");
            };

            return new Node(kind, tok.text(), List.of(child), null);
        }

        // NUMBERS
        if (tok.type().equals("NUMBER")) {
            this.advance();
            return new Node("NUMBER", tok.text(), null, null);
        }

        // VARIABLES AND FUNCTIONS
        if (tok.type().equals("VARIABLE")) {
            this.advance();

            List<Node> params = this.parseFunctionParameters(tok);

            if (this.current().type().equals("LEFT_PAREN")) {
                if (params == null) {
                    return this.parseFunctionCall(tok, null);
                }

                return this.parseFunctionCall(tok, params);
            }

            if (params != null && !params.isEmpty()) {
                throw new ParseException(
                    "Function " + this.formatVariableName(tok)
                    + " requires " + params.size() + " arguments"
                );
            }

            return this.variableNode(tok);
        }

        // BUILT-IN FUNCTIONS AND SPECIAL SYMBOLS
        if (FUNCTIONS.contains(tok.type())) {
            this.advance();

            // If function is log, check for a base subscript
            Node base = null;
            if (tok.type().equals("LOG")) {
                base = new Node("NUMBER", "10", null, null);
                if (this.accept("BASE") != null) {
                    this.expect("PLAIN_LEFT_BRACE");
                    base = this.parseExpression(0, false);
                    this.expect("PLAIN_RIGHT_BRACE");
                }                
            }

            Node arg;
            if (this.accept("PLAIN_LEFT_BRACE") != null) {
                arg = this.parseExpression(0, false);
                this.expect("PLAIN_RIGHT_BRACE");
            }
            else {
                arg = this.parseExpression(55, false);
            }

            List<Node> children = new ArrayList<>();
            if (base != null) {
                children.add(base);
            }
            children.add(arg);
            return new Node("FUNCTION_CALL", tok.text(), children, null);
        }

        if (CONSTANTS.contains(tok.type())) {
            this.advance();
            return new Node("CONSTANT", tok.text(), null, null);
        }

        // FRACTIONS
        if (tok.type().equals("FRAC")) {
            this.advance();
            Node numerator = this.parseGroupArgument();
            Node denominator = this.parseGroupArgument();
            return new Node("DIVIDE", "/", List.of(numerator, denominator), null);
        }

        // SQUARE ROOTS
        if (tok.type().equals("SQRT")) {
            this.advance();

            Node degree = new Node("NUMBER", "2", null, null);

            if (this.accept("LEFT_BRACKET") != null) {
                degree = this.parseExpression(0, false);
                this.expect("RIGHT_BRACKET");
            }

            Node radicand = this.parseGroupArgument();
            return new Node("ROOT", tok.text(), List.of(degree, radicand), null);         
        }

        // TUPLES
        if (tok.type().equals("LEFT_PAREN")) {
            this.advance();
            Node first = this.parseExpression(0, false);
            if (this.accept("COMMA") != null) {
                List<Node> args = new ArrayList<>();
                args.add(first);
                args.add(this.parseExpression(0, false));
                while (this.accept("COMMA") != null) {
                    args.add(this.parseExpression(0, false));
                }
                this.expect("RIGHT_PAREN");
                return new Node("TUPLE", null, args, null);
            }
            this.expect("RIGHT_PAREN");
            return new Node("GROUP", null, List.of(first), null);
        }

        // SETS AND PIECEWISE FUNCTIONS
        if (tok.type().equals("LEFT_BRACE")) {
            return this.parseStructuralBrace();
        }

        // OTHER GROUPINGS
        if (tok.type().equals("PLAIN_LEFT_BRACE")) {
            this.advance();
            Node inner = this.parseExpression(0);
            this.expect("PLAIN_RIGHT_BRACE");
            return new Node("GROUP", null, List.of(inner), null);
        }

        throw new ParseException("Unexpected token " + tok.type() + " at position " + this.index);      
    }

    private Node parseGroupArgument() {
        if (this.accept("PLAIN_LEFT_BRACE") != null) {
            Node expr = this.parseExpression(0);
            this.expect("PLAIN_RIGHT_BRACE");
            return expr;
        }
        return this.parsePrefix();
    }

    private Node parseFunctionCall(Token function, List<Node> params) {
        this.expect("LEFT_PAREN");

        List<Node> args = new ArrayList<>();

        if (!this.current().type().equals("RIGHT_PAREN")) {
            args.add(this.parseExpression(0, false));

            while (this.accept("COMMA") != null) {
                args.add(this.parseExpression(0, false));
            }
        }

        this.expect("RIGHT_PAREN");

        if (params != null && args.size() != params.size()) {
            throw new ParseException(
                "Function " + this.formatVariableName(function)
                + " expects " + params.size()
                + " arguments, but got " + args.size()
            );
        }

        return new Node(
            "FUNCTION_CALL",
            this.formatVariableName(function),
            args,
            null
        );
    }

    private List<Node> parseFunctionParameters(Token token) {
        String key = this.formatVariableName(token);

        Node definition = this.symbolTable.get(key);

        // The variable is not defined.
        if (definition == null) {
            return null;
        }

        if (!definition.type().equals("DEFINITION")) {
            throw new ParseException(
                "Invalid symbol table entry for " + key
                + ": expected DEFINITION but found "
                + definition.type()
            );
        }

        List<Node> children = definition.children();

        if (children == null || children.size() != 3) {
            throw new ParseException(
                "Malformed definition for " + key
            );
        }

        Node freeVariables = children.get(1);

        if (freeVariables == null ||
            !freeVariables.type().equals("FREE_VARIABLES")) {

            throw new ParseException(
                "Expected FREE_VARIABLES in definition of " + key
            );
        }

        List<Node> parameters = freeVariables.children();

        // A definition with no parameters.
        if (parameters == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(parameters);
    }

// ---------------------------------------------------------------------
// STRUCTURAL EXPRESSIONS
// ---------------------------------------------------------------------
    private Node parseStructuralBrace() {
        this.expect("LEFT_BRACE");
        int start = this.index;
        int end = this.findMatchingBrace(start);
        List<Token> content = this.tokens.subList(start, end);
        this.index = end + 1;

        // If braces of the form {}, then return empty set
        if (content.isEmpty()) {
            return new Node("SET_LITERAL", "\\emptyset", null, new HashMap<String, Object>() {{ put("finite", true); }});
        }

        // Comprehension recognized by a membership operator in the first clause
        List<List<Token>> clauses = this.splitTopLevel(content, "IF");
        List<Token> first = clauses.get(0);
        if (this.containsTopLevelType(first, "IN")) {
            int inPos = this.findTopLevelType(first, "IN");
            List<String> boundVars = this.parseBoundVariables(first.subList(0, inPos));
            Node source = this.parseFragment(first.subList(inPos + 1, first.size()));
            List<Node> conditions = new ArrayList<>();
            for (int i = 1; i < clauses.size(); i++) {
                if (!clauses.get(i).isEmpty()) {
                    conditions.add(this.parseFragment(clauses.get(i)));
                }                
            }
            Node condition = combineConditions(conditions);
            return new Node("COMPREHENSION", null, List.of(source, condition), new HashMap<String, Object>() {{ put("boundVars", boundVars); }});
        }

        // A comma-separated list of branches with conditional separators is piecewise. A branch without a separator is the default
        List<List<Token>> branches = this.splitTopLevel(content, "COMMA");
        boolean someBranchesHaveConditions = false;
        for (List<Token> branch : branches) {
            if (this.containsTopLevelType(branch, "IF")) {
                someBranchesHaveConditions = true;
                break;
            }
        }
        if (branches.size() > 1 && someBranchesHaveConditions) {
            return new Node("EXPRESSION", null, this.parsePiecewise(branches), new HashMap<String, Object>() {{ put("piecewise", true); put("domain", "R"); }});
        }

        // A comma-separated value or a trailing ellipsis may be a set literal or inferred infinite sequence
        boolean hasTopLevelComma = branches.size() > 1;
        boolean hasEllipsis = false;
        for (Token tok : content) {
            if (tok.type().equals("ELLIPSIS")) {
                hasEllipsis = true;
                break;
            }
        }
        if (hasTopLevelComma || hasEllipsis) {
            return this.parseSetLiteral(content);
        }

        // If no source set is specified, assume specification over R^n where n is number of bound variables
        Node whole = this.parseFragment(content);
        if (this.isCondition(whole)) {
            List<Token> variables = this.findVariables(whole);
            List<Token> boundVariables = new ArrayList<>();

            for (Token variable : variables) {
                if (!this.symbolTable.containsKey(this.formatVariableName(variable)) &&
                    !this.symbolTable.containsKey(variable.text())) {
                    boundVariables.add(variable);
                }
            }
            Node sourceSetBase = new Node("REALS", "\\R", null, null);
            Node sourceSet;

            if (boundVariables.size() == 1) {
                sourceSet = sourceSetBase;
            } else {
                Node exponent = new Node("NUMBER", Integer.toString(boundVariables.size()), null, null);
                sourceSet = new Node("POWER", "^", List.of(sourceSetBase, exponent), null);
            }
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("boundVars", boundVariables);

            return new Node("COMPREHENSION", null, List.of(sourceSet, whole), attributes);
        }

        // Otherwise, the separator introduces a domain restriction
        if (clauses.size() > 1) {
            Node expr = this.parseFragment(clauses.get(0));
            List<Node> domains = new ArrayList<>();
            for (List<Token> clause : clauses.subList(1, clauses.size())) {
                if (clause != null) {
                    domains.add(this.parseFragment(clause));
                };
            }
            Node domain = combineConditions(domains);
            if (!this.isCondition(domain)) {
                throw new ParseException("A domain restriction must be a condition");
            }
            return new Node("RESTRICTED_EXPRESSION", null, List.of(expr, domain), null);
        }
        return new Node("SET_LITERAL", null, List.of(whole), new HashMap<String, Object>() {{ put("finite", true); }});       
    }    
    
    private Node parseSetLiteral(List<Token> content) {
        List<List<Token>> clauses = this.splitTopLevel(content, "COMMA");

        int ellipsisCount = 0;
        int ellipsisClause = -1;
        int ellipsisPosition = -1;

        // Locate the ellipsis.
        for (int ci = 0; ci < clauses.size(); ci++) {
            for (int ti = 0; ti < clauses.get(ci).size(); ti++) {
                if (clauses.get(ci).get(ti).type().equals("ELLIPSIS")) {
                    ellipsisCount++;
                    ellipsisClause = ci;
                    ellipsisPosition = ti;
                }
            }
        }

        if (ellipsisCount > 1) {
            throw new ParseException(
                "Only one ellipsis is supported in a set pattern"
            );
        }

        // No ellipsis: parse an ordinary finite set.
        if (ellipsisCount == 0) {
            List<Node> elements = new ArrayList<>();

            for (List<Token> clause : clauses) {
                if (clause.isEmpty()) {
                    throw new ParseException(
                        "Expected a set element between commas"
                    );
                }

                elements.add(this.parseFragment(clause));
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("finite", true);

            return new Node("SET_LITERAL", null, elements, attributes);
        }

        // Separate the elements before and after the ellipsis.
        List<List<Token>> beforeClauses = new ArrayList<>();
        List<List<Token>> afterClauses = new ArrayList<>();

        // All complete clauses before the ellipsis.
        for (int i = 0; i < ellipsisClause; i++) {
            if (clauses.get(i).isEmpty()) {
                throw new ParseException(
                    "Expected a set element before the ellipsis"
                );
            }

            beforeClauses.add(clauses.get(i));
        }

        // Any tokens before the ellipsis in its own clause.
        List<Token> ellipsisTokens = clauses.get(ellipsisClause);

        if (ellipsisPosition > 0) {
            beforeClauses.add(
                new ArrayList<>(ellipsisTokens.subList(0, ellipsisPosition))
            );
        }

        // Any tokens after the ellipsis in its own clause.
        if (ellipsisPosition + 1 < ellipsisTokens.size()) {
            afterClauses.add(
                new ArrayList<>(
                    ellipsisTokens.subList(
                        ellipsisPosition + 1,
                        ellipsisTokens.size()
                    )
                )
            );
        }

        // All complete clauses after the ellipsis.
        for (int i = ellipsisClause + 1; i < clauses.size(); i++) {
            if (clauses.get(i).isEmpty()) {
                throw new ParseException(
                    "Expected a set element after the ellipsis"
                );
            }

            afterClauses.add(clauses.get(i));
        }

        // Parse the elements before the ellipsis.
        List<Node> beforeElements = new ArrayList<>();

        for (List<Token> clause : beforeClauses) {
            if (clause.isEmpty()) {
                throw new ParseException(
                    "Expected a set element before the ellipsis"
                );
            }

            beforeElements.add(this.parseFragment(clause));
        }

        if (beforeElements.isEmpty()) {
            throw new ParseException(
                "A set pattern needs at least one term before the ellipsis"
            );
        }

        // Parse the elements after the ellipsis.
        List<Node> afterElements = new ArrayList<>();

        for (List<Token> clause : afterClauses) {
            if (clause.isEmpty()) {
                throw new ParseException(
                    "Expected a set element after the ellipsis"
                );
            }

            afterElements.add(this.parseFragment(clause));
        }

        // Combine all explicitly supplied elements.
        List<Node> elements = new ArrayList<>(beforeElements);
        elements.addAll(afterElements);

        /*
        * CASE 1:
        * No upper-bound elements were supplied.
        *
        * Infer an infinite arithmetic sequence from the elements
        * preceding the ellipsis.
        */
        if (afterElements.isEmpty()) {
            if (beforeElements.size() < 3) {
                throw new ParseException(
                    "Require at least 3 elements to infer an infinite pattern"
                );
            }

            List<Double> values = new ArrayList<>();

            for (Node node : beforeElements) {
                values.add(numericValue(node));
            }

            double first = values.get(0);
            double step = values.get(1) - values.get(0);

            if (Math.abs(step) <= NUMERIC_TOLERANCE) {
                throw new ParseException(
                    "An arithmetic sequence cannot have a zero step"
                );
            }

            // Check that every preceding element follows the same step.
            for (int i = 2; i < values.size(); i++) {
                double actualStep = values.get(i) - values.get(i - 1);

                if (Math.abs(actualStep - step) > NUMERIC_TOLERANCE) {
                    throw new ParseException(
                        "Arithmetic sequence with inconsistent step size"
                    );
                }
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("pattern", "arithmetic");
            attributes.put("first", first);
            attributes.put("step", step);
            attributes.put("inferred", true);

            return new Node(
                "INFINITE_SET_PATTERN",
                null,
                beforeElements,
                attributes
            );
        }

        /*
        * CASE 2:
        * Upper-bound elements were supplied.
        *
        * If fewer than two initial elements are available, the step
        * cannot be inferred. Preserve the explicitly supplied elements
        * as a finite set pattern without claiming an arithmetic pattern.
        */
        if (beforeElements.size() < 2) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("finite", true);
            attributes.put("inferred", false);

            return new Node(
                "FINITE_SET_PATTERN",
                null,
                elements,
                attributes
            );
        }

        List<Double> beforeValues = new ArrayList<>();

        for (Node node : beforeElements) {
            beforeValues.add(numericValue(node));
        }

        double first = beforeValues.get(0);
        double step = beforeValues.get(1) - beforeValues.get(0);

        if (Math.abs(step) <= NUMERIC_TOLERANCE) {
            throw new ParseException(
                "An arithmetic sequence cannot have a zero step"
            );
        }

        // Verify that all elements before the ellipsis follow the same step.
        for (int i = 2; i < beforeValues.size(); i++) {
            double actualStep = beforeValues.get(i) - beforeValues.get(i - 1);

            if (Math.abs(actualStep - step) > NUMERIC_TOLERANCE) {
                throw new ParseException(
                    "Arithmetic sequence with inconsistent step size"
                );
            }
        }

        /*
        * Validate every element after the ellipsis.
        *
        * Each upper-bound element must:
        * 1. Be a numeric term of the inferred arithmetic sequence.
        * 2. Occur after the preceding explicitly supplied term.
        * 3. Follow the previous upper-bound element consecutively.
        */
        List<Double> afterValues = new ArrayList<>();

        for (Node node : afterElements) {
            afterValues.add(numericValue(node));
        }

        double previousIndex = beforeValues.size() - 1;

        for (int i = 0; i < afterValues.size(); i++) {
            double value = afterValues.get(i);

            double sequenceIndex = (value - first) / step;
            double nearestIndex = Math.rint(sequenceIndex);

            if (Math.abs(sequenceIndex - nearestIndex) > NUMERIC_TOLERANCE) {
                throw new ParseException(
                    "Upper-bound element is not part of the inferred arithmetic sequence"
                );
            }

            if (nearestIndex <= previousIndex) {
                throw new ParseException(
                    "Upper-bound elements must follow the preceding sequence terms"
                );
            }

            if (i > 0 &&
                Math.abs(nearestIndex - previousIndex - 1) > NUMERIC_TOLERANCE) {

                throw new ParseException(
                    "Upper-bound elements must be consecutive terms of the arithmetic sequence"
                );
            }

            previousIndex = nearestIndex;
        }

        double last = afterValues.getLast();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("finite", true);
        attributes.put("inferred", true);
        attributes.put("pattern", "arithmetic");
        attributes.put("first", first);
        attributes.put("step", step);
        attributes.put("last", last);

        return new Node(
            "FINITE_SET_PATTERN",
            null,
            elements,
            attributes
        );
    }
    
    private List<Node> parsePiecewise(List<List<Token>> branches) {
        List<Node> result = new ArrayList<>();
        boolean hasDefault = false;

        for (List<Token> branch : branches) {
            if (branch.isEmpty()) {
                throw new ParseException("Empty piecewise branch");
            }

            List<List<Token>> parts = this.splitTopLevel(branch, "IF");

            Node expression = this.parseFragment(parts.get(0));

            if (parts.size() == 1) {
                if (hasDefault) {
                    throw new ParseException("Multiple default branches in piecewise expression");
                }

                hasDefault = true;
                result.add(new Node("PIECE", null, List.of(expression), null));
                continue;
            }

            if (hasDefault) {
                throw new ParseException("A default branch must be the final piecewise branch");
            }

            List<Node> conditions = new ArrayList<>();

            for (List<Token> part : parts.subList(1, parts.size())) {
                if (part.isEmpty()) {
                    throw new ParseException("Expected a condition after separator");
                }

                Node condition = this.parseFragment(part);

                if (!this.isCondition(condition)) {
                    throw new ParseException("A piecewise branch must have a condition");
                }

                conditions.add(condition);
            }

            Node combinedCondition = combineConditions(conditions);

            result.add(new Node(
                "PIECE",
                null,
                List.of(expression, combinedCondition),
                null
            ));
        }

        return result;
    }

    private static Node combineConditions(List<Node> conditions) {
        if (conditions.isEmpty()) {return new Node("CONDITION", "true", null, null);}
        if (conditions.size() == 1) {return conditions.get(0);}
        return new Node("AND", "and", conditions, null);
    }

    private int findMatchingBrace(int start) {
        int depth = 1;
        for (int j = start; j < this.tokens.size(); j++) {
            String typ = this.tokens.get(j).type();
            if (typ.equals("END_OF_INPUT")) {break;}
            if (typ.equals("LEFT_BRACE")) {depth++;}
            if (typ.equals("RIGHT_BRACE")) {depth--;}
            if (depth == 0) {return j;}
        }
        throw new ParseException("Missing closing delimiter for position " + start);
    }

// ---------------------------------------------------------------------
// FRAGMENT & VARIABLE PARSING
// ---------------------------------------------------------------------
    
    private Node parseFragment(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new ParseException("Expected an expression, found an empty fragment");
        }
        Parser sub = new Parser(tokens, this.symbolTable);
        Node node = sub.parseExpression(0);
        if (!sub.isAtEnd()) {
            Token tok = sub.current();
            throw new ParseException("Unconsumed token in fragment: " + tok.type() + ":" + tok.text());
        }
        return node;
    }

    private List<String> parseBoundVariables(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            throw new ParseException(
                "Expected at least one bound variable"
            );
        }

        List<List<Token>> parts;

        // A single unparenthesized variable.
        if (tokens.size() == 1 &&
            tokens.get(0).type().equals("VARIABLE")) {

            parts = List.of(tokens);

        // A parenthesized variable list.
        } else if (tokens.get(0).type().equals("LEFT_PAREN") &&
                tokens.get(tokens.size() - 1).type().equals("RIGHT_PAREN")) {

            List<Token> contents = tokens.subList(1, tokens.size() - 1);

            if (contents.isEmpty()) {
                throw new ParseException(
                    "A bound-variable list cannot be empty"
                );
            }

            parts = this.splitTopLevel(contents, "COMMA");

        } else {
            throw new ParseException(
                "Invalid bound-variable list in set comprehension"
            );
        }

        List<String> result = new ArrayList<>();
        Set<VariableKey> uniqueVariables = new HashSet<>();

        for (List<Token> part : parts) {
            if (part.size() != 1 ||
                !part.get(0).type().equals("VARIABLE")) {

                throw new ParseException(
                    "Expected a variable in the bound-variable list"
                );
            }

            Token variable = part.get(0);

            VariableKey key = new VariableKey(
                variable.text(),
                variable.subscript()
            );

            if (!uniqueVariables.add(key)) {
                throw new ParseException(
                    "Duplicate bound variable: "
                    + this.formatVariableName(variable)
                );
            }

            result.add(this.formatVariableName(variable));
        }

        return result;
    }

    private List<Token> findVariables(Node node) {
        List<VariableKey> found = new ArrayList<>();
        this.collectVariables(node, found);
        List<Token> result = new ArrayList<>();
        for (VariableKey key : found) {
            result.add(new Token("VARIABLE", key.name(), key.subscript()));
        }
        return result;
    }

    private void collectVariables(Node node, List<VariableKey> found) {

        if (node.type().equals("VARIABLE")) {
            VariableKey key = new VariableKey(node.value(), (String) node.attributes().get("subscript"));
            if (!found.contains(key)) {
                found.add(key);
            }
        }
        for (Node child : node.children()) {
            collectVariables(child, found);
        }
    }

// ---------------------------------------------------------------------
// AST CONSTRUCTION & VARIABLE UTILITIES
// ---------------------------------------------------------------------

    private Node variableNode(Token token) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("subscript", token.subscript());

        return new Node("VARIABLE", token.text(), null, attributes);
    }
  
    private Node parameterNode(Token token) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("subscript", token.subscript());

        return new Node("PARAM", token.text(), null, attributes);
    }
    
    private String formatVariableName(Token token) {
        return token.subscript() == null ? token.text() : token.text() + "_" + token.subscript();
    }

    private static double numericValue(Node node) {
        if (node.type().equals("NUMBER")) {
            return Double.parseDouble(node.value());
        }
        if (node.type().equals("NEGATE") && node.children().size() == 1) {
            return -numericValue(node.children().get(0));
        }
        if (node.type().equals("UNARY_PLUS") && node.children().size() == 1) {
            return numericValue(node.children().get(0));
        }
        throw new ParseException("Non numeric value entered");
    }

// ---------------------------------------------------------------------
// EXPRESSION CLASSIFICATION
// ---------------------------------------------------------------------

    private String classifyBody(Node node) {
        if (this.isCondition(node)) {
            return "CONDITION";
        }
        if (this.isSetValued(node)) {
            return "SET";
        }
        return "EXPRESSION";
    }

    private boolean isCondition(Node node) {
        String[] conditions = {"CONDITION", "AND", "OR", "NOT", "EQUALS", "NOT_EQUALS", "LESS_THAN", "GREATER_THAN", "LESS_EQUAL", "GREATER_EQUAL", "IN", "RELATION"};
        for (String condition : conditions) {
            if (node.type().equals(condition)) {
                return true;
            }
        }
        return false;
    }    

    private boolean isSetValued(Node node) {
        if (node == null) {
            return false;
        }

        if (node.type().equals("CONSTANT")) {
            return Set.of(
                "NATURALS",
                "INTEGERS",
                "REALS",
                "EMPTYSET"
            ).contains(node.value());
        }

        if (Set.of(
            "SET_LITERAL",
            "FINITE_SET_PATTERN",
            "INFINITE_SET_PATTERN",
            "COMPREHENSION"
        ).contains(node.type())) {
            return true;
        }

        if (node.type().equals("UNION") ||
            node.type().equals("INTERSECTION")) {

            return node.children() != null &&
                node.children().size() == 2 &&
                node.children().stream().allMatch(this::isSetValued);
        }

        if (node.type().equals("POWER")) {
            return node.children() != null &&
                !node.children().isEmpty() &&
                this.isSetValued(node.children().get(0));
        }

        return false;
    }

    private boolean canEndAtom(Node node) {
        return switch (node.type()) {
            case "NUMBER",
                "VARIABLE",
                "CONSTANT",
                "FUNCTION_CALL",
                "GROUP",
                "TUPLE",
                "SET_LITERAL",
                "FINITE_SET_PATTERN",
                "INFINITE_SET_PATTERN",
                "COMPREHENSION",
                "RESTRICTED_EXPRESSION",
                "FACTORIAL",
                "POWER",
                "ROOT",
                "DIVIDE",
                "NEGATE",
                "UNARY_PLUS",
                "INDEX",
                "RELATION",
                "EXPRESSION" -> true;
            default -> false;
        };
    }

    private boolean startsAtom(Token token) {
        return switch (token.type()) {
            case "NUMBER",
                 "CONSTANT",
                 "VARIABLE",
                 "SIN", "COS", "TAN",
                 "SEC", "CSC", "COT",
                 "HYPSIN", "HYPCOS", "HYPTAN",
                 "HYPSEC", "HYPCSC", "HYPCOT",
                 "LN", "LOG", "EXP",
                 "LEFT_PAREN",
                 "LEFT_BRACE",
                 "PLAIN_LEFT_BRACE",
                 "LEFT_ABS",
                 "FRAC",
                 "SQRT" -> true;
            default -> false;
        };
    }

// ---------------------------------------------------------------------
// TOKEN-SEQUENCE & DELIMITER UTILITIES
// ---------------------------------------------------------------------

    private static boolean isOpeningDelimeter(String type) {
        return switch (type) {
            case "LEFT_BRACE", "PLAIN_LEFT_BRACE",
                 "LEFT_PAREN", "LEFT_BRACKET",
                 "LEFT_ABS" -> true;
            default -> false;
        };
    }

    private static boolean isClosingDelimiter(String type) {
        return switch (type) {
            case "RIGHT_BRACE", "PLAIN_RIGHT_BRACE",
                 "RIGHT_PAREN", "RIGHT_BRACKET",
                 "RIGHT_ABS" -> true;
            default -> false;
        };
    }

    private static String matchingDelimiter(String type) {
        return switch (type) {
            case "LEFT_BRACE" -> "RIGHT_BRACE";
            case "PLAIN_LEFT_BRACE" -> "PLAIN_RIGHT_BRACE";
            case "LEFT_PAREN" -> "RIGHT_PAREN";
            case "LEFT_BRACKET" -> "RIGHT_BRACKET";
            case "LEFT_ABS" -> "RIGHT_ABS";
            default -> null;
        };
    }

    private List<Integer> findTopLevelPositions(List<Token> source, String wantedType) {
        List<Integer> positions = new ArrayList<>();
        List<String> delimiters = new ArrayList<>();

        for (int i = 0; i < source.size(); i++) {
            Token token = source.get(i);

            if (token == null) {
                throw new ParseException(
                    "Null token at position " + i
                );
            }

            String type = token.type();

            if (type.equals("END_OF_INPUT")) {
                break;
            }

            // Opening delimiter.
            if (isOpeningDelimeter(type)) {
                delimiters.add(matchingDelimiter(type));
                continue;
            }

            // Closing delimiter.
            if (isClosingDelimiter(type)) {
                if (delimiters.isEmpty()) {
                    throw new ParseException(
                        "Unexpected closing delimiter at token " + i
                    );
                }

                String expected = delimiters.remove(
                    delimiters.size() - 1
                );

                if (!expected.equals(type)) {
                    throw new ParseException(
                        "Mismatched delimiter at token " + i
                        + ": expected " + expected
                        + " but found " + type
                    );
                }

                continue;
            }

            // Only record tokens outside all delimiters.
            if (delimiters.isEmpty() && type.equals(wantedType)) {
                positions.add(i);
            }
        }

        if (!delimiters.isEmpty()) {
            throw new ParseException(
                "Unclosed delimiter in token sequence"
            );
        }

        return positions;
    }

    private int findTopLevelToken(String wantedType) {
        int end = this.tokens.size();

        if (end > 0 &&
            this.tokens.get(end - 1).type().equals("END_OF_INPUT")) {

            end--;
        }

        List<Token> source = this.tokens.subList(0, end);

        List<Integer> positions = this.findTopLevelPositions(
            source,
            wantedType
        );

        return positions.isEmpty() ? -1 : positions.get(0);
    }

    private int findTopLevelType(List<Token> source, String wantedType) {
        List<Integer> positions = this.findTopLevelPositions(
            source,
            wantedType
        );

        if (positions.isEmpty()) {
            throw new ParseException(
                "Expected top-level " + wantedType
            );
        }

        return positions.get(0);
    }

    private boolean containsTopLevelType(List<Token> source, String wantedType) {
        return !this.findTopLevelPositions(source, wantedType).isEmpty();
    }

    private List<List<Token>> splitTopLevel(List<Token> source, String wantedType) {
        List<List<Token>> result = new ArrayList<>();

        List<Integer> positions = this.findTopLevelPositions(
            source,
            wantedType
        );

        int start = 0;

        for (int position : positions) {
            result.add(
                new ArrayList<>(source.subList(start, position))
            );

            start = position + 1;
        }

        // Always include the final fragment.
        result.add(
            new ArrayList<>(source.subList(start, source.size()))
        );

        return result;
    }

 









    
}
