# Tuple

A tuple is an ordered sequence of expressions with a fixed and finite *size* parameter defined on declaration.

## 1. Size

Every tuple has a fixed finite size equal to the number of components it contains.

The size of a tuple must be at least 2. Individual expressions can be thought of as single element tuples though these maintain distinct type.

## 2. Variables in Tuples

The free variables of a tuple is equal to the union of all free variables among its components.

For example, the free variables in `(x + y - a, b + x)` are `x, y, a, b`, assuming these are not global variables.

## 3. Tuple Assignment

A tuple may be assigned to a global variable where the variable refers to the entire tuple. For example, `a = (1, 2)`.

Alternatively, a components of a tuple may be assigned individually using a *destructuring* assignment.

The general syntax of a destructuring assignment is `(a_1,...,a_n)=(b_1,...,b_n)` which assigns to each `b_i` the variable name `a_i`.

Note that a a destructuring assignment requires that the size of the tuple of variables and the tuple of definitions is the same. For example, `(a, b, c) <- (1, 2)` throws a syntax error.

## 4. Tuple Operations

Tuples of equal size may be added component-wise. For example, `(1, 2) + (-2, 5) = (-1, 7)`.

Tuple may also be multiplied by a scalar expression. For example, `x * (3, x^2) = (3x, x^3)`

## 5. Component Access

Individual Components of a tuple may be access using square bracket notation.

The general syntax of component access is `(a_1,...,a_n)[i]=a_i` where `1 <= i <= n`.

Note that the index must be between 1 and the size of the tuple. For example, `(0, 2)[3]` throws a definition error.

## 6. Tuples as Points

A tuple of size 2 is interpreted as a 2D point and may be plotted. For example, `(0, 0)` plots the origin.

A discrete set of tuples of size 2 is interpreted as a collection of points, all of which may be plotted. For example, `{(0, 0), (1, 1), (3,-2)}` plots these three points

A continuous set of points is interpreted as a implicit function and may be plotted in this way. For example, `{(x, y) in R^2 : x^2 + y^2 = 1}` represents the unit circle.

# Philosophy

A tuple is an ordered collection of expressions treated as a single object. Tuples provide the standard representation of points, vectors, higher-dimensional elements, and relations throughout the system. Variables, substitution, and scope behave inside tuples exactly as they do elsewhere in the language, making tuples a convenient way to work with multiple values without introducing new variable semantics.
