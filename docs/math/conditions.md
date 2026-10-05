# Conditions

A condition is formally a pair of expressions, called *components* separated by a relation symbol which holds a true or false value determined by the value of its components. Conditions are used to specify elements in a set and to determine a portion of a piecewise-defined function.

Equality, inequality, and membership are the three types conditions in this project.

---

## 1. Built in Conditions

The six built-in relations are separated into three classes:
1. Equality: `=`
2. Inequality: `<`, `>`, `<=`, `>=`
3. Membership `in`

These relation symbols separate expressions called *components*. For example:
- x^2 + 1 = 3
- 4 < 2
- a in {1, 2}

We can specify the components as *left* and *right* components. Note that the second example is `false` while the first and third examples depend on the values for `x` and `a`, respectively.

A relation symbol must separate expressions. A list of 2 or more expressions is often acceptable. In this case, the entire condition is `true` only if each condition is pairwise `true`. For example:
- `{1, 2, 3} = {3, 2, 1} = {3, 2, 1, 1}`
- `2 < 4 = 2 + 2`
- `18 = 9 * 2 = 3 * 3 * 2`

are each true while
- `1 < 3 < 2`
- `sqrt 9 = 2`
  
are each false.

It is important that the components of an equality relation or of an inequality relation are the same type. Otherwise, an error is thrown. For example: `2 = (2, 3)` throws an error.

### Equality

Equality is the most basic relation which evaluates to `true` only when both components are, by definition, the same. For example:
- `3 = 3` -> `true`
- `4 = 2 * 1` -> `false`
- `(1, 2) = (0 + 1, 1 + 1)`
- `Z = N cup -N` -> `true`

Equality preceding a separator is used to denote definition, *not* a condition. For example:
`f(x)=x^2 if x = 4`.

In this case, the first equality defines `f(x)=x^2` and the second equality defines a domain of `{4}`.

### Inequality

Inequality is a relation defined only on numbers. In particular, inequality is not defined on sets or tuples. The four built-in inequalities are:
- `<`
- `>`
- `<=`
- `>=`

Note that `<=` is rendered as `≤` and `>=` is rendered as `≥`.

`inf` is defined by `a < inf` and `-inf < a` for all numbers `a`.

### Membership

Membership is the only relation whose components maybe be of different type. In particular, the left component must be a number or a tuple while the right component must be a set. For example: `2 in 3` is invalid but `-3 in N` is valid, though false.

The membership relation is true if and only if the left component is a member of the right component.

## 2. Logical Connectives

Logical connectives are means by which the user may join conditions in a specified manner to create new conditions. The three connectives are:
- `and`
- `or`
- `not`

The first two connectives called *conjunction* and *disjunction*, respectively, are binary connectives and separate a pair of conditions. For example:
- `x < 3 and x in N`
- `2 = 1 + 1 or 3 / 4 in Z`

The last connective, called *negation*, precedes a single condition. For example:
- `not x < 4`

The order of connectives follows the PNOA rule: Brackets evaluate first, then negation, then disjunction, then conjunction. For example:
`not 2 - 1 = 1 and 3 in N or ((3, 2) = (2 + 1, 2) and 3.5 in N)`

checks the following order:
1. `((3, 2) = (2 + 1, 2) and 3.5 in N)` -> `false`
2. `not 2 - 1 = 1` -> `false`
3. `3 in N or (false)` -> `true`
4. `false and true` -> `false`

So the whole statement is false.

### Conjunction and Disjunction

The conjunction and disjunction connectives join conditions to create new conditions that hold `true` or `false` value according to the following rules:
- `P and Q` is `true` if and only if both `P` and `Q` are `true`
- `P or Q` is `true` if and only if `P` is `true`, `Q` is `true`, or both `P` and `Q` are `true`

For example: `2 = 1 + 1 and 3 < 2` and `2 - 1 = 0 or 3.14 in N` are both `false` while `-2 in Z - N and 3 * 3 = 9` and `4 - 0 = 5 or (1, 2) = (3 - 2, 2)` are both `true`.

## Negation

The negation connective takes a single condition and creates a new condition that holds `true` or `false` value according to the following rule:
`not P` is `true` if and only if `P` is false

For example: `not 2 = 1` is `true` while `not 3 in Z` is `false`.


### Parentheses

Parentheses may be used to define specific ordering on connectives or make conditions visually appealing. Parentheses are not necessary to apply condition as argument to a connective though they are optional, e.g., `not P` is the same as `not(P)`. As is according to PNOA, parentheses take highest priority when evaluating a condition.

## 3. Free Variables


## 4. Separators (if, :)






