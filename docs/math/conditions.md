# Conditions

A condition is formally a pair of expressions separated by a relation symbol which holds a true or false value determined by the value of its components. Conditions are used to specify elements in a set and to determine a portion of a piecewise-defined function.

Equality, inequality, and membership are the three types conditions in this project.

---

## 1. Built in Conditions

The six built-in relations are separated into three classes:
1. Equality: `=`
2. Inequality: `<`, `>`, `<=`, `>=`
3. Membership `in`

These relation symbols separate expressions. For example:
- x^2 + 1 = 3
- 4 < 2
- a in {1, 2}

Note that the second example is `false` while the first and third examples depend on the values for x and a, respectively.

### Equality

Equality is the most basic relation which evaluates to `true` only when both components are, by definition, the same. For example:
- `3 = 3`
- `{1, 2} = {1, 2, 2}`
- `2 in N`

All always evaluate to true, while
- `f(x) = 4`
- `(x, 3) = (2, 3)`
- `x > -10`

depend on the free variable `x`, and
- `1 = 5`
- `pi in Q`
- `2 < 0`

are always false.

## 2. Free Variables

## 3. Separators (if, :)






