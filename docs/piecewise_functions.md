# Piecewise Functions

## Piecewise Syntax

A piecewise expression is written using braces containing one or more conditional branches:

{x^2 if x < 4, 8x - 16 if 4 <= x < 8, 48}

This represents:

 - `x^2` when `x < 4`
 - `8x - 16` when `4 <= x < 8`
 - `48` otherwise

The final branch may omit its condition and acts as the default case.

A piecewise expression is an ordinary mathematical expression and may appear anywhere an ordinary expression is valid.

## `if` Expressions

`if` introduces a Boolean condition for an expression.

For example:

x if x < 4

represents `x` restricted to the values for which `x < 4`.

This provides a compact way to express simple conditional restrictions without constructing a full piecewise expression.

The condition may use set membership:

x^2 if x in A

where:

A = {x in R : x < 0}

This allows reusable restrictions to be represented as ordinary named sets.

A more explicit equivalent is:

x^2 if x in {x in R : x < 0}

The `if` expression therefore does not require a separate domain object or domain editor.

## Set-Based Restrictions

Any Boolean condition supported by the language may be used after `if`.

For example:

x^2 if x < 0

is a direct condition.

A set-based condition:

x^2 if x in {x in R : x < 0}

uses a set comprehension.

A named set:

A = {x in R : x < 0}
x^2 if x in A

allows the restriction to be reused by multiple expressions.

This mechanism is the standard way to express explicit restrictions on a graphable expression.

## Branch Evaluation

Branches are evaluated in order.

For each branch:

1. evaluate its condition;
2. if the condition is true, evaluate and return its expression;
3. otherwise continue to the next branch.

The first applicable branch determines the result.

A branch without a condition is always applicable and should normally be the final branch.

For example:

{x if x < 0, x^2 if x >= 0}

evaluates the first branch for negative `x` and the second branch for non-negative `x`.

Overlapping conditions are permitted. Earlier branches take precedence.

## Conditions and Sets

A branch condition is a Boolean expression.

It may contain:
 - comparisons
 - arithmetic expressions
 - function calls
 - variables
 - set membership
 - other supported Boolean expressions

For example:

{x^2 if x in A, 0}

is valid when `A` is a set.

A set-builder predicate uses the same Boolean expression language:

{x in R : x^2 > 10}

The two constructs therefore share the same underlying semantic representation for conditions.

## Braces

Braces have mathematical meaning and are not general-purpose expression-layer controls.

When the user types `{` or `}`, they are interpreted according to the mathematical grammar.

Braces may introduce:
 - finite set enumeration
 - set-builder notation
 - piecewise expressions

The editor may internally use a separate representation for its structural expression layers, but users cannot directly create arbitrary expression layers with braces.

For example, a function may have an empty argument layer in the structured editor without exposing that layer as user-facing `{}` syntax.

## Nested Piecewise Expressions

Piecewise expressions may occur anywhere an ordinary expression is valid.

For example:

{sin(x) if x < 0, {x^2 if x < 1, 2x} if x >= 0, 0}

Nested braces are interpreted according to the piecewise and set grammar rather than as editor navigation commands.

The parser determines the mathematical construct represented by each brace-delimited expression from its contents and surrounding syntax.

## Default Branches

A branch without an `if` condition is a default branch.

For example:

{x^2 if x < 0, x if x >= 0, 0}

contains an unreachable final default branch and may therefore produce a semantic warning if the implementation chooses to detect unreachable branches.

The final branch:

{ x^2 if x < 0, 0 }

has a meaningful default branch.

## Undefined Values

A branch may be selected successfully while its expression is numerically undefined.

For example:

{1/x if x < 1, 0}

is semantically valid, but the selected expression is undefined at `x = 0`.

Numerical undefinedness is handled by the numerical evaluator and does not make the entire piecewise expression syntactically or semantically invalid.

## Set Definitions and Piecewise Functions

Sets may be used to define reusable conditions for piecewise expressions.

For example:

A = {x in R : x < 0}

f(x) = {x^2 if x in A, x if x >= 0}

The dependency system must recognize that the function depends on `A`.

Changing `A` therefore invalidates or recomputes the affected function automatically.
