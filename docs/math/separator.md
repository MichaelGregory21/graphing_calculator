# Separators

Separators are symbols or keywords that divide one syntactic object from another. They do not represent values and are never evaluated directly. Instead, they indicate how expressions, conditions, sets, and other system constructs are organized. 

---

## 1. Rule Separators

The rule separators are `if` and `:` which are interchangeable and have identical meaning in all contexts. Their purpose is to separate an object from a rule, restriction, or defining condition.

Rule separators are used in exactly three specific contexts:
- Restricting the domain of a function, e.g., `f(x)=x + 1 if x in N` plots all positive natural numbers
- Defines a set using set comprehension notation, e.g. `{x in R: 0 < x < 1}` is the real interval strictly bounded between 0 and 1
- Defines piecewise expressions, e.g., `{x^2 if x < 10, x if x >= 10}` plots a parabola less than 10 with the identity function for x greater than or equal to 10

## 2. List Separators

The only list separator is the comma `,`. A comma separates elements of a list.

List separators are used in exactly five specific contexts:
- Separating items in a tuple, e.g., `(1, 2)`
- Separating function arguments, e.g., `f(x, y)`
- Separating items in a finite set with all elements specified, e.g,. `{1, 2, 3}`
- Separating items in an infinite list defined by a recognizable pattern, e.g., `{2, 4, 6, ...}`
- Separating parts of a piecewise defined expression, e.g,. `{x^2 if x < 10, x if x >= 10}`

# Philosophy

Separators make the structure of mathematical statements explicit. Rather than introducing new mathematical objects, separators indicate how existing objects are connected to one another.

