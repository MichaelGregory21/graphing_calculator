# Mathematical Language Specification

## Overview

The calculator uses conventional mathematical notation with support for variables, functions, equations, inequalities, piecewise expressions, sets, and conditional expressions.

Expressions use double-precision real numbers. Complex numbers are not supported.

Sets are first-class mathematical values. They may be constructed, named, combined, tested for membership, and used to restrict expressions.

## Identifiers

Variables and user-defined functions use a single letter with an optional subscript.

Examples:
 - x
 - a
 - x_1
 - f
 - g_2

Subscripts may contain letters and numbers. A subscript is treated literally, so `x_pi` does not refer to π.

Set names use the same identifier rules as variables.

For example:

A = {x in R : x < 0}

defines the set `A`.

Function names are distinct from variables.

## Numbers and Constants

Decimal and integer literals are supported.

Built-in constants include:
 - π
 - e

Numeric literals are values, not functions.

## Operators

Supported arithmetic operators include:

`+`  `-`   `*`   `/`  `^`

Standard mathematical precedence applies.

Implicit multiplication is supported:
 - 2x
 - 2(x + 1)
 - (x + 1)(x - 1)
 - 2sin(x)

These are equivalent to their explicit multiplication forms.

Exponentiation uses `^`.

Set operations use:
 - `∩` for intersection
 - `∪` for union
 - `-` for set difference
 - `*` or justaposition for Cartesian product
 - `^` for finite Cartesian powers
 - juxtaposition for scalar multiplication where the left operand is a scalar and the right operand is a set

For example:

A ∩ B
A ∪ B
A - B
A * A
A^2
2A

The parser and semantic analyzer determine the meaning of an operator from the types of its operands.

## Functions

Built-in functions include:
 - trigonometric functions
 - inverse trigonometric functions
 - hyperbolic functions
 - logarithms and natural logarithm
 - exponential
 - absolute value
 - floor and ceiling
 - minimum and maximum
 - sign
 - factorial

User-defined functions may take any number of parameters.

Examples:
 - f(x) = x^2
 - g(x, a) = x^2 + a

Functions of n-many variables take n-tuples are component-wise arguments. For example, `f(a,b,c) = a * b * c` is defined on `R^3`

## Angle Modes

Trigonometric functions use one of three global angle modes:

 - radians
 - degrees
 - gradians

## Sets

Sets are first-class mathematical values.

The language supports several forms of set construction.

### Built-in Number Sets

The built-in sets are:

 - `N` — natural numbers, including zero
 - `Z` — integers
 - `Q` — rational numbers
 - `R` — real numbers

They may be used directly as sets or as the base domain of a set comprehension.

For example:

x in R

and:

{x in N : x < 10}

are valid.

### Finite Enumeration

Finite sets may be written by explicitly listing their elements:

{0, 1, 2, 3, 4}

The elements are ordinary expressions and are evaluated when the set is resolved.

Repeated elements represent the same set element and do not create duplicate mathematical elements.

### Set Comprehension

A set may be defined using set-builder notation:

{x in R : x^2 > 10}

The expression before the colon determines the elements being considered, while the Boolean formula after the colon determines which elements belong to the set.

The general form is:

{x in A : P(x)}

where `A` is a set and `P(x)` is a Boolean condition.

The domain may therefore itself be a set expression:

{x in {0, 1, 2, 3} : x > 1}

### Default Domain

A set-builder expression without an explicit domain uses `R`.

Therefore:

{x < 0}

is equivalent to:

{x in R : x < 0}

### Set Membership

Membership uses `in`:

x in A

A membership expression is Boolean.

The right-hand side must resolve to a set.

Membership may be used in ordinary conditions:

x^2 if x in A

and in set comprehensions:

{x in R : x in A}

### Set Operations

The calculator supports:

A ∩ B

for intersection,

A ∪ B

for union,

A - B

for set difference,

A * B

for Cartesian product.

Intersection and union are rendered as mathematical `∩` and `∪`.

### Set Powers

For a finite positive integer `n`:

A^n

denotes the n-fold Cartesian product of `A`.

Therefore:

A^2

is equivalent to:

A * A

The exponent must be a finite non-negative integer supported by the implementation.

An infinite exponent such as:

A^∞

is not defined.

### Scalar Multiplication of Sets

For a finite scalar `n`:

nA

denotes:

{n a : a in A}

For example:

2Z

is the set of even integers.

Scalar multiplication also applies to Cartesian products:

n(A * A)

and:

n(A^m)

are valid when the underlying set expression is defined.

For ordered pairs:

n(a,b)

is interpreted as:

(na, nb)

when the pair represents a two-dimensional element.

### Ellipses

Finite or computationally recognizable sequences may be written using ellipses:

{2, 4, 6, ...}

The calculator may recognize simple supported patterns, but it does not attempt general symbolic inference from arbitrary ellipses.

If the meaning of an ellipsis is ambiguous or computationally infeasible, the expression produces an appropriate semantic or evaluation error rather than being resolved indefinitely.

### Named Sets

A set may be assigned a name using a declaration:

A = {x in 2Z : x / 2 in 2Z}

The set can subsequently be used wherever a set is expected:

x^2 if x in A

Set definitions participate in the same dependency system as other definitions.

## Boolean Conditions

A formula used as a set-builder condition or `if` condition is a Boolean statement.

It may contain:
 - arithmetic operators
 - functions
 - constants
 - variables
 - comparisons
 - set membership

Supported comparison operators are:

`=`, `<`, `<=`, `>`, `>=`, `in`

For example:

x^2 > 10

and:

x in A

are Boolean expressions.

A Boolean expression may be used directly as a condition:

x^2 if x^2 > 10

or as a set-builder predicate:

{x in R : x^2 > 10}

## Declarations

`=` declares a mathematical relationship.

Its interpretation depends on the left-hand side.

For example:

f(x) = x^2

declares a function and graphs it.

A set definition:

A = {x in R : x < 0}

declares the set `A`.

An implicit equation:

x^2 + y^2 = 25

defines a graphable mathematical relationship.

A declaration may contain parameters that prevent it from being directly graphable:

f(x, a) = x^2 + a

Using `<-` declares a value, function, or set without requesting visualization:

a <- 5
f(x) <- x^2 + a
A <- {x in R : x < 0}

The left-hand side of `<-` must be a valid declaration target.

The following are invalid:

x <- 5
2 <- x
x^2 + y^2 <- 25

## Equations and Inequalities

Equations use `=`.

Supported inequalities are:

`<`, `<=`, `>`, `>=`

Inequalities may be graphed as regions.

When an equation is not a valid declaration, it is interpreted as a graphable mathematical relationship where appropriate.

## Piecewise Expressions

A piecewise expression contains an ordered sequence of expression/condition pairs.

For example:

{x if x < 1, 2x if 1 <= x, 0}

Conditions are evaluated in order. If multiple conditions overlap, the first applicable branch is used.

A final branch without a condition acts as the default branch.

A piecewise expression is a single mathematical expression.

## Conditional Expressions

`if` introduces a Boolean condition for an expression.

For example:

x if x < 4

restricts `x` to values satisfying `x < 4`.

A set may be used explicitly:

x^2 if x in {x in R : x < 0}

A named set may also be used:

 - A = {x in R : x < 0}
 - x^2 if x in A

Conditional expressions are ordinary expression-tree constructs rather than editor metadata.

## Undefined Values

Expressions may be undefined at particular inputs without being invalid expressions.

Examples include:
 - 1 / x
 - sqrt(x)
 - log(x)

Undefined values are handled during numerical evaluation.

An input may also be mathematically outside a conditional restriction or set membership condition. Such an input produces no applicable value for that conditional expression rather than making the expression itself invalid.

## Recursion and Dependencies

User-defined functions may be recursive.

Evaluation uses a fixed maximum call depth. Exceeding that depth produces a runtime error.

Variables, functions, and named sets form a dependency graph.

Circular dependencies between definitions are invalid.

## Errors

The language distinguishes:

 - syntax errors
 - semantic errors
 - missing definitions
 - invalid set constructions
 - runtime errors

An undefined numerical result at a particular input is not itself an expression error.

## Scope

Function parameters are local to their function definition. Global variables, functions, and sets are otherwise available according to the calculator's definition environment.

Duplicate definitions are errors; a later definition does not replace an earlier one.
