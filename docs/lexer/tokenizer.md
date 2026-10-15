# Tokenizer Specification

## 1. Purpose and Scope

The tokenizer converts the preprocessed input into an ordered sequence of tokens for the parser.

The input to the tokenizer is **not the raw text entered by the user**. A preprocessing step first transforms interface-level input into a representation suitable for tokenization. For example, the user can create fractions, square roots, subscripts, exponents, and delimiters through the interface without directly entering the corresponding internal commands.

The tokenizer is responsible for:
- Recognizing tokens and assigning token classes.
- Validating the lexical structure of individual tokens.
- Preserving token values and source positions.
- Reporting lexical errors.

The tokenizer is not responsible for:
- Determining whether a sequence of tokens forms a valid expression, set, or condition.
- Resolving whether `=` represents equality or a definition.
- Determining whether parentheses represent grouping, tuples, or function arguments.
- Evaluating expressions or checking mathematical validity.

## 2. Token Representation

Each token has the following fields:

| Field | Description |
|---|---|
| `type` | The token's class. |
| `value` | The token's textual value or normalized representation. |
| `start` | The starting position in the preprocessed input. |
| `end` | The exclusive ending position in the preprocessed input. |
| `subscript` | The subscript text for a `VARIABLE` token, or `None` if absent. |

The `subscript` field is only used for `VARIABLE` tokens.

Token positions use a zero-based indexing convention, with `end` pointing immediately after the token.

The tokenizer returns an **ordered list** of tokens. A successful tokenization ends with an `END_OF_INPUT` token.

Numeric literals should retain their original textual representation rather than being converted to numeric values during tokenization.

## 3. Token Classes

### 3.1 Literals and Variables

| Token class | Recognized input | Description |
|---|---|---|
| `NUMBER` | Integer or decimal literal | A numeric literal. |
| `VARIABLE` | A single ASCII letter, optionally with a subscript | A variable, function name, or constant name defined by the user. |
| `CONSTANT` | Reserved built-in constant | A predefined mathematical constant. |
| `EMPTY_SET` | `empty` | The empty set. |

### 3.2 Built-in Functions

| Token class | Recognized input |
|---|---|
| `FUNCTION` | `sin`, `cos`, `tan` |
| `FUNCTION` | `sinh`, `cosh`, `tanh` |
| `FUNCTION` | `csc`, `sec`, `cot` |
| `FUNCTION` | `csch`, `sech`, `coth` |
| `FUNCTION` | `ln`, `log` |
| `FUNCTION` | `max`, `min` |
| `FUNCTION` | `floor`, `ceiling`, `round`, `abs` |

All built-in function names are reserved and cannot be redefined by the user.

The tokenizer recognizes the function name; the parser and later stages handle its arguments and semantics.

### 3.3 Built-in Constants

| Token class | Recognized input |
|---|---|
| `CONSTANT` | `pi` |
| `CONSTANT` | `e` |
| `CONSTANT` | `infty` |

The input `-infty` is tokenized as `MINUS` followed by `CONSTANT`, not as a single constant.

### 3.4 Arithmetic Operators

| Token class | Recognized input |
|---|---|
| `PLUS` | `+` |
| `MINUS` | `-` |
| `MULTIPLY` | `*` |
| `DIVIDE` | `/` |
| `POWER` | `^` |
| `FACTORIAL` | `!` |

The tokenizer recognizes `!` as factorial unless it forms part of the longer operator `!=`.

### 3.5 Relations and Definition

| Token class | Recognized input |
|---|---|
| `EQUALS` | `=` |
| `NOT_EQUALS` | `!=` |
| `LESS_THAN` | `<` |
| `GREATER_THAN` | `>` |
| `LESS_EQUAL` | `<=` |
| `GREATER_EQUAL` | `>=` |
| `DEFINITION_ARROW` | `<-` |

The tokenizer does not distinguish between equality and definition uses of `=`. Both produce `EQUALS`; the parser determines its meaning from context.

### 3.6 Logical Operators

| Token class | Recognized input |
|---|---|
| `AND` | `and` |
| `OR` | `or` |
| `NOT` | `not` |

### 3.7 Set Operators

| Token class | Recognized input |
|---|---|
| `IN` | `in` |
| `UNION` | `cup` |
| `INTERSECTION` | `cap` |

### 3.8 Conditional Separator

| Token class | Recognized input |
|---|---|
| `CONDITIONAL_SEPARATOR` | `if` |
| `CONDITIONAL_SEPARATOR` | `:` |

Both inputs produce the same token class.

### 3.9 Structural Commands

The preprocessing step may produce structural commands that were not typed literally by the user. The tokenizer recognizes these commands as individual tokens.

| Token class | Preprocessed representation | Description |
|---|---|---|
| `FRAC` | `\frac` | Begins a fraction expression. |
| `SQRT` | `\sqrt` | Begins a square-root expression. |
| `SUBSCRIPT` | `_` | Introduces a general subscript operation when not consumed as part of a variable token. |
| `POWER` | `^` | Introduces an exponent. |

`POWER` is shared with the arithmetic operators section; it is not a separate token class for structural use.

The tokenizer does not parse the arguments of `FRAC` or `SQRT`. It emits the command token followed by the tokens for the associated arguments and delimiters. The parser validates their structure.

A variable with a valid subscript, such as `a_{12}`, is still represented by a single `VARIABLE` token. The `SUBSCRIPT` token is for an independently represented subscript operation, not for splitting a variable token.

### 3.10 Delimiters

| Token class | Preprocessed representation |
|---|---|
| `LEFT_PAREN` | Opening parenthesis representation |
| `RIGHT_PAREN` | Closing parenthesis representation |
| `LEFT_BRACKET` | Opening square-bracket representation |
| `RIGHT_BRACKET` | Closing square-bracket representation |
| `LEFT_BRACE` | Opening brace representation |
| `RIGHT_BRACE` | Closing brace representation |
| `COMMA` | `,` |
| `ABSOLUTE_VALUE_DELIMITER` | `|` |

The tokenizer recognizes the complete preprocessed representation of each delimiter. If preprocessing produces LaTeX-style commands, the tokenizer matches each complete command rather than tokenizing its backslashes and letters separately.

Absolute-value delimiters are emitted individually. The parser determines which delimiters form a pair.

The tokenizer does not decide whether braces delimit a set, a piecewise expression, or a structural command's argument. That is determined by the parser.

### 3.11 End of Input

| Token class | Recognized input |
|---|---|
| `END_OF_INPUT` | End of the preprocessed input |

This token is appended after successful tokenization.

## 4. Variable Syntax

A variable consists of exactly one ASCII letter, optionally followed by a subscript.

The subscript syntax is:
- An underscore `_`.
- An opening brace `{`.
- A nonempty sequence of ASCII letters and digits.
- A closing brace `}`.

Examples of valid variables:
- `x`
- `a_{1}`
- `B_{12}`
- `T_{YEAR2024}`
- `c_{abc}`

Examples that are not single valid variables:
- `ab`: two adjacent variables, `a` and `b`.
- `a1`: a variable followed by a number.
- `a_1`: invalid subscript syntax.
- `a_{}`: empty subscript.
- `a_{x+1}`: invalid subscript contents.
- `a_{12`: unterminated subscript.

The tokenizer validates the syntax of the subscript and consumes it as part of the `VARIABLE` token. It does not interpret the subscript as an expression.

For example, `a_{12}` produces one token:

`VARIABLE(value="a", subscript="12", ...)`

## 5. Numeric Literal Syntax

A numeric literal is either an integer or a decimal.

An integer consists of one or more digits.

A decimal consists of one or more digits, followed by a decimal point, followed by one or more digits.

Examples of valid numeric literals:
- `0`
- `42`
- `123456789`
- `0.5`
- `123.456`

Examples of invalid numeric literals:
- `.5`
- `5.`
- `1.234.34`

There is no fixed limit on the number of digits in a numeric literal.

When scanning a number, the tokenizer consumes the complete numeric sequence and validates it. A second decimal point immediately following the same numeric sequence is a lexical error.

## 6. Reserved Words and Longest Matching

Built-in function names, built-in constants, logical operators, set operators, conditional separators, and `empty` are reserved.

When scanning a letter, the tokenizer first checks for a reserved name at the current position. It chooses the longest matching reserved name, if multiple names match, before falling back to variable recognition.

Examples:
- `sin` produces `FUNCTION("sin")`.
- `sinh` produces `FUNCTION("sinh")`, not `FUNCTION("sin")` followed by a variable.
- `sinn` produces `FUNCTION("sin")` followed by `VARIABLE("n")`.
- `empty` produces `EMPTY_SET`.

Adjacent tokens do not necessarily require whitespace. For example, adjacent variables can represent implicit multiplication at the parser level.

## 7. Tokenization Algorithm

The tokenizer scans the preprocessed input from left to right.

At each position:

1. **Number:** If the next character is a digit, scan the complete numeric literal and validate its syntax.
2. **Reserved name or variable:** If the next character is a letter, recognize the longest matching reserved name. If no reserved name matches, scan one variable letter and any valid attached subscript.
3. **Multi-character operator:** Recognize multi-character operators before their shorter prefixes. These include `<=`, `>=`, `!=`, and `<-`.
4. **Single-character operator:** Recognize arithmetic operators and other single-character operators.
5. **Structural command:** Recognize structural commands such as `\frac` and `\sqrt`.
6. **Delimiter:** Recognize the complete preprocessed representation of a delimiter.
7. **Unknown input:** If no rule matches, report a lexical error.

After scanning the entire input successfully, append `END_OF_INPUT`.

The tokenizer must not use a fixed lookahead limit for numeric literals. It scans a number until the numeric sequence ends, then validates the complete sequence.

## 8. Error Handling

Lexical errors include:
- Invalid numeric literals.
- Invalid variable subscripts.
- Unterminated variable subscripts.
- Unknown characters or structural commands.
- Malformed preprocessed delimiter representations.

When a lexical error occurs, the tokenizer should report the error and its position in the preprocessed input. It should not silently discard invalid characters or produce a successful token sequence.

Syntactic errors are the parser's responsibility. For example, `f(x)+` may tokenize successfully but fail to parse.

Mathematical evaluation errors, such as division by zero, belong to a later stage.

## 9. Examples

### Example A: Function call

Input:

`sin(x)`

Tokens:

1. `FUNCTION("sin")`
2. `LEFT_PAREN`
3. `VARIABLE("x")`
4. `RIGHT_PAREN`
5. `END_OF_INPUT`

### Example B: Subscripted variable

Input:

`a_{12}+b`

Tokens:

1. `VARIABLE(value="a", subscript="12")`
2. `PLUS`
3. `VARIABLE(value="b", subscript=None)`
4. `END_OF_INPUT`

### Example C: Negative infinity

Input:

`-infty`

Tokens:

1. `MINUS`
2. `CONSTANT("infty")`
3. `END_OF_INPUT`

### Example D: Fraction

Preprocessed input:

`\frac{a}{b}`

Conceptual token sequence:

1. `FRAC`
2. `LEFT_BRACE`
3. `VARIABLE("a")`
4. `RIGHT_BRACE`
5. `LEFT_BRACE`
6. `VARIABLE("b")`
7. `RIGHT_BRACE`
8. `END_OF_INPUT`

The parser determines that the braces following `FRAC` delimit its two arguments.

### Example E: Factorial and inequality

Input:

`x! != y`

Tokens:

1. `VARIABLE("x")`
2. `FACTORIAL`
3. `NOT_EQUALS`
4. `VARIABLE("y")`
5. `END_OF_INPUT`

The multi-character operator `!=` takes precedence over `!`.

## 10. Design Principle

The tokenizer recognizes lexical units, not complete mathematical constructs.

In particular:
- A subscript attached to a variable belongs to its `VARIABLE` token.
- A structural command such as `FRAC` or `SQRT` is its own token.
- Delimiters are emitted individually; the parser determines their roles.
- Built-in names are recognized as reserved tokens.
- The parser determines the grammatical meaning of token sequences.
- Later stages perform evaluation and rendering.

This division keeps tokenization predictable and allows the grammar and rendering rules to evolve independently.
