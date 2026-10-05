# Parser and Semantic Analysis Pipeline

## Overview

The language-processing pipeline is:

source -> lexer -> parser -> expression tree -> semantic analysis -> resolved model

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
 - membership operators
 - set operators
 - structural delimiters
 - set-builder separators
 - `if`

Whitespace is normally insignificant.

Braces have mathematical meaning in the user-facing language. They may represent:
 - finite set enumeration
 - set-builder notation
 - piecewise expressions

They are not generic expression-layer delimiters.

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
 - set operations
 - parentheses
 - set expressions
 - comparisons
 - membership
 - conditional expressions
 - declarations

Implicit multiplication is recognized when two adjacent expressions can form a product.

For example:

2x
2(x + 1)
x sin(x)

are parsed as multiplication.

## Function Calls

When a function name is followed by its argument structure, the parser creates a function-call node containing the argument expressions.

Structured editor serialization may use internal delimiters to preserve editable expression layers, but those delimiters are distinct from user-facing mathematical braces.

An empty function argument remains valid editor state where the editor permits incomplete input.

## Parentheses

Parentheses are parsed as ordinary grouping.

(x + 1) * y

is parsed by first parsing the expression inside the parentheses and then using that expression as the left operand of multiplication.

Parentheses do not create mathematical values of their own.

## Sets

Braces introduce mathematical set or piecewise syntax according to the surrounding grammar.

### Finite Sets

For:

{0, 1, 2, 3}

the parser constructs a set-enumeration node containing the element expressions.

### Set Comprehension

For:

{x in R : x^2 > 10}

the parser constructs a set-comprehension node containing:
 - the bound variable
 - the source set `R`
 - the predicate `x^2 > 10`

A simple form:

{x < 0}

is interpreted as a set-builder expression over `R` when a set value is required.

Thus:

{x < 0}

is equivalent to:

{x in R : x < 0}

The braces may be omitted in simple contexts where the parser can determine that the expression is being interpreted as a set condition.

### Piecewise Expressions

A brace-delimited sequence containing conditional branches is parsed as a piecewise expression.

For example:

{x if x < 1, 2x if 1 <= x, 0}

is parsed as an ordered collection of expression/condition pairs.

The parser determines whether a brace expression represents a finite set, set-builder expression, or piecewise expression from its grammatical contents.

## Set Operators

The parser recognizes:

∩
∪
-
*
^

as potential set operators.

The semantic analyzer determines whether their operands have compatible meanings.

For example:

A * A

is interpreted as a Cartesian product when both operands are sets.

Likewise:

A^2

is interpreted as a Cartesian power when `A` is a set and `2` is a supported finite integer.

## Membership

The `in` operator creates a membership expression.

For:

x in A

the parser creates:

Membership
├── x
└── A

Membership is a Boolean expression.

It may occur in:
 - conditional expressions
 - set-builder predicates
 - piecewise conditions
 - ordinary Boolean contexts

## Conditional Expressions

The `if` keyword creates a conditional expression.

For:

x^2 if x < 0

the parser creates:

Conditional
├── x^2
└── x < 0

For:

x^2 if x in A

the condition is a Membership node.

## Declarations

The parser distinguishes declaration forms from ordinary expressions.

Examples:

f(x) = x^2
A = {x in R : x < 0}
a <- 5
x^2 + y^2 = 25

The parser records the syntactic form; semantic analysis determines whether the declaration is legal and what it represents.

## Syntax Errors

Syntax errors occur when the token sequence cannot form a valid expression.

Examples:
 - 2 +
 - f(
 - x ++
 - {x in R :
 - A =

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
 - whether set operations have valid operand types
 - whether set-builder domains are valid sets
 - whether set-builder predicates are Boolean
 - whether membership has a set on its right-hand side
 - whether set powers use supported finite exponents
 - whether scalar set operations are valid
 - whether an expression is graphable

## Symbol Resolution

Definitions are collected into a symbol environment.

References are resolved against:
 - local function parameters
 - local set-builder variables
 - user-defined symbols
 - built-in sets
 - built-in functions and constants

An unresolved reference produces a missing-definition error.

For example:

f(x) = a*x

requires `a` to be defined.

Likewise:

A = {x in R : x < a}

requires `a` to be defined.

## Set Comprehension Scope

The variable introduced by a set comprehension is local to that comprehension.

For:

{x in R : x^2 > 10}

the `x` appearing in the predicate refers to the bound set-comprehension variable.

The binding must not accidentally capture an unrelated global `x`.

Nested comprehensions may introduce distinct local bindings.

## Dependency Analysis

Definitions and set expressions form a dependency graph.

For example:

a = 5
A = {x in R : x < a}
f(x) = x^2 if x in A

produces dependencies equivalent to:

a → A → f

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

A = {x in R : x < 0}

→ set definition

x^2 if x in A

→ conditionally restricted function graph

A mathematically valid definition may still be non-graphable, such as a function with unresolved free parameters or a set that cannot be represented by the currently supported graphing algorithms.

## Incremental Analysis

Editing one expression should not require rebuilding the entire mathematical environment unnecessarily.

After an edit, the system should:
 - parse the changed expression
 - update its tree
 - update affected definitions
 - update affected set definitions
 - recompute affected dependencies
 - re-run semantic analysis for affected expressions

Unchanged expressions should retain their existing analysis where possible.

## Runtime Evaluation

Semantic analysis establishes that an expression is meaningful; numerical and set evaluation occurs later.

Undefined numerical results are normal evaluation outcomes.

For example:

1 / x

is semantically valid even though evaluation at x = 0 is undefined.

Set evaluation may likewise fail to produce a complete result when a set construction exceeds its supported computational model or budget. Such failures are evaluation results rather than parser failures.

Recursive evaluation is subject to the configured maximum call depth.
