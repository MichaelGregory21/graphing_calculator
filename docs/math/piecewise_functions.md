# Piecewise Functions

## 1. Piecewise Syntax

A piecewise expression is written using braces containing one or more conditional branches:

{x^2 if x < 4, 8x - 16 if 4 <= x < 8, 48}

This represents:

 - `x^2` when `x < 4`
 - `8x - 16` when `4 <= x < 8`
 - `48` otherwise

The final branch may omit its condition and acts as the default case.

More generally, a piecewise expression is formatted as follows

`{f_1(x) if P_1(x), f_2(x) if P_2(x), ..., f_n(x)}`

where `n` is an arbitrary number of conditions, `f_1,...,f_n` and `P_1,...,P_n` are functions and formulas, respectively, each of which may be defined elsewhere. Notice that the expression must be completely bound by braces and only the last case may be left without a condition to indicate the default case.

## 2. Branch Priority

Branches are prioritizes in the order that they are provided. Therefore, overlapping conditions are well-defined. For example:

`{0 if x > 0, 2 if x > 2}`

produces a constant plot of `0` for all `x > 0` whereas:

`{2 if x > 2, 0 if x > 0}`

produces a plot of `2` for `x > 2` and a plot of `0` for `0 < x <= 2`. 

The default case of a piecewise function applies to all real numbers not satisfying any of the provided conditions. For example:
`{0 if x in 2N, 1}`

may be expected to produce a parity function only defined on N, but it actually plots 1 almost everywhere except for countably many 0 points on non-negative integers. To produce a parity function, one would need to specify the default case as follows:
`{0 if x in 2N, 1 if x in 2N + 1}`

