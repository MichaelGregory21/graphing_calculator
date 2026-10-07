# Variables

Variables are symbols used to represent mathematical objects called *definitions*. Depending on the context, a variable may refer to a function, a set, a condition, another variable, or a placeholder whose value is supplied later.

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

## 2. Types of Variables

Every occurrence of a variable belongs to exactly one of the following categories
- Global Variables
- Free Variables
- Bound Variables

### Global Variables

A global variable is a named definition that may be referenced from anywhere after it has been defined. 

Examples:
- `a = 3`
- `A = {x in Z : x < 0}`
- `P(x, y) <- x < y`
- `f(x) = x^2`
- `b = e`
- `t = (1, 2)`

In these definitions, the names `a`, `A`, `P`, `f`, `b`, and `t` are, respectively, global variables.

For the sake of the discussion around variables, the notations `=` and `<-` are interchangeable when defining global variables and only makes a difference to the graph plotting system and when defining conditions. 

Global variables may represent values, tuples, expressions, sets, conditions, functions, or other system objects.

The system also provides several built-in global variables. These are:
- Independent and Dependent Variables: `x, y`
- Math Constants: `e, π`
- Built-In Sets: `N, Z, R`

Tuples of global variables may be given component-wise definitions and referenced individually later. For example, `(a, b) = (z, 3)` renames the global variable `z` to `a` and assigns `3` to a global variable, called `b`. Later, the values of `a` and `b` may be substituted individually, elsewhere.

### Free Variables

A free variable is a placeholder whose value is supplied when a definition is referenced or evaluated.

Unlike global variables, free variables do not have their own definitions. Instead, they are introduced by a surrounding definition.

A definition containing free variables must provide a list all free variables contained in the definition, separated by commas, enclosed by parentheses, and directly after the name of the definition.

Examples:
- `f(x) = x`
- `f(y) = 3 if 3 < y`
- `P(a, b) <- a < b`
- `A(i) = {(x, z) in N : x + z < i}`

In these examples, `x`, `y`, `a,b`, and `i` are, repsectively, free variables.

A free variable may only appear within a definition that introduces it. For example, `f(x) = x + a` throws a definition error if `a` is not a global variable. Simply `a + b` also throws a definition error if `a` and `b` are not global variables.

Free variables may *not* share the name of their definition. For example, `f(f) = f + 1` throws a definition error.

Note that a placeholder for a free variable may be present despite not appearing in the definition. Such free variables are called *unused*. For example, `f(x) = 3` and `f(x, y, z) = x - z` are both legal. In this case, substituting into the missing free variables has no effect.

### Bound Variables

Bound variables exist only in a comprehension definition of a set.

Examples:
- `A = {x in Z : x < 0}`
- `B(y) = {n in N : n < y}`
- `d = {(i, j) in R^2 : i < 2}`

In these examples, `x`, `n`, and `i,j` are, respectively, bound variables.

Bound variables are local to the construct that introduces them and may never be referenced directly outside of that context.

---

## 3. Scope

Free variables and bound variables exist only within the definitions that introduce them and may not be referenced elsewhere.

The priority order for interpreting variables follows the *BFG* rule. That is
- Bound variables take first priority
- Free variables take second priority
- Global variables take third priority

Examples:
- `f(x) = x + 2` treats `x` as a free variable
- `A = {e in Z : e < 3}` treats `e` as a bound variable
- `A(y) = {y in R : y in N}` treats `y` as a bound variable leaving the free variable of the same name unused

---

## 4. Substitution of Free Variables

A definition

When a definition containing free variables is referenced, values may be substituted for those free variables which are assigned according to the order to which they appeared in the definition. For example, given definition `f(x) = x + 2`, `3` may be substituted for `x`, denoted `f(3)` which returns a value of `5`.

Not all free variables are required to be substituted when later referenced. However, these variables remain free in the definition that the appear in. For example, `x` is free in the expression `f(x, 1)` where `f` is a previously defined function.

---

# Philosophy

Every variable occurrence in the system is either global, free, or bound. Global variables name definitions, free variables receive values through substitution, and bound variables are local to the construct that introduces them.
