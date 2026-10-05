# Numerical Evaluation

## Purpose

The numerical evaluation system evaluates the mathematical expression tree for concrete variable values.

It must support:

- double-precision floating-point arithmetic;
- variables and user-defined functions;
- arbitrary function arity;
- recursive function calls;
- piecewise expressions;
- conditional expressions;
- set membership;
- set-valued expressions where required;
- the configured angle mode for trigonometric functions.

The evaluator operates on the semantic expression tree produced by semantic analysis. It does not perform parsing or symbolic manipulation.

## Evaluation Model

Evaluation is recursive over the expression tree.

Each evaluation receives an environment containing:

- values for independent variables;
- local parameter bindings for function calls;
- local bindings for set-comprehension variables;
- access to resolved user-defined definitions.

Operators evaluate their operands and then apply the corresponding numerical operation.

Function calls:

1. evaluate each argument;
2. create a local parameter environment;
3. evaluate the function body;
4. return the resulting value.

Function calls may recurse, but recursion is limited by a fixed maximum call depth.

## Numerical Results

Evaluation produces either:

- a finite double-precision value;
- an infinite value where the underlying operation produces one;
- an undefined result.

Undefined results are normal numerical outcomes rather than expression errors. Examples include:

- division by zero;
- square roots of negative values;
- logarithms outside their numerical domain;
- other operations that cannot produce a real-valued result.

The graphing system determines how undefined results affect visualization.

## Boolean Evaluation

Boolean expressions evaluate to a Boolean result.

Examples include:

x < 5

x >= 0

x in A

Boolean expressions are used by:
 - `if` expressions
 - piecewise branches
 - set-builder predicates
 - other Boolean constructs

## Set Evaluation

Set expressions evaluate to mathematical set values.

Finite enumerated sets may be represented explicitly.

For example:

{0, 1, 2, 3}

can be represented as a finite collection of evaluated elements.

Set-builder expressions may represent infinite sets and therefore do not necessarily enumerate their members.

For:

{x in R : x^2 > 10}

membership can be evaluated by:
1. evaluating the candidate value;
2. checking that the candidate belongs to the source set;
3. evaluating the predicate;
4. returning the Boolean result.

The evaluator should preserve a representation suitable for the set operation being performed rather than attempting to enumerate arbitrary infinite sets.

## Set Membership

For:

x in A

the evaluator:

1. evaluates `x`;
2. evaluates or resolves `A` as a set;
3. determines whether the resulting value belongs to `A`.

Membership in a set-builder expression evaluates the set's predicate for the candidate element.

For example:

{x in R : x < 0}

contains `-1` because:

- `-1` belongs to `R`;
- `-1 < 0` is true.

It does not contain `1` because:

- `1` belongs to `R`;
- `1 < 0` is false.

## Set Operations

Set intersection, union, difference, Cartesian product, powers, and scalar multiplication are evaluated according to their mathematical definitions.

For example:

A ∪ B

contains values that belong to `A` or `B`.

A ∩ B

contains values that belong to both.

A - B

contains values belonging to `A` but not `B`.

For a finite set `A`:

2A

contains the values obtained by multiplying every element of `A` by `2`.

The evaluator is not required to explicitly enumerate an infinite result.

## Set Powers

For a finite positive integer `n`:

A^n

represents the n-fold Cartesian product.

For example:

A^2

contains ordered pairs:

(a,b)

where:

a in A
b in A

The evaluator may represent such products lazily rather than explicitly enumerating all elements.

Infinite or unsupported powers produce an appropriate semantic or evaluation error.

## Conditional Evaluation

For:

x^2 if x in A

the evaluator:

1. evaluates the condition `x in A`;
2. if false, produces no applicable value;
3. if true, evaluates `x^2`.

The condition is therefore evaluated before the restricted expression.

A false conditional condition is not an expression error.

## Piecewise Evaluation

Piecewise branches are evaluated in order.

For each branch:

1. evaluate its condition;
2. if the condition is true, evaluate and return its expression;
3. otherwise continue to the next branch.

The first applicable branch wins.

A final branch without a condition acts as the default branch.

## Numerical Undefinedness

A value may be:

1. outside a conditional restriction;
2. inside the restriction but numerically undefined;
3. inside the restriction and numerically defined.

These cases remain distinct.

For example:

sqrt(x) if x in R

has a broad set restriction but remains numerically undefined for negative values.

Likewise:

1/x if x in R

is undefined at zero.

The evaluator should not turn numerical undefinedness into a semantic error.

## Dependency Interaction

Set definitions and ordinary definitions use the same resolved mathematical environment.

For example:

a = 5
A = {x in R : x < a}
f(x) = x^2 if x in A

evaluation of `f` depends on both `A` and `a`.

Changes to definitions are propagated by the dependency system before numerical evaluation is requested.

## Numerical Stability

The evaluator should avoid unnecessary intermediate transformations and preserve the direct mathematical structure of the expression tree.

No general symbolic simplification is performed during numerical evaluation.

Operations should use Java's standard `double` arithmetic unless a specific numerical algorithm requires otherwise.

## Computational Budgets

Set evaluation and numerical evaluation must have finite computational budgets where unrestricted computation is possible.

For example, the evaluator should not attempt to enumerate an infinite set indefinitely.

If an operation cannot be completed within its computational budget, it should return an appropriate evaluation result rather than blocking the application indefinitely.
