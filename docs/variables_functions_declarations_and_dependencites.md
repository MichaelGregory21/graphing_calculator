# Variables, Functions, Declarations, and Dependencies

## Definitions
A definition associates a name with a mathematical expression or function.

Examples:
 - a <- 5
 - b <- a + 1
 - f(x) <- x^2 + b

Definitions are stored in a shared mathematical environment.

A name may have only one definition. Duplicate definitions are errors.

## Variables

A variable definition contains:
 - a name
 - an expression
 - optional domain metadata

For example:

a <- 5

defines a as the value of 5.

Variables are evaluated using the definitions available in the mathematical environment.

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
<- explicitly creates a definition without requesting visualization.

= may create a definition or describe a graphable mathematical relationship, depending on its left-hand side.

For example:

f(x) = x^2

creates a function definition and a graphable function.

x^2 + y^2 = 25

does not create a named definition; it describes an implicit graph.

The parser identifies the syntactic form, while semantic analysis determines its meaning.

## Symbol Resolution

When evaluating a definition, a symbol is resolved against the current scope.

For a function body:
 - function parameters are checked first;
 - user-defined symbols are checked next;
 - built-in constants and functions are checked as appropriate.

An unresolved symbol produces a missing-definition error.

## Dependencies

Definitions form a directed dependency graph.

For:
a <- 5
b <- a + 1
f(x) <- x + b

the dependency relationships are:

a → b → f

A definition is affected whenever one of its dependencies changes.

Domain bounds participate in the same dependency system.

## Circular Dependencies

Circular dependencies between definitions are invalid.

For example:
a <- b + 1
b <- a + 1

creates a cycle and cannot be evaluated.

Recursive function definitions are permitted. They are not treated as circular variable dependencies; runtime recursion is limited by a fixed maximum call depth.

## Graphability
A definition may be mathematically valid without being directly graphable.

For example:

f(x, a) = x^2 + a

is a valid function definition, but it has an additional free parameter and therefore cannot be displayed as an ordinary one-variable graph without further information.

Graphability is determined from the resolved mathematical model rather than from the declaration syntax alone.
is a valid function definition, but it has an additional free parameter and therefore cannot be displayed as an ordinary one-variable graph without further information.

Graphability is determined from the resolved mathematical model rather than from the declaration syntax alone.
