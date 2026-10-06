# Conditions
A condition is a defined rule by which a set can be defined or the domain of a function can be specified. A condition evaluates to one of `true`, `false`, or `unknown` once values are substituted for its free variables. They are defined inductively as follows:
- If `f(a_1,...,a_n) and g(b_1,...,b_m)` are expressions and `R` is a relation, then `f(a_1,...,a_n) R g(b_1,...,b_m)` is a condition
- If `P` and `Q` are conditions, then `P and Q`, `P or Q`, and `not P` are conditions

---

## 1. Connectives

Connectives are operations on conditions which create new conditions from smaller conditions whose `true` or `false` value are according to a particular ruleset. The three built-in connectives are `and`, `or`, and `not`.

### Conjunction and Disjunction

The `and` and `or` connectives are binary which create new conditions from a pair of existing conditions according to the following rules:
- `P and Q` is true if `P` is `true` and `Q` is `true`
- `P or Q` is true if `P` is `true`, `Q` is `true`, or both `P` and `Q` are `true`.

## Negation

The `not` connective is unary and creates new conditions from a a single existing condition according to the following rule:
- `not P` is true if `P` is `false`

---

## 2. Free Variables
In the context of a condition, a free variable is any variable which is not a user-defined variable. For example, the condition `2 + 2 = 4` has no free variables. On the other hand, if `a` and `b` are not user defined variables, then these are both free variables in the condition `a < b`.

When defining a set or restricting a function domain, free variables must be locally defined. For example, in the function `f(a, b) = ab if a + b < 3`, `a` and `b` are locally defined.

If a free variable is present in the definition of a set or the restriction of a function domain, then an illegal definition error is thrown. For example, `{a in N : a + b < 3}` throws and error if `b` is a free variable in `a + b < 3`.

---

## 3. Declaring Conditions

A condition with no free variables may be evaluated by entering it into an expression box. For example, `2 + 2 = 4` prints `true`. A condition can be declared under a variable name and evaluated later by supplying values for its free variables. Note that this declaration must use the `<-` notation and never use `=`. For example, `P(x, y) <- x + y < 3` is valid but `P(x, y) = x + y < 3` is not valid and will throw a syntax error.

A declaration may be substituted for set definition or function specification. For example, a set may be defined `{a in A : P(a)}`.


