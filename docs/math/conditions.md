# Conditions
A condition is a defined rule which evaluates to one of `true`, `false`, or `unknown` once values are substituted for its free variables. Conditions are defined inductively as follows:
- If `f(a_1,...,a_n)` and `g(b_1,...,b_m)` are expressions and `R` is a relation, then `f(a_1,...,a_n) R g(b_1,...,b_m)` is a condition which holds `true` value for values of `(a_1,...,a_n)` in `A` and `(b_1,...,b_m)` in `B` where `(f(a_1,...,a_n), g(b_1,...,b_m))` is a member of `R`, and `false` for all other values
- If `f(a_1,...,a_n)` is an expression and `A` is a set, then `f(a_1,...,a_n) in A` is a condition which is `true` for values of `(a_1,...,a_n)` where `f(a_1,...,a_n)` is a member of `A`, and `false` for all other values
- If `P` is a condition, then so is `(P)`
- If `P` and `Q` are conditions, then `P and Q`, `P or Q`, and `not P` are conditions

---

## 1. Connectives

Connectives are operations on conditions which create new conditions from smaller conditions whose `true` or `false` value are according to a particular ruleset. The three built-in connectives are `and`, `or`, and `not`.

The `and` and `or` connectives are binary which create new conditions from a pair of existing conditions according to the following rules:
- `P and Q` is `true` only when `P` is `true` and `Q` is `true`
- `P or Q` is `true` only when `P` is `true`, `Q` is `true`, or both `P` and `Q` are `true`.

The `not` connective is unary and creates new conditions from a single existing condition according to the following rule:
- `not P` is true if `P` is `false`

---

## 3. Declaring Conditions

A condition with no free variables may be evaluated by entering it into an expression box. For example, `2 + 2 = 4` prints `true`. A condition can be declared under a variable name and evaluated later by supplying values for its free variables. Note that this declaration must use the `<-` notation and never use `=`. For example, `P(x, y) <- x + y < 3` is valid but `P(x, y) = x + y < 3` is not valid and will throw a syntax error.

A declaration may be substituted for a set condition or function domain restriction. For example, a set may be defined `{a in A : P(a)}` and a function may be defined `f(a) = a if P(a)`.

---

## 4. Evaluating Conditions

---

# Philosophy

Conditions are intentionally restrictive in their capabilities to avoid non-computable functions. In particular, users cannot create conditions using quantifiers. Additionally, the system is built with a fair amount of redundancy in mind. For example, connectives can be replaced with set operations and set membership conditions on user defined sets can always be avoided by carrying conditions through sets. The purpose of these redundancies is the reinforce an elegant system that feels like a true mathematical tool, rather than requiring the user to translate work into a restricted system.
