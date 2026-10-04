# Piecewise Functions

## Piecewise Syntax

A piecewise expression is written using braces containing one or more conditional branches:

{x^2 if x < 4, 8x - 16 if 4 <= x < 8, 48}


This represents:

 - `x^2` when `x < 4`
 - `8x - 16` when `4 <= x < 8`
 - `48` otherwise

The final branch may omit its condition and acts as the default case.

## `if` Expressions

`if` introduces a condition for an expression.

For example:

x if x < 4

represents `x` restricted to the region where `x < 4`.

This provides a compact way to express simple conditional restrictions without constructing a full piecewise expression.

## Branch Evaluation

Branches are evaluated in order.

The first branch whose condition is true determines the result. Therefore, overlapping conditions are permitted, with earlier branches taking precedence.

A branch without a condition is always applicable and should normally be the final branch.

## Braces

Braces have mathematical meaning and are not general-purpose expression-layer controls.

When the user types `{` or `}`, they are interpreted as literal mathematical braces.

The editor may internally use a separate representation for its structural expression layers, but users cannot directly create arbitrary expression layers with braces.

For example:

sin

creates an empty argument layer for the `sin` function.

Typing:

sin{}

does not enter that existing layer. Instead, the braces represent a piecewise expression with an empty body, subject to the normal rules for piecewise syntax.

This distinction prevents the user-facing language from exposing the editor's internal layer structure.

## Nested Piecewise Expressions

Piecewise expressions may occur anywhere an ordinary expression is valid.

For example:

sin{{x if x < 0, -x} if x >= 0, 0}

Nested braces are therefore interpreted according to the piecewise grammar rather than as editor navigation commands.

## Domain Interaction

A conditional expression can provide a simple expression-level restriction:

x if x < 4

More complex or reusable domains are represented by the separate domain model.
