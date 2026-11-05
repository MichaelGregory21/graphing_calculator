"""Recursive-descent parser for the math-language tokenizer output.

The tokenizer is expected to return strings such as ``VARIABLE:x`` and
finish with ``END_OF_INPUT``.  Structural braces (``LEFT_BRACE`` /
``RIGHT_BRACE``) are user-authored ``\\left\\{`` / ``\\right\\}`` and
introduce brace constructs.  Plain braces (``PLAIN_LEFT_BRACE`` /
``PLAIN_RIGHT_BRACE``) are tokenizer-generated grouping delimiters used by
functions, fractions, square roots, and powers.

Both ':' and 'if' should be tokenized as CONDITIONAL_SEPARATOR.  Their text
is intentionally ignored by the parser: the surrounding grammar determines
the separator's role.
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from typing import Any, Iterable


@dataclass
class Node:
    kind: str
    value: Any = None
    children: list["Node"] = field(default_factory=list)
    attrs: dict[str, Any] = field(default_factory=dict)

    def to_dict(self) -> dict[str, Any]:
        return {
            "kind": self.kind,
            "value": self.value,
            "attrs": self.attrs,
            "children": [child.to_dict() for child in self.children],
        }

    def pretty(self, indent: int = 0) -> str:
        """Return a semantic, consistently capitalized tree representation."""
        lines: list[str] = []
        self._pretty_into(lines, indent)
        return "\n".join(lines)

    def _pretty_into(self, lines: list[str], indent: int,
                     category_override: str | None = None,
                     name_override: str | None = None) -> None:
        pad = " " * indent

        # Definitions carry their body category in a TYPE wrapper. Display
        # the definition's name first, then its parameters, then the body.
        if self.kind == "DEFINITION":
            name = self.children[0].value if self.children else self.value
            lines.append(f"{pad}DEFINITION: {name}")
            for child in self.children:
                if child.kind == "FREE_VARIABLES":
                    variables = child.value or []
                    lines.append(" " * (indent + 4) +
                                 "FREE_VARIABLES: " + ", ".join(map(str, variables)))
                elif child.kind == "TYPE" and child.children:
                    child.children[0]._pretty_into(
                        lines, indent + 4, category_override=str(child.value))
            return

        category = category_override or self._pretty_category()
        name = name_override if name_override is not None else self._pretty_name()
        lines.append(f"{pad}{category}: {name}")
        node_type = "None" if category == "CONDITION" else self.kind
        lines.append(" " * (indent + 4) + f"TYPE: {node_type}")

        if self.kind == "COMPREHENSION":
            bound = self.attrs.get("bound_variables", [])
            lines.append(" " * (indent + 4) +
                         "BOUND_VARIABLES: " + ", ".join(map(str, bound)))
            if self.children:
                lines.append(" " * (indent + 4) + "SOURCE_SET:")
                self.children[0]._pretty_into(lines, indent + 8)
            if len(self.children) > 1:
                lines.append(" " * (indent + 4) + "CONDITION:")
                self.children[1]._pretty_into(lines, indent + 8,
                                              category_override="CONDITION")
            return

        if self.kind == "SET_LITERAL" and self.value == "empty":
            return

        # Keep metadata that has semantic value, but avoid printing internal
        # parser flags such as finite=True as if they were part of the AST.
        for key, value in self.attrs.items():
            if key in {"subscript", "bound_variables"} or key.startswith("_ "):
                continue
            if key in {"finite"}:
                continue
            if key == "free_variables":
                lines.append(" " * (indent + 4) +
                             "FREE_VARIABLES: " + ", ".join(map(str, value)))
            elif key in {"pattern", "first", "step", "last", "inferred"}:
                lines.append(" " * (indent + 4) + f"{key.upper()}: {value}")

        for child in self.children:
            child._pretty_into(lines, indent + 4)

    def _pretty_category(self) -> str:
        if self.kind in {"CONDITION", "AND", "OR", "NOT", "EQUALS", "NOT_EQUALS",
                         "LESS_THAN", "GREATER_THAN", "LESS_EQUAL", "GREATER_EQUAL",
                         "IN", "RELATION_APPLICATION"}:
            return "CONDITION"
        if self.kind in {"SET", "SET_LITERAL", "FINITE_SET_PATTERN",
                         "INFINITE_SET_PATTERN", "COMPREHENSION", "UNION",
                         "INTERSECTION"}:
            return "SET"
        if self.kind == "BUILTIN" and str(self.value) in {
                "N", "Z", "Q", "R", "empty", r"\emptyset", "∅"}:
            return "SET"
        if self.kind in {"SUBTRACT", "MULTIPLY"} and len(self.children) == 2:
            if all(child._pretty_category() == "SET" for child in self.children):
                return "SET"
        if self.kind == "POWER" and self.children and self.children[0]._pretty_category() == "SET":
            return "SET"
        return "EXPRESSION"

    def _pretty_name(self) -> str:
        if self.kind == "BUILTIN":
            names = {"N": "naturals", "Z": "integers", "Q": "rationals",
                     "R": "reals", "empty": "empty", r"\emptyset": "empty", "∅": "empty"}
            return names.get(str(self.value), str(self.value))
        if self.kind == "SET_LITERAL" and self.value == "empty":
            return "empty"
        if self.kind == "VARIABLE":
            subscript = self.attrs.get("subscript")
            return str(self.value) if subscript is None else f"{self.value}_{{{subscript}}}"
        if self.value is not None:
            return str(self.value)
        return "None"


@dataclass(frozen=True)
class Token:
    type: str
    text: str
    subscript: str | None = None


def split_token(raw: str | Token) -> Token:
    """Convert the tokenizer's ``TYPE:text`` representation to a Token."""
    if isinstance(raw, Token):
        return raw
    if raw == "END_OF_INPUT":
        return Token("END_OF_INPUT", "")
    if ":" not in raw:
        raise ValueError(f"Malformed token {raw!r}; expected 'TYPE:text'")

    token_type, text = raw.split(":", 1)
    if token_type == "VARIABLE":
        match = re.fullmatch(r"([A-Za-z])(?:_\{([A-Za-z0-9]+)\})?", text)
        if match is None:
            raise ValueError(f"Malformed variable token: {text!r}")
        return Token("VARIABLE", text, match.group(2))

    # Normalize separator spellings in case the tokenizer uses distinct
    # token types for ':' and 'if'.
    if (token_type in ("COLON", "IF") or
            (token_type == "CONDITIONAL_SEPARATOR" and text in (":", "if"))):
        return Token("CONDITIONAL_SEPARATOR", text)
    if token_type in {"ELLIPSIS", "DOTS", "THREE_DOTS", "CONTINUATION"} or text == "...":
        return Token("ELLIPSIS", "...")
    return Token(token_type, text)


class Parser:
    PRECEDENCE = {
        "OR": 10,
        "AND": 20,
        "EQUALS": 30,
        "NOT_EQUALS": 30,
        "LESS_THAN": 30,
        "GREATER_THAN": 30,
        "LESS_EQUAL": 30,
        "GREATER_EQUAL": 30,
        "IN": 30,
        "UNION": 35,
        "INTERSECTION": 35,
        "PLUS": 40,
        "MINUS": 40,
        "MULTIPLY": 50,
        "DIVIDE": 50,
        "IMPLICIT_MULTIPLY": 50,
        "POWER": 60,
    }

    OPEN_TO_CLOSE = {
        "LEFT_BRACE": "RIGHT_BRACE",              # user structural braces
        "PLAIN_LEFT_BRACE": "PLAIN_RIGHT_BRACE",  # internal grouping braces
        "LEFT_PAREN": "RIGHT_PAREN",
        "LEFT_BRACKET": "RIGHT_BRACKET",
        "LEFT_ABS": "RIGHT_ABS",
    }
    CLOSE_TYPES = frozenset(OPEN_TO_CLOSE.values())

    RELATION_KINDS = {"relation", "binary_relation", "user_relation"}
    CONDITION_KINDS = {
        "CONDITION", "AND", "OR", "NOT", "EQUALS", "NOT_EQUALS",
        "LESS_THAN", "GREATER_THAN", "LESS_EQUAL", "GREATER_EQUAL",
        "IN", "RELATION_APPLICATION",
    }

    def __init__(self, raw_tokens: Iterable[str | Token], symbol_table=None):
        self.tokens = [split_token(t) for t in raw_tokens]
        # Keep exactly one end marker at the end.
        self.tokens = [t for t in self.tokens if t.type != "END_OF_INPUT"]
        self.tokens.append(Token("END_OF_INPUT", ""))
        self.i = 0
        self.symbol_table = symbol_table if symbol_table is not None else {}

    # ----- token navigation -------------------------------------------------

    def current(self) -> Token:
        return self.tokens[min(self.i, len(self.tokens) - 1)]

    def peek(self, offset: int = 1) -> Token:
        return self.tokens[min(self.i + offset, len(self.tokens) - 1)]

    def advance(self) -> Token:
        tok = self.current()
        if tok.type != "END_OF_INPUT":
            self.i += 1
        return tok

    def accept(self, *types: str) -> Token | None:
        return self.advance() if self.current().type in types else None

    def expect(self, *types: str) -> Token:
        tok = self.accept(*types)
        if tok is None:
            expected = " or ".join(types)
            got = self.current()
            raise SyntaxError(
                f"Expected {expected}, got {got.type}:{got.text!r} "
                f"at token index {self.i}"
            )
        return tok

    def at_end(self) -> bool:
        return self.current().type == "END_OF_INPUT"

    @staticmethod
    def is_separator(tok: Token) -> bool:
        return tok.type == "CONDITIONAL_SEPARATOR"

    # ----- public entry point and definitions ------------------------------

    def parse(self) -> Node:
        saved = self.i
        lhs = None

        if self.current().type == "VARIABLE":
            try:
                lhs = self.parse_lhs_candidate()
            except SyntaxError:
                lhs = None

        if lhs is not None and self.current().type in ("EQUALS", "DEFINITION"):
            op = self.advance()
            body = self.parse_value()
            self.expect("END_OF_INPUT")
            name, params = self.unpack_lhs(lhs)
            body_type = self.classify(body)

            if op.type == "EQUALS" and body_type == "CONDITION":
                raise SyntaxError("A condition definition must use '<-', not '='")
            return Node(
                "DEFINITION", op.text,
                [Node("NAME", name),
                 Node("FREE_VARIABLES", params if params else None),
                 Node("TYPE", body_type, [body])],
                {"definition_operator": op.text},
            )

        self.i = saved
        result = self.parse_value()
        self.expect("END_OF_INPUT")
        return result

    def parse_lhs_candidate(self) -> Node:
        name = self.expect("VARIABLE")
        params: list[Node] = []
        if self.accept("LEFT_PAREN"):
            if self.current().type != "RIGHT_PAREN":
                params.append(Node("PARAM", self.expect("VARIABLE").text))
                while self.accept("COMMA"):
                    params.append(Node("PARAM", self.expect("VARIABLE").text))
            self.expect("RIGHT_PAREN")
        return Node("LHS", name.text, params)

    @staticmethod
    def unpack_lhs(lhs: Node) -> tuple[str, list[str]]:
        return lhs.value, [child.value for child in lhs.children]

    def parse_value(self) -> Node:
        return self.parse_expr(0)

    # ----- Pratt expression parser -----------------------------------------

    def parse_expr(self, min_prec: int = 0, *, allow_separator: bool = True) -> Node:
        left = self.parse_prefix()

        while True:
            tok = self.current()

            # Separator handling is deliberately independent of whether its
            # original spelling was ':' or 'if'. At a nonzero precedence
            # level, the enclosing construct owns the separator.
            if self.is_separator(tok):
                if not allow_separator or min_prec > 0:
                    break
                self.advance()
                condition = self.parse_condition()
                left = Node("RESTRICTED_EXPRESSION", children=[left, condition])
                continue

            if tok.type == "FACTORIAL":
                if 70 < min_prec:
                    break
                self.advance()
                left = Node("FACTORIAL", children=[left])
                continue

            # User-defined binary relation application, e.g. x L y.
            if (tok.type == "VARIABLE" and tok.text in self.symbol_table
                    and self.can_end_atom(left) and self.starts_atom(self.peek())):
                metadata = self.symbol_table[tok.text]
                kind = (metadata.get("kind", metadata.get("type", "relation"))
                        if isinstance(metadata, dict) else "relation")
                if str(kind).lower() in self.RELATION_KINDS:
                    self.advance()
                    right = self.parse_expr(31, allow_separator=False)
                    left = Node("RELATION_APPLICATION", tok.text, [left, right])
                    continue

            implicit = self.starts_atom(tok) and self.can_end_atom(left)
            op_type = "IMPLICIT_MULTIPLY" if implicit else tok.type
            prec = self.PRECEDENCE.get(op_type)
            if prec is None or prec < min_prec:
                break

            if implicit:
                right = self.parse_expr(prec + 1, allow_separator=False)
                left = Node("MULTIPLY", "implicit", [left, right])
                continue

            # Exponents must be braced: x^{...}. Both structural and plain
            # braces can occur in tokenizer output, but are matched exactly.
            if tok.type == "POWER":
                self.advance()
                if self.accept("PLAIN_LEFT_BRACE"):
                    close_type = "PLAIN_RIGHT_BRACE"
                elif self.accept("LEFT_BRACE"):
                    close_type = "RIGHT_BRACE"
                else:
                    raise SyntaxError("Expected an opening brace after '^'")
                if self.current().type == close_type:
                    raise SyntaxError("Exponent cannot be empty")
                exponent = self.parse_expr(0, allow_separator=False)
                self.expect(close_type)
                left = Node("POWER", "^", [left, exponent])
                continue

            self.advance()
            right = self.parse_expr(prec + 1, allow_separator=False)
            left = Node(self.operator_name(op_type), tok.text, [left, right])

        return left

    def parse_condition(self) -> Node:
        condition = self.parse_expr(0, allow_separator=False)
        if not self.is_condition(condition):
            raise SyntaxError("Expected a condition after ':' or 'if'")
        return condition

    # ----- prefix expressions ----------------------------------------------

    def parse_prefix(self) -> Node:
        tok = self.current()

        if tok.type in ("PLUS", "MINUS", "NOT"):
            self.advance()
            child = self.parse_expr(55 if tok.type != "NOT" else 25,
                                    allow_separator=False)
            kind = {"PLUS": "UNARY_PLUS", "MINUS": "NEGATE", "NOT": "NOT"}[tok.type]
            return Node(kind, tok.text, [child])

        if tok.type == "NUMBER":
            self.advance()
            return Node("NUMBER", tok.text)

        if tok.type == "CONSTANT":
            self.advance()
            return Node("BUILTIN", tok.text)

        if tok.type == "VARIABLE":
            self.advance()
            # Parentheses mean function evaluation only when the symbol table
            # says this name is defined with one or more free variables.
            # Otherwise, the ordinary Pratt loop sees the following '(' as a
            # grouped factor, so g(g) and g(2) are implicit multiplication.
            parameters = self.function_parameters(tok)
            if parameters is not None and self.current().type == "LEFT_PAREN":
                self.advance()  # LEFT_PAREN
                arguments: list[Node] = []
                if self.current().type != "RIGHT_PAREN":
                    arguments.append(self.parse_expr(0, allow_separator=False))
                    while self.accept("COMMA"):
                        arguments.append(self.parse_expr(0, allow_separator=False))
                self.expect("RIGHT_PAREN")
                if len(arguments) != len(parameters):
                    raise SyntaxError(
                        f"Function {tok.text} expects {len(parameters)} argument(s), "
                        f"got {len(arguments)}"
                    )
                return Node(
                    "FUNCTION_APPLICATION",
                    tok.text,
                    arguments,
                    attrs={"subscript": tok.subscript,
                           "free_variables": parameters},
                )
            return self.variable_node(tok)

        if tok.type == "FUNCTION":
            self.advance()
            if self.accept("PLAIN_LEFT_BRACE"):
                arg = self.parse_expr(0, allow_separator=False)
                self.expect("PLAIN_RIGHT_BRACE")
            elif self.accept("LEFT_PAREN"):
                arg = self.parse_expr(0, allow_separator=False)
                self.expect("RIGHT_PAREN")
            else:
                arg = self.parse_expr(55, allow_separator=False)
            return Node("FUNCTION", tok.text, [arg])

        if tok.type == "FRAC":
            self.advance()
            numerator = self.parse_group_argument()
            denominator = self.parse_group_argument()
            return Node("DIVIDE", "/", [numerator, denominator])

        if tok.type == "SQRT":
            self.advance()
            return Node("SQRT", tok.text, [self.parse_group_argument()])

        if tok.type == "LEFT_ABS":
            self.advance()
            arg = self.parse_expr(0, allow_separator=False)
            self.expect("RIGHT_ABS")
            return Node("ABSOLUTE_VALUE", "| |", [arg])

        if tok.type == "LEFT_PAREN":
            self.advance()
            first = self.parse_expr(0, allow_separator=False)
            if self.accept("COMMA"):
                values = [first, self.parse_expr(0, allow_separator=False)]
                while self.accept("COMMA"):
                    values.append(self.parse_expr(0, allow_separator=False))
                self.expect("RIGHT_PAREN")
                return Node("TUPLE", children=values)
            self.expect("RIGHT_PAREN")
            return Node("GROUP", children=[first])

        # Only structural braces invoke the user-level brace grammar.
        if tok.type == "LEFT_BRACE":
            return self.parse_structural_brace()

        # Plain braces only group their contents. They cannot introduce a
        # set comprehension, restricted-expression construct, or piecewise
        # expression.
        if tok.type == "PLAIN_LEFT_BRACE":
            self.advance()
            inner = self.parse_expr(0)
            self.expect("PLAIN_RIGHT_BRACE")
            return Node("GROUP", children=[inner])

        raise SyntaxError(f"Unexpected token {tok.type}:{tok.text!r} at index {self.i}")

    def function_parameters(self, token: Token) -> list[str] | None:
        """Return declared free-variable names, or None if this is not callable.

        Accepted symbol-table entry shapes include dictionaries with a
        ``free_variables``, ``parameters``, ``params``, or ``arguments`` field,
        and this parser's own DEFINITION AST. An empty/missing parameter list
        means the name is a value, not a function.
        """
        key = token.text
        entry = self.symbol_table.get(key)
        if entry is None:
            # Also support symbol tables keyed by the full subscripted name.
            key = token.text
            entry = self.symbol_table.get(key)
        if entry is None:
            return None

        raw_parameters = None
        if isinstance(entry, Node) and entry.kind == "DEFINITION":
            for child in entry.children:
                if child.kind == "FREE_VARIABLES":
                    raw_parameters = child.value
                    break
        elif isinstance(entry, dict):
            for field_name in ("free_variables", "parameters", "params", "arguments"):
                if field_name in entry:
                    raw_parameters = entry[field_name]
                    break
            if raw_parameters is None and isinstance(entry.get("definition"), Node):
                return self.function_parameters_from_definition(entry["definition"])
        else:
            # A few symbol-table implementations store parameter lists
            # directly as the entry value.
            if isinstance(entry, (list, tuple)):
                raw_parameters = entry

        if not raw_parameters:
            return None
        names = []
        for item in raw_parameters:
            if isinstance(item, Node):
                names.append(str(item.value))
            elif isinstance(item, dict):
                names.append(str(item.get("name", item.get("value", item))))
            else:
                names.append(str(item))
        return names if names else None

    @staticmethod
    def function_parameters_from_definition(definition: Node) -> list[str] | None:
        if definition.kind != "DEFINITION":
            return None
        for child in definition.children:
            if child.kind == "FREE_VARIABLES" and child.value:
                return [str(value) for value in child.value]
        return None

    def parse_group_argument(self) -> Node:
        if self.accept("PLAIN_LEFT_BRACE"):
            expr = self.parse_expr(0)
            self.expect("PLAIN_RIGHT_BRACE")
            return expr
        # TeX structural braces may be emitted by a tokenizer around a
        # command argument in some configurations; match them, but do not
        # send their contents through the user-level brace grammar here.
        if self.accept("LEFT_BRACE"):
            expr = self.parse_expr(0)
            self.expect("RIGHT_BRACE")
            return expr
        return self.parse_prefix()

    # ----- user-authored structural brace constructs -----------------------

    def parse_structural_brace(self) -> Node:
        self.expect("LEFT_BRACE")
        start = self.i
        end = self.find_matching_close(start, "RIGHT_BRACE")
        content = self.normalize_ellipsis(self.tokens[start:end])
        self.i = end + 1

        if not content:
            return Node("SET_LITERAL", "empty", attrs={"finite": True})

        # A comprehension is recognized by a top-level membership operator
        # in the first clause. ':' and 'if' are equivalent separators.
        clauses = self.split_top_level(content, "CONDITIONAL_SEPARATOR")
        first = clauses[0]
        if self.contains_top_level_type(first, "IN"):
            in_pos = self.find_top_level(first, "IN")
            bound_vars = self.parse_bound_variables(first[:in_pos])
            source = self.parse_fragment(first[in_pos + 1:])
            conditions = [self.parse_fragment(c) for c in clauses[1:] if c]
            condition = self.combine_conditions(conditions)
            return Node("COMPREHENSION", children=[source, condition],
                        attrs={"bound_variables": bound_vars})

        # A comma-separated list of branches with conditional separators
        # is piecewise. A branch without a separator can be the default.
        branches = self.split_top_level(content, "COMMA")
        if len(branches) > 1 and any(self.contains_top_level_type(
                branch, "CONDITIONAL_SEPARATOR") for branch in branches):
            return Node("EXPRESSION", children=self.parse_piecewise(branches),
                        attrs={"piecewise": True, "domain": "R"})

        # Comma-separated values (or a trailing ellipsis) may be a set
        # literal / inferred infinite sequence. Do this before parsing the
        # whole fragment, since commas and ellipses are not expression ops.
        has_top_level_comma = len(branches) > 1
        has_ellipsis = any(tok.type == "ELLIPSIS" for tok in content)
        if has_top_level_comma or has_ellipsis:
            return self.parse_set_literal(content)

        # A brace containing only a condition denotes a comprehension over
        # R or R^n, with non-global variables inferred as bound variables.
        whole = self.parse_fragment(content)
        if self.is_condition(whole):
            variable_keys = self.variables_in(whole)
            bound_keys = [key for key in variable_keys
                          if self.variable_name_from_key(key) not in self.symbol_table
                          and key[0] not in self.symbol_table]
            bound_names = [self.variable_name_from_key(key) for key in bound_keys]
            source = (Node("POWER", "^", [Node("BUILTIN", "R"),
                                            Node("NUMBER", str(len(bound_names)))])
                      if len(bound_names) > 1 else Node("BUILTIN", "R"))
            return Node("COMPREHENSION", children=[source, whole],
                        attrs={"bound_variables": bound_names})

        # Otherwise the separator introduces a domain restriction.
        if len(clauses) > 1:
            expr = self.parse_fragment(clauses[0])
            domains = [self.parse_fragment(c) for c in clauses[1:] if c]
            domain = self.combine_conditions(domains)
            if not self.is_condition(domain):
                raise SyntaxError("A domain restriction must be a condition")
            return Node("RESTRICTED_EXPRESSION", children=[expr, domain])

        # A single non-condition expression in braces is a singleton set.
        return Node("SET_LITERAL", children=[whole], attrs={"finite": True})

    @staticmethod
    def normalize_ellipsis(tokens: list[Token]) -> list[Token]:
        """Normalize either a dedicated ellipsis token or three dot tokens."""
        result: list[Token] = []
        i = 0
        dot_types = {"DOT", "PERIOD", "FULL_STOP"}
        while i < len(tokens):
            if (i + 2 < len(tokens) and
                    all(t.type in dot_types or t.text == "." for t in tokens[i:i + 3])):
                result.append(Token("ELLIPSIS", "..."))
                i += 3
            else:
                result.append(tokens[i])
                i += 1
        return result

    def parse_set_literal(self, content: list[Token]) -> Node:
        """Parse finite literals and simple finite/infinite sequence patterns."""
        clauses = self.split_top_level(content, "COMMA")
        ellipsis_positions = [
            (ci, ti)
            for ci, clause in enumerate(clauses)
            for ti, tok in enumerate(clause)
            if tok.type == "ELLIPSIS"
        ]
        if len(ellipsis_positions) > 1:
            raise SyntaxError("Only one ellipsis is supported in a set pattern")

        if ellipsis_positions:
            ci, ti = ellipsis_positions[0]
            before_tokens = [tok for clause in clauses[:ci] for tok in clause]
            before_tokens.extend(clauses[ci][:ti])
            after_tokens = clauses[ci][ti + 1:] + [
                tok for clause in clauses[ci + 1:] for tok in clause
            ]
            before_clauses = [c for c in clauses[:ci] if c]
            if clauses[ci][:ti]:
                before_clauses.append(clauses[ci][:ti])
            after_clauses = []
            if clauses[ci][ti + 1:]:
                after_clauses.append(clauses[ci][ti + 1:])
            after_clauses.extend(c for c in clauses[ci + 1:] if c)

            before_elements = [self.parse_fragment(c) for c in before_clauses]
            after_elements = [self.parse_fragment(c) for c in after_clauses]
            elements = before_elements + after_elements
            if not before_elements:
                raise SyntaxError("A set pattern needs at least one term before '...'")

            if after_elements:
                # Ellipsis followed by a final term denotes a finite pattern,
                # e.g. {1,2,...,100}. Preserve all explicitly supplied terms.
                values = [self.numeric_value(node) for node in elements]
                attrs: dict[str, Any] = {"finite": True, "inferred": False}
                if len(before_elements) >= 2 and all(v is not None for v in values):
                    before_values = values[:len(before_elements)]
                    step = before_values[1] - before_values[0]
                    if (step != 0 and all(
                        abs((before_values[i] - before_values[i - 1]) - step) < 1e-12
                        for i in range(2, len(before_values))
                    )):
                        last = values[-1]
                        quotient = (last - before_values[0]) / step
                        if quotient >= 0 and abs(quotient - round(quotient)) < 1e-9:
                            attrs.update({
                                "pattern": "arithmetic_progression",
                                "first": before_values[0],
                                "step": step,
                                "last": last,
                                "inferred": True,
                            })
                return Node("FINITE_SET_PATTERN", children=elements, attrs=attrs)

            # A trailing ellipsis denotes an infinite continuation.
            values = [self.numeric_value(node) for node in before_elements]
            if len(values) >= 3 and all(v is not None for v in values):
                differences = [values[i] - values[i - 1] for i in range(1, len(values))]
                if all(abs(d - differences[0]) < 1e-12 for d in differences[1:]):
                    return Node("INFINITE_SET_PATTERN", children=before_elements,
                                attrs={"pattern": "arithmetic_progression",
                                       "first": values[0], "step": differences[0],
                                       "inferred": True})
            return Node("INFINITE_SET_PATTERN", children=before_elements,
                        attrs={"pattern": "unknown", "inferred": False})

        elements = []
        for clause in clauses:
            if not clause:
                raise SyntaxError("Expected a set element between commas")
            elements.append(self.parse_fragment(clause))
        return Node("SET_LITERAL", children=elements, attrs={"finite": True})

    @staticmethod
    def numeric_value(node: Node) -> float | None:
        try:
            if node.kind == "NUMBER":
                return float(node.value)
            if node.kind == "NEGATE" and len(node.children) == 1:
                value = Parser.numeric_value(node.children[0])
                return None if value is None else -value
            if node.kind == "UNARY_PLUS" and len(node.children) == 1:
                return Parser.numeric_value(node.children[0])
        except (TypeError, ValueError):
            return None
        return None

    def parse_piecewise(self, branches: list[list[Token]]) -> list[Node]:
        result = []
        for branch in branches:
            if not branch:
                continue
            parts = self.split_top_level(branch, "CONDITIONAL_SEPARATOR")
            expr = self.parse_fragment(parts[0])
            conditions = [self.parse_fragment(p) for p in parts[1:] if p]
            if not conditions:
                result.append(Node("PIECE", children=[expr]))
                continue
            condition = self.combine_conditions(conditions)
            if not self.is_condition(condition):
                raise SyntaxError("A piecewise branch condition must be a condition")
            result.append(Node("PIECE", children=[expr, condition]))
        return result

    @staticmethod
    def combine_conditions(conditions: list[Node]) -> Node:
        if not conditions:
            return Node("CONDITION", "true")
        if len(conditions) == 1:
            return conditions[0]
        return Node("AND", "and", conditions)

    # ----- balanced delimiters and top-level splitting ---------------------

    def find_matching_close(self, start: int, expected_close: str) -> int:
        stack = [expected_close]
        for j in range(start, len(self.tokens)):
            typ = self.tokens[j].type
            if typ == "END_OF_INPUT":
                break
            if typ in self.OPEN_TO_CLOSE:
                stack.append(self.OPEN_TO_CLOSE[typ])
            elif typ in self.CLOSE_TYPES:
                if not stack or typ != stack[-1]:
                    raise SyntaxError(f"Mismatched closing delimiter {typ} at token index {j}")
                stack.pop()
                if not stack:
                    return j
        raise SyntaxError(f"Missing closing delimiter {expected_close}")

    @classmethod
    def split_top_level(cls, tokens: list[Token], wanted_type: str) -> list[list[Token]]:
        pieces, start, stack = [], 0, []
        for j, tok in enumerate(tokens):
            if tok.type in cls.OPEN_TO_CLOSE:
                stack.append(cls.OPEN_TO_CLOSE[tok.type])
                continue
            if tok.type in cls.CLOSE_TYPES:
                if stack and tok.type == stack[-1]:
                    stack.pop()
                continue
            if not stack and tok.type == wanted_type:
                pieces.append(tokens[start:j])
                start = j + 1
        pieces.append(tokens[start:])
        return pieces

    @classmethod
    def contains_top_level_type(cls, tokens: list[Token], wanted_type: str) -> bool:
        stack = []
        for tok in tokens:
            if tok.type in cls.OPEN_TO_CLOSE:
                stack.append(cls.OPEN_TO_CLOSE[tok.type])
                continue
            if tok.type in cls.CLOSE_TYPES:
                if stack and tok.type == stack[-1]:
                    stack.pop()
                continue
            if not stack and tok.type == wanted_type:
                return True
        return False

    @classmethod
    def find_top_level(cls, tokens: list[Token], wanted_type: str) -> int:
        stack = []
        for j, tok in enumerate(tokens):
            if tok.type in cls.OPEN_TO_CLOSE:
                stack.append(cls.OPEN_TO_CLOSE[tok.type])
                continue
            if tok.type in cls.CLOSE_TYPES:
                if stack and tok.type == stack[-1]:
                    stack.pop()
                continue
            if not stack and tok.type == wanted_type:
                return j
        raise SyntaxError(f"Expected top-level {wanted_type}")

    # ----- variables, classification, helpers -----------------------------

    @staticmethod
    def variable_node(tok: Token) -> Node:
        return Node("VARIABLE", tok.text[0], attrs={"subscript": tok.subscript})

    def parse_bound_variables(self, tokens: list[Token]) -> list[str]:
        if len(tokens) == 1 and tokens[0].type == "VARIABLE":
            return [tokens[0].text]
        if (len(tokens) >= 3 and tokens[0].type == "LEFT_PAREN"
                and tokens[-1].type == "RIGHT_PAREN"):
            parts = self.split_top_level(tokens[1:-1], "COMMA")
            if len(parts) >= 2 and all(len(p) == 1 and p[0].type == "VARIABLE" for p in parts):
                return [p[0].text for p in parts]
        raise SyntaxError("Invalid bound-variable list in set comprehension")

    @staticmethod
    def variable_key(node: Node) -> tuple[str, str | None]:
        return node.value, node.attrs.get("subscript")

    @staticmethod
    def variable_name_from_key(key: tuple[str, str | None]) -> str:
        base, subscript = key
        return base if subscript is None else f"{base}_{{{subscript}}}"

    def variables_in(self, node: Node) -> list[tuple[str, str | None]]:
        found = []
        def visit(n: Node):
            if n.kind == "VARIABLE":
                key = self.variable_key(n)
                if key not in found:
                    found.append(key)
            for child in n.children:
                visit(child)
        visit(node)
        return found

    def parse_fragment(self, tokens: list[Token]) -> Node:
        if not tokens:
            raise SyntaxError("Expected an expression, found an empty fragment")
        sub = Parser(tokens, symbol_table=self.symbol_table)
        node = sub.parse_expr(0)
        if not sub.at_end():
            tok = sub.current()
            raise SyntaxError(f"Unconsumed token in fragment: {tok.type}:{tok.text!r}")
        return node

    def classify(self, node: Node) -> str:
        # Conditions take priority; otherwise classify the value by its AST.
        if self.is_condition(node):
            return "CONDITION"
        if self.is_set_valued(node):
            return "SET"
        return "EXPRESSION"

    def is_set_valued(self, node: Node) -> bool:
        explicit_set_kinds = {
            "SET", "SET_LITERAL", "FINITE_SET_PATTERN", "INFINITE_SET_PATTERN",
            "COMPREHENSION", "UNION", "INTERSECTION",
        }
        if node.kind in explicit_set_kinds:
            return True
        # Built-in number domains and the empty set are set-valued symbols.
        if node.kind == "BUILTIN" and str(node.value) in {
            "N", "Z", "Q", "R", "empty", r"\emptyset", "∅"
        }:
            return True
        # These operators can denote set construction when applied to sets.
        if node.kind in {"SUBTRACT", "MULTIPLY"} and len(node.children) == 2:
            return all(self.is_set_valued(child) for child in node.children)
        if node.kind == "POWER" and node.children:
            return self.is_set_valued(node.children[0])
        return False

    def is_condition(self, node: Node) -> bool:
        if node.kind in self.CONDITION_KINDS:
            return True
        if node.kind == "RESTRICTED_EXPRESSION":
            return False
        return False

    def can_end_atom(self, node: Node) -> bool:
        return node.kind in {
            "NUMBER", "BUILTIN", "VARIABLE", "FUNCTION", "GROUP", "TUPLE",
            "ABSOLUTE_VALUE", "SET", "SET_LITERAL", "FINITE_SET_PATTERN", "INFINITE_SET_PATTERN", "COMPREHENSION", "EXPRESSION",
            "RESTRICTED_EXPRESSION", "FACTORIAL", "POWER", "SQRT",
        }

    @staticmethod
    def starts_atom(tok: Token) -> bool:
        return tok.type in {
            "NUMBER", "CONSTANT", "VARIABLE", "FUNCTION", "LEFT_PAREN",
            "LEFT_BRACE", "PLAIN_LEFT_BRACE", "LEFT_ABS", "FRAC", "SQRT",
        }

    @staticmethod
    def operator_name(typ: str) -> str:
        return {
            "OR": "OR", "AND": "AND", "EQUALS": "EQUALS",
            "NOT_EQUALS": "NOT_EQUALS", "LESS_THAN": "LESS_THAN",
            "GREATER_THAN": "GREATER_THAN", "LESS_EQUAL": "LESS_EQUAL",
            "GREATER_EQUAL": "GREATER_EQUAL", "IN": "IN",
            "UNION": "UNION", "INTERSECTION": "INTERSECTION",
            "PLUS": "ADD", "MINUS": "SUBTRACT", "MULTIPLY": "MULTIPLY",
            "DIVIDE": "DIVIDE", "IMPLICIT_MULTIPLY": "MULTIPLY",
        }.get(typ, typ)


def parse_tokens(tokens: Iterable[str | Token], symbol_table=None) -> Node:
    """Parse tokenizer output and return the root AST Node."""
    return Parser(tokens, symbol_table=symbol_table).parse()


if __name__ == "__main__":
    import tokenizer_prototype as tokenizer

    examples = [
        r"Q\left(x\right)<-x<1",
        r"\left\{x in R : x < 1\right\}",
        r"\left\{x in R if x < 1\right\}",
        r"\left\{x^{2} : x in R\right\}",
        r"\left\{x^{2} if x in R\right\}",
        r"\left\{x^{2} if x < 0, x if x >= 0\right\}",
        r"\left\{x^{2} : x < 0, x : x >= 0\right\}",
    ]

    for example in examples:
        print(f"\nInput: {example}")
        try:
            print(parse_tokens(tokenizer.tokenize(example)).pretty())
        except (SyntaxError, ValueError) as exc:
            print(f"Parse error: {exc}")
