# Numerical Evaluation

## Purpose

The numerical evaluation system evaluates the mathematical expression tree for concrete variable values.

It must support:

- double-precision floating-point arithmetic;
- variables and user-defined functions;
- arbitrary function arity;
- recursive function calls;
- piecewise expressions and conditional expressions;
- domain-aware evaluation where required;
- the configured angle mode for trigonometric functions.

The evaluator operates on the semantic expression tree produced by semantic analysis. It does not perform parsing or symbolic manipulation.

## Evaluation Model

Evaluation is recursive over the expression tree.

Each evaluation receives an environment containing:

- values for independent variables;
- local parameter bindings for function calls;
- access to resolved user-defined definitions.

Operators evaluate their operands and then apply the corresponding numerical operation.

Function calls:

1 evaluate each argument;
2. create a local parameter environment;
3. evaluate the function body;
4. return the resulting value.

Function calls may recurse, but recursion is limited by a fixed maximum call depth.

## 3. Numerical Results

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

## Piecewise Evaluation

Piecewise branches are evaluated in order.

For each branch:

1. evaluate its condition;
2. if the condition is true, evaluate and return its expression;
3. otherwise continue to the next branch.

The first applicable branch wins.

A final branch without a condition acts as the default branch.

## Domain Interaction

A domain is evaluated separately from the mathematical expression itself.

When a domain restriction is relevant, the evaluator must determine whether the supplied variable values satisfy the domain before evaluating the expression.

Domain bounds are themselves expression trees and therefore use the same evaluation mechanism.

A value may therefore be:

1. outside the declared domain;
2. inside the domain but numerically undefined;
3. inside the domain and numerically defined.

These cases remain distinct.

## Numerical Stability

The evaluator should avoid unnecessary intermediate transformations and preserve the direct mathematical structure of the expression tree.

No general symbolic simplification is performed during numerical evaluation.

Operations should use Java's standard `double` arithmetic unless a specific numerical algorithm requires otherwise.
