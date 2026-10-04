# Parser and Semantic Analysis Pipeline

## Overview

The language-processing pipeline is:

source
  ↓
lexer
  ↓
parser
  ↓
expression tree
  ↓
semantic analysis
  ↓
resolved model

Parsing determines structure. Semantic analysis determines whether that structure is valid and resolves its meaning.

## Lexing

The lexer converts source text into tokens.

Tokens include:
 - numbers
 - identifiers
 - operators
 - parentheses
 - commas
 - declaration operators
 - comparison operators
 - structural delimiters

Whitespace is normally insignificant.

The structured serialization uses { and } as expression-layer delimiters.

For example:

sin{x + 1}

produces tokens corresponding to:

sin
{
x
+
1
}

Braces are not mathematical grouping operators.

## Parsing Strategy

The parser consumes tokens and constructs the expression tree directly.

Operator precedence must be handled so that:

x + 2y^2

is interpreted as:

x + (2 * (y^2))

A precedence-based expression parser, such as precedence climbing or Pratt parsing, is appropriate.

The parser handles:
 - primary expressions
 - function calls
 - exponentiation
 - unary operators
 - multiplication and division
 - addition and subtraction
 - comparisons
 - declarations

Implicit multiplication is recognized when two adjacent expressions can form a product.

For example:

2x
2(x + 1)
x sin(x)

are parsed as multiplication.

## Function Calls

When a function name is followed by an expression layer, the parser creates a function-call node containing that layer.

Structured serialization makes the boundary explicit:

sin{x + 1}

represents:

Sin
└── x + 1

while:

sin{x} + 1

represents:

Add
├── Sin(x)
└── 1

An empty layer is valid editor state:

sin{}

The parser therefore creates an empty child expression rather than treating it as a syntax error.

## Parentheses

Parentheses are parsed as ordinary grouping.

(x + 1) * y

is parsed by first parsing the expression inside the parentheses and then using that expression as the left operand of multiplication.

Parentheses do not create editor layers.

## Structural Constructs

Structural syntax is parsed according to the construct being represented.

For example, a fraction produces two child expressions:

fraction(numerator, denominator)

A piecewise expression produces an ordered collection of expression/condition pairs.

Each structural child is parsed independently while preserving its position in the resulting tree.

## Declaration Parsing
The parser distinguishes declaration forms from ordinary expressions.

Examples:

f(x) = x^2
a <- 5
x^2 + y^2 = 25

The parser records the syntactic form; semantic analysis determines whether the declaration is legal and what it represents.

## Syntax Errors
Syntax errors occur when the token sequence cannot form a valid expression.

Examples:
 - 2 +
 - f(
 - x ++

The parser should report an error at the most useful source location available and recover where practical so that other expression items can continue to be processed.

## Semantic Analysis
Semantic analysis operates on the completed tree.

It determines:
 - whether referenced symbols exist
 - whether declarations have valid targets
 - whether function parameters are valid
 - whether function arity is correct
 - whether duplicate definitions exist
 - whether definitions create circular dependencies
 - whether an expression is graphable
 - whether domain declarations are valid

## Symbol Resolution
Definitions are collected into a symbol environment.

References are resolved against:
 - local function parameters
 - user-defined symbols
 - built-in functions and constants

An unresolved reference produces a missing-definition error.

For example:

f(x) = a*x

requires a to be defined.

## Dependency Analysis
Definitions and domain bounds form a dependency graph.

For example:

a = 5
b = a + 1
f(x) = x + b

produces:

a → b → f

Circular dependencies are detected by analyzing this graph.

Recursive function calls are treated separately and are permitted within the configured recursion limit.

## Graph Classification
Semantic analysis determines what a valid mathematical item represents.

Examples:

f(x) = x^2

→ ordinary function graph

x^2 + y^2 = 25

→ implicit equation

y > x^2

→ inequality region

A mathematically valid definition may still be non-graphable, such as a function with unresolved free parameters.

## Incremental Analysis
Editing one expression should not require rebuilding the entire mathematical environment unnecessarily.

After an edit, the system should:
 - parse the changed expression
 - update its tree
 - update affected definitions
 - recompute affected dependencies
 - re-run semantic analysis for affected expressions

Unchanged expressions should retain their existing analysis where possible.

## Runtime Evaluation
Semantic analysis establishes that an expression is meaningful; numerical evaluation occurs later.

Undefined numerical results are normal evaluation outcomes.

For example:

1 / x

is semantically valid even though evaluation at x = 0 is undefined.

Recursive evaluation is subject to the configured maximum call depth.

Recursive evaluation is subject to the configured maximum call depth.
