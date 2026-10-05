# Variables, Functions, Declarations, and Dependencies

## Definitions

A definition associates a name with a mathematical expression, function, or set.

Examples:
 - a <- 5
 - b <- a + 1
 - f(x) <- x^2 + b
 - A <- {x in R : x < 0}

Definitions are stored in a shared mathematical environment.

A name may have only one definition. Duplicate definitions are errors.

## Variables

A variable definition contains:
 - a name
 - an expression

For example:

a <- 5

defines `a` as the value of 5.

A variable may also contain a set value:

A <- {0, 1, 2, 3}

defines `A` as a finite set.

Variables are evaluated using the definitions available in the mathematical environment.

The mathematical type of a definition is determined from its resolved expression. A definition may therefore represent a scalar, set, or another supported mathematical value.

## Sets

Sets are ordinary mathematical values and are defined using the same declaration mechanism as other named values.

For example:

A = {x in R : x < 0}

defines `A` as the set of real numbers less than zero.

A set definition may depend on other definitions:

a = 5
A = {x in R : x < a}

The dependency is:

a → A

Set definitions may subsequently be used in membership expressions:

x^2 if x in A

Named sets are therefore not special domain objects. They are ordinary definitions whose values happen to be sets.

## Functions

A function definition contains:
 - a name
 - an ordered list of local parameters
 - an expression body

For example:

f(x, a) <- x^2 + a

defines a two-parameter function.

Parameters are local to the function body and take precedence over definitions with the same name outside the function.

User-defined functions may call other user-defined functions, including themselves.

## Declaration Operators

`<-` explicitly creates a definition without requesting visualization.

For example:

a <- 5

A <- {x in R : x < 0}

f(x) <- x^2 + a

`=` may create a definition or describe a graphable mathematical relationship, depending on its left-hand side.

For example:

f(x) = x^2

creates a function definition and a graphable function.

A set definition:

A = {x in R : x < 0}

creates a named set definition.

An equation:

x^2 + y^2 = 25

does not create a named definition; it describes an implicit graph.

The parser identifies the syntactic form, while semantic analysis determines its meaning.

## Symbol Resolution

When evaluating a definition, a symbol is resolved against the current scope.

For a function body:
 - function parameters are checked first;
 - local set-comprehension variables are checked when applicable;
 - user-defined symbols are checked next;
 - built-in sets, constants, and functions are checked as appropriate.

An unresolved symbol produces a missing-definition error.

## Set-Comprehension Variables

A set comprehension introduces a local variable.

For:

A = {x in R : x^2 > 10}

the `x` used by the predicate is local to the set comprehension.

The outer environment's `x`, if one exists, is not used for that binding.

Set-comprehension scope is therefore analogous to function-parameter scope for dependency and name-resolution purposes.

## Dependencies

Definitions form a directed dependency graph.

For:

a <- 5
b <- a + 1
A <- {x in R : x < b}
f(x) <- x + b

the dependency relationships are:

a → b → A
    ↘ f

A definition is affected whenever one of its dependencies changes.

Set definitions participate in the same dependency system as scalar variables and functions.

Restrictions expressed through conditional expressions also create dependencies.

For:

A <- {x in R : x < a}
f(x) <- x^2 if x in A

the dependency relationships include:

a → A → f

## Circular Dependencies

Circular dependencies between definitions are invalid.

For example:

a <- b + 1
b <- a + 1

creates a cycle and cannot be evaluated.

The same rule applies to set definitions:

A <- {x in R : x in B}
B <- {x in R : x in A}

creates a dependency cycle.

Recursive function definitions are permitted. They are not treated as circular variable dependencies; runtime recursion is limited by a fixed maximum call depth.

## Graphability

A definition may be mathematically valid without being directly graphable.

For example:

f(x, a) = x^2 + a

is a valid function definition, but it has an additional free parameter and therefore cannot be displayed as an ordinary one-variable graph without further information.

Likewise, a set definition may be valid without being directly visualizable.

Graphability is determined from the resolved mathematical model rather than from the declaration syntax alone.

## Set Construction and Evaluation

The mathematical environment does not require every set to be explicitly enumerated.

Finite sets can be evaluated directly:

A = {0, 1, 2, 3}

Set-builder expressions may represent potentially infinite sets:

A = {x in R : x < 0}

The evaluator should use the representation most appropriate to the operation being performed.

The implementation is not required to symbolically enumerate arbitrary infinite sets. Operations that cannot be resolved within the supported mathematical model or computational budget produce an appropriate evaluation result.

## Definition Changes

When a definition changes, dependent definitions and graph expressions should be invalidated or recomputed according to the dependency graph.

For example, changing:

a <- 5

to:

a <- 10

should automatically update:

A <- {x in R : x < a}

and any graph using:

x^2 if x in A

without requiring the user to manually reconfigure a domain.
