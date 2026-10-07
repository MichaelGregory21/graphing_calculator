# Variables

Variables are symbols used to represent mathematical objects. Depending on the context, a variable may refer to a fixed value, a function, a set, a condition, or a placeholder whose value is supplied later.

The system distinguishes between notions global variables, free variables, and bound variables. Understanding these concepts is essential for understanding expressions, conditions, sets, and functions.
---

## 1. Naming conventions

A variable is given a letter name, upper or lower case, e.g. `g, A, b, x`. For further specificity, a user may attach a subscript to a variable which may be a string consisting of a mixed sequence of uppercase letters, lowercase letters, and numerals, but no symbols are allowed in subscripts. 

Subscripts of variables do not adhere to the rendering process built in to the main expression system. For example, `x_pi` renders thusly and does not render as `x_π`.

Examples: 
- `x_1`
- `x_2`
- `A_FINANCE2026`
- `f_TEMP`

Subscripts may not have their own subscripts.

Outside of the context of a subscript, itself, the letter `e` without a subscript is reserved for the system constant.

---

## 2. Global Variables

A global variable is a variable that may be referenced from anywhere after it has been defined. 

Examples:
- `a = 3`
- `A = {x in Z : x < 0}`
- `P(x, y) <- x < y`
- `f(x) = x^2`

In these definitions, the names `a`, `A`, `P`, and `f` are, respectively, global variables.

Global variables may represent values, tuples, expressions, sets, conditions, functions, or other system objects.

The system also provides several built-in global variables. These are:
- `x`
- `y`
- `e`
- `π`

## 3. Free Variables

A free variable is a placeholder whose value is supplied when a definition is referenced or evaluated.

Unlike global variables, free variables do not have their own definitions. Instead, they are introduced by a surrounding definition.

Examples:
- `f(x) = x`
- `f(y) = 3 + y if 3 < y`
- `P(a, b) <- a < b`
- `A(i) = {x in N : x < i}`

In these examples, `x`, `y`, `a,b`, and `i` are, repsectively, free variables.

A free variable may only appear within a definition that introduces it. For example, `f(x) = x + a` throws a definition error if `a` is not a global variable.

## Lots of Examples

# Philosophy
