# Tokenizer

## 1. Purpose and Responsibilities

The tokenizer converts an input string into an ordered sequence of tokens for the parser.

Its responsibilities are to:

- Recognize valid numbers, variables, keywords, built-in functions, constants, operators, and delimiters.
- Apply consistent rules for matching reserved names and multi-character operators.
- Validate the internal format of numeric literals and variable subscripts.
- Report lexical errors when a token is malformed or an unrecognized character is encountered.
- Preserve token order and source-position information for subsequent stages.

The tokenizer does not determine the grammatical structure of an expression, resolve variable definitions, check mathematical dimensions, or evaluate expressions. These responsibilities belong to the parser, definition-validation stage, and evaluator.

## 2. Input and Output

### 2.1 Input

The tokenizer receives a string representing a mathematical expression, set, condition, or definition.

Whitespace is removed during preprocessing before tokenization begins.

The tokenizer scans the resulting string from left to right.

### 2.2 Output

The tokenizer produces an ordered list of token objects, followed by an `END_OF_INPUT` token.

If a lexical error occurs, tokenization terminates and reports the error.

## 3. Token Class Names

The following token classes are proposed for the initial implementation.

### 3.1 Literals and Names

| Token class | Purpose | Example |
|---|---|---|
| `NUMBER` | Numeric literal | `123`, `3.14159` |
| `VARIABLE` | Variable, optionally subscripted | `x`, `a_{12}` |
| `FUNCTION` | Built-in function name | `sin`, `floor` |
| `CONSTANT` | Built-in mathematical constant | `pi`, `e`, `infty` |
| `EMPTY_SET` | Empty-set symbol | `∅` |

### 3.2 Arithmetic and Relational Operators

| Token class | Symbols |
|---|---|
| `PLUS` | `+` |
| `MINUS` | `-` |
| `MULTIPLY` | `*` |
| `DIVIDE` | `/` |
| `POWER` | `^` |
| `FACTORIAL` | `!` |
| `EQUALS` | `=` |
| `NOT_EQUALS` | `!=` |
| `LESS_THAN` | `<` |
| `GREATER_THAN` | `>` |
| `LESS_EQUAL` | `<=` |
| `GREATER_EQUAL` | `>=` |
| `DEFINITION_ARROW` | `<-` |

### 3.3 Logical and Set Operators

| Token class | Symbol or keyword |
|---|---|
| `AND` | `and` |
| `OR` | `or` |
| `NOT` | `not` |
| `IN` | `in` |
| `UNION` | `cup` |
| `INTERSECTION` | `cap` |

### 3.4 Conditional Separator

| Token class | Symbol or keyword |
|---|---|
| `CONDITIONAL_SEPARATOR` | `if`, `:` |

Both `if` and `:` are normalized to the same token class because they are interchangeable in the language.

### 3.5 Delimiters

| Token class | Symbol |
|---|---|
| `LEFT_PAREN` | `(` |
| `RIGHT_PAREN` | `)` |
| `LEFT_BRACKET` | `[` |
| `RIGHT_BRACKET` | `]` |
| `LEFT_BRACE` | `{` |
| `RIGHT_BRACE` | `}` |
| `COMMA` | `,` |
| `ABSOLUTE_VALUE_DELIMITER` | `\|` |

The tokenizer emits individual delimiter tokens except for braces consumed as part of a variable's subscript.

### 3.6 End Marker

| Token class | Purpose |
|---|---|
| `END_OF_INPUT` | Marks the end of a successfully tokenized input |

## 4. Reserved Names

### 4.1 Built-in Functions

The following names are reserved built-in functions:

**Trigonometric functions**

- `sin`
- `cos`
- `tan`
- `csc`
- `sec`
- `cot`

**Hyperbolic functions**

- `sinh`
- `cosh`
- `tanh`
- `csch`
- `sech`
- `coth`

**Logarithmic functions**

- `ln`
- `log`

**Minimum and maximum**

- `min`
- `max`

**Rounding and magnitude functions**

- `floor`
- `ceiling`
- `round`
- `abs`

Reserved built-in names cannot be redefined by the user.

### 4.2 Built-in Constants

The following names are reserved constants:

- `pi`
- `e`
- `infty`

The negative-infinity notation `-infty` is tokenized as `MINUS` followed by `CONSTANT("infty")`.

### 4.3 Other Reserved Keywords

The following reserved keywords are recognized:

- `if`
- `and`
- `or`
- `not`
- `in`
- `cup`
- `cap`

These are emitted using their corresponding token classes.

## 5. High-Level Tokenization Procedure

The tokenizer uses a left-to-right scanning procedure.

At each position in the input, apply the following rules:

1. If a digit is encountered, scan and emit a complete numeric literal.
2. If a letter is encountered, recognize a reserved name if one begins at the current position. Otherwise, recognize a variable, including any valid subscript.
3. If an operator begins at the current position, recognize the longest matching operator and emit its corresponding token.
4. If the character is a recognized delimiter or special symbol, emit its corresponding token.
5. If no rule matches, report a lexical error.

After emitting a token, advance the scanning position past all characters consumed by that token and continue.

When the input has been consumed successfully, append `END_OF_INPUT`.

The order of recognition rules matters. Reserved names must take precedence over interpreting their initial letters as separate variables. For example, `sinn` is interpreted as `sin` followed by `n`, leaving the parser to interpret adjacency as implicit multiplication.

## 6. Numbers

A number is either an integer or a decimal literal.

An integer consists of one or more digits.

A decimal consists of one or more digits, followed by a decimal point, followed by one or more digits.

Examples of valid numeric literals:

- `0`
- `42`
- `12345678901234567890`
- `3.14159`
- `0.000001`

Examples of invalid numeric literals:

- `.5`
- `5.`
- `1.2.3`
- `1..2`
- `.`

There is no fixed maximum length for a number. The tokenizer consumes digits until the numeric literal ends.

The tokenizer preserves the original text of a number. Conversion to the numerical representation used by the evaluator occurs later.

A minus sign is a separate operator, not part of the numeric token. For example, `-12` produces `MINUS` followed by `NUMBER("12")`.

Scientific notation is not supported in the current numeric syntax.

## 7. Variables and Subscripts

A variable consists of a single ASCII letter, optionally followed by a subscript.

The permitted base letters are `a-z` and `A-Z`.

A subscript consists of:

1. An underscore `_`.
2. An opening brace `{`.
3. A nonempty sequence of ASCII letters and digits.
4. A closing brace `}`.

The permitted subscript characters are `0-9`, `a-z`, and `A-Z`.

Examples of valid variables:

- `x`
- `a_{12}`
- `B_{abc}`
- `T_{YEAR2024}`

Examples of malformed variables:

- `a_{}` — empty subscript.
- `a_12` — missing braces.
- `a_{12` — missing closing brace.
- `a_{x+y}` — illegal subscript character.
- `a_{SUBSCRIPT!}` — illegal subscript character.

The entire variable, including its subscript, is emitted as one `VARIABLE` token.

For example:

`a_{12}`

produces a token equivalent to:

`VARIABLE(value="a", subscript="12")`

The tokenizer validates the subscript format but does not determine whether the variable has been defined.

Adjacent variables remain separate tokens. For example, `ab` is tokenized as `VARIABLE("a")` followed by `VARIABLE("b")`. The parser determines whether their adjacency represents implicit multiplication.

## 8. Keyword and Operator Matching

### 8.1 Longest Matching

When multiple reserved names or operators begin at the current position, the tokenizer selects the longest matching token.

Examples:

- `sinh` is recognized as `FUNCTION("sinh")`, not `FUNCTION("sin")` followed by `VARIABLE("h")`.
- `<=` is recognized as one relational token.
- `!=` is recognized as one relational token.
- `<-` is recognized as one definition token.

Matching only considers tokens that begin at the current scanning position.

### 8.2 Look-Ahead

The tokenizer may inspect multiple characters ahead to determine which token begins at the current position.

A look-ahead of five characters is sufficient for the current reserved function names. However, the implementation should not impose this fixed limit on all token types.

Instead:

- Reserved names are matched against the remaining input.
- Operators are checked in descending order of length.
- Numbers are scanned until the numeric literal ends.
- Variables are scanned according to the base-letter and subscript rules.

This permits arbitrarily long numeric literals without increasing the look-ahead limit.

## 9. Lexical Errors

The tokenizer reports lexical errors when it encounters malformed tokens or unrecognized characters.

### 9.1 Malformed Numbers

Examples include:

- `1.234.34`
- `1..2`
- `.5`
- `5.`

The tokenizer reports the position of the offending character whenever possible.

### 9.2 Malformed Variables and Subscripts

Examples include:

- `a_{}` — empty subscript.
- `a_12` — missing braces.
- `a_{12` — unterminated subscript.
- `a_{x+1}` — illegal subscript character.
- `a_{SUBSCRIPT!}` — illegal subscript character.

Malformed subscripts must not silently fall back to a sequence of unrelated tokens.

### 9.3 Unrecognized Characters

Any character that cannot begin a recognized token produces a lexical error. Invalid characters must not be silently discarded.

### 9.4 Incomplete Operators

A character that is only valid as part of a multi-character operator must not be accepted independently.

For example, if `!` is recognized only as the factorial operator or as part of `!=`, its interpretation depends on the actual tokenization rule. Since `!` is the factorial symbol in this language, a standalone `!` is a valid `FACTORIAL` token; the parser determines whether it appears in a valid position.

A sequence such as `=<` may tokenize as `EQUALS` followed by `LESS_THAN`. The parser determines whether the resulting arrangement is syntactically valid.

### 9.5 Errors Handled by Later Stages

The tokenizer does not reject the following solely on the basis of their overall structure:

- `f(x)+` — incomplete expression.
- `f(x,y(` — malformed grammatical structure.
- `1/0` — division by zero during evaluation.
- A reference to an undefined variable or function.
- An invalid function argument count.
- Incompatible mathematical dimensions.

These are handled by the parser, definition-validation stage, or evaluator.

## 10. Token Object Format

Every token should contain:

- `type`: the token class.
- `value`: the token's value or original text.
- `start`: the starting source position.
- `end`: the ending source position.

Variable tokens additionally contain:

- `subscript`: the subscript string, or `None` if absent.

Example token representations:

`NUMBER(value="123.45", start=0, end=6)`

`VARIABLE(value="a", subscript="12", start=0, end=6)`

`FUNCTION(value="sin", start=0, end=3)`

`LESS_EQUAL(value="<=", start=0, end=2)`

The exact implementation language and class syntax may vary, but the information represented by each field should remain consistent.

Token positions should refer to a clearly defined source representation. If whitespace is removed before tokenization, positions will refer to the whitespace-stripped string unless the preprocessing stage preserves a mapping back to the original input.

Tokens must be stored in an ordered list, not a set. The final token in every successfully tokenized input is `END_OF_INPUT`.

## 11. Boundary Between Tokenization and Parsing

The tokenizer recognizes valid individual tokens. The parser determines how those tokens fit together.

Examples:

| Input | Tokenizer responsibility | Parser responsibility |
|---|---|---|
| `(x,y)` | Emit delimiters, variables, and comma | Recognize a tuple |
| `(x)` | Emit delimiters and variable | Recognize grouping |
| `f(x,y)` | Emit name, delimiters, arguments, and comma | Recognize a function call |
| `t[i]` | Emit name and brackets | Recognize indexing |
| `2x` | Emit number followed by variable | Recognize implicit multiplication |
| `x if x>0` | Emit expression tokens and conditional separator | Recognize a conditional expression |
| `{x in Z : x>0}` | Emit braces, names, operators, and separator | Recognize a set comprehension |
| `|x|` | Emit absolute-value delimiters and the variable | Recognize an absolute-value expression |

The tokenizer does not insert multiplication operators between adjacent tokens. It also does not decide whether parentheses represent a tuple, a grouped expression, or a function call.

## 12. Implementation Principle

The tokenizer should be implemented as a single scanning loop with specialized recognition procedures for numbers, variables, reserved names, and operators.

Each recognition procedure must either:

1. Return a valid token and the position immediately after it; or
2. Report a lexical error with a useful source position.

This design keeps the tokenizer deterministic, makes lexical errors easier to diagnose, and allows new keywords or operators to be added without changing the fundamental scanning procedure.
