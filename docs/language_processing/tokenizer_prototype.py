"""
Tokenizer Prototype

Converts preprocessed mathematical expressions into a list of tokens.

Usage:
    tokens = tokenize(r"sin(x) + 2")

Returns a list of strings, ending with "END_OF_INPUT".
Raises ValueError for invalid lexical input.

This is a prototype. It recognizes tokens but does not parse or evaluate
expressions. See the language specification for the complete syntax.
"""

FUNCTIONS = {"sin", "cos", "tan",
             "sinh", "cosh", "tanh",
             "csc", "sec", "cot",
             "csch", "sech", "coth",
             "ln", "log",
             "max", "min",
             "floor", "ceiling", "round", "abs"
             }

CONSTANTS = {"pi", "e", "infty", "empty", "Z", "N", "R"}

KEYWORDS = {"and" : "AND",
             "or" : "OR",
             "not" : "NOT",
             "in" : "IN",
             "cup" : "UNION",
             "cap" : "INTERSECTION",
             "if" : "CONDITIONAL_SEPARATOR"
            }

OPERATORS = {"<=" : "LESS_EQUAL",
             ">=" : "GREATER_EQUAL",
             "!=" : "NOT_EQUALS",
             "<-" : "DEFINITION",
             "+" : "PLUS",
             "-" : "MINUS",
             "*" : "MULTIPLY",
             "/" : "DIVIDE",
             "^" : "POWER",
             "!" : "FACTORIAL",
             "=" : "EQUALS",
             "<" : "LESS_THAN",
             ">" : "GREATER_THAN",
            }

PUNCTUATION = {"[" : "PLAIN_LEFT_BRACKET",
               "]" : "PLAIN_RIGHT_BRACKET",
               "{" : "PLAIN_LEFT_BRACE",
               "}" : "PLAIN_RIGHT_BRACE",
               "," : "COMMA",
               ":" : "CONDITIONAL_SEPARATOR"
               }

STRUCTURAL_COMMANDS = {r"\frac" : "FRAC",
                       r"\sqrt" : "SQRT",
                       r"\left(" : "LEFT_PAREN",
                       r"\right)" : "RIGHT_PAREN",
                       r"\left[" : "LEFT_BRACKET",
                       r"\right]" : "RIGHT_BRACKET",
                       r"\left\{" : "LEFT_BRACE",
                       r"\right\}" : "RIGHT_BRACE",
                       r"\right|" : "RIGHT_ABS",
                       r"\left|" : "LEFT_ABS"
                       }

def tokenize(source: str) -> list[str]:
    tokens = []
    i = 0
    n = len(source)

    commands = sorted(STRUCTURAL_COMMANDS, key=len, reverse=True)
    operators = sorted(OPERATORS, key=len, reverse=True)
    reserved_words = sorted(FUNCTIONS | CONSTANTS | set(KEYWORDS), key=len, reverse=True)

    while i < n:
        # Skip blank spaces
        if source[i].isspace():
            i += 1
            continue

        # Check for structural command match
        matched = False
        for command in commands:
            if source.startswith(command, i):
                tokens.append(f"{STRUCTURAL_COMMANDS[command]}:{command}")
                i += len(command)
                matched = True
                break
        if matched:
                continue

        # If digit found, tokenize integers and decimals with digits on both sides
        if source[i].isdigit():
            start = i

            while i < n and source[i].isdigit():
                i += 1

            if i < n and source[i] == ".":
                i += 1

                if i >= n or not source[i].isdigit():
                    raise ValueError(f"Invalid number at position {start}: "
                                     "a decimal point must be followed by digits"
                                     )

                while i < n and source[i].isdigit():
                    i += 1

            if i < n and source[i] == ".":
                raise ValueError(f"Invalid number at position {start}: "
                                 "multiple decimal points"
                                 )

            tokens.append(f"NUMBER:{source[start:i]}")
            continue

        # If letter is found, check if starts a reserved word. Otherwise, tokenize variable if valid
        if source[i].isascii() and source[i].isalpha():
            matched_word = None

            # Check if each letter starts a reserved word. If so, tokenize it
            for word in reserved_words:
                if source.startswith(word, i):
                    matched_word = word
                    break

            if matched_word is not None:
                if matched_word in FUNCTIONS:
                    token_type = "FUNCTION"
                elif matched_word in CONSTANTS:
                    token_type = "CONSTANT"
                else:
                    token_type = KEYWORDS[matched_word]

                tokens.append(F"{token_type}:{matched_word}")
                i += len(matched_word)
                continue

            # If not reserved, assume it is a variable
            variable = source[i]
            i += 1

            if i < n and source[i] == "_":
                if i + 1 >= n or source[i + 1] != "{":
                    raise ValueError(f"Invalid subscript after variable {variable!r}"
                                     f"at position {i}"
                                     )
                closing = source.find("}", i + 2)

                if closing == -1:
                    raise ValueError(f"Unterminated subscript for variable {variable!r}")

                subscript = source[i + 1 : closing]

                if not subscript or not subscript.isascii() or not subscript.isalnum():
                    raise ValueError(f"Invalid subscript {subscript!r} "
                                     f"for variable {variable!r}")

                variable += source[i : closing + 1]
                i = closing + 1

            tokens.append(f"VARIABLE:{variable}")
            continue

        # If operator is found, tokenize it
        matched_operator = None
        for operator in operators:
            if source.startswith(operator, i):
                matched_operator = operator
                break

        if matched_operator is not None:
            tokens.append(f"{OPERATORS[matched_operator]}:{matched_operator}")
            i += len(matched_operator)
            continue

        char = source[i]
        if char in PUNCTUATION:
            tokens.append(f"{PUNCTUATION[char]}:{char}")
            i += 1
            continue

        raise ValueError(f"Unexpected character {char!r} at position {i}")

    tokens.append("END_OF_INPUT")
    return tokens

