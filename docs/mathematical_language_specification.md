# Mathematical Language Specification

## Overview
The calculator uses conventional mathematical notation with support for variables, functions, equations, inequalities, piecewise expressions, and domains.

Expressions use double-precision real numbers. Complex numbers are not supported.

## Identifiers
Variables and user-defined functions use a single letter with an optional subscript.

Examples:
 - x
 - a
 - x_1
 - f
 - g_2

Subscripts may contain letters and numbers. A subscript is treated literally, so x_pi does not refer to π.

Function names are distinct from variables.

## Numbers and Constants
Decimal and integer literals are supported.

Built-in constants include:
 - π
 - e

Numeric literals are values, not functions.

## Operators
Supported arithmetic operators include:

+   -   *   /   ^

Standard mathematical precedence applies.

Implicit multiplication is supported:
 - 2x
 - 2(x + 1)
 - (x + 1)(x - 1)
 - 2sin(x)

These are equivalent to their explicit multiplication forms.

Exponentiation uses ^.

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

## Angle Modes
Trigonometric functions use one of three global angle modes:

 - radians
 - degrees
 - gradians
## Declarations
= declares a mathematical relationship.

Its interpretation depends on the left-hand side:

f(x) = x^2

declares a function and graphs it.

x^2 + y^2 = 25

defines an implicit equation and graphs it.

2 = x

defines the equivalent graph x = 2.

A declaration may contain parameters that prevent it from being directly graphable:

f(x, a) = x^2 + a

<- declares a value or function without requesting visualization:

a <- 5
f(x) <- x^2 + a

The left-hand side of <- must be a valid declaration target.

The following are invalid:

x <- 5
2 <- x
x^2 + y^2 <- 25

## Equations and Inequalities

Equations use =.

Supported inequalities are:

<
<=
>
>=

Inequalities may be graphed as regions.

## Piecewise Expressions

A piecewise expression contains an ordered sequence of expression/condition pairs.

Conditions are evaluated in order. If multiple conditions overlap, the first applicable branch is used.

A piecewise expression is a single mathematical expression.

## Domains

Domains are editor metadata rather than ordinary mathematical syntax.

A variable may have one or more intervals, each with:

a lower bound
an upper bound
inclusive or exclusive endpoints
a number set: N, Z, Q, or R

N includes zero.

Examples:

0 < x <= 10
-5 <= x < a
0 < x < f(a)

Bounds may be arbitrary valid expressions.

Intervals are separated in the domain editor. Overlapping intervals are merged.

Q and R use the same underlying numerical representation, but their mathematical interpretation affects symbolic presentation where applicable.

Additionally, the user may use the "if" keyword to specify simple domain in text.

For example:
x if x < 4

creates the linear identity function strictly bounded above by 4

## Undefined Values

Expressions may be undefined at particular inputs without being invalid expressions.

Examples include:
 - 1 / x
 - sqrt(x)
 - log(x)

Undefined values are handled during numerical evaluation.

## Recursion and Dependencies

User-defined functions may be recursive.

Evaluation uses a fixed maximum call depth. Exceeding that depth produces a runtime error.

Circular dependencies between definitions are invalid.

## Errors

The language distinguishes:

 - syntax errors
 - semantic errors
 - missing definitions
 - runtime errors

An undefined numerical result at a particular input is not itself an expression error.

## Scope

Function parameters are local to their function definition. Global variables and function definitions are otherwise available according to the calculator's definition environment.

Duplicate definitions are errors; a later definition does not replace an earlier one.
Duplicate definitions are errors; a later definition does not replace an earlier one.
