# Conditions
A condition is a defined rule which evaluates to one of `true`, `false`, or `unknown` once values are substituted for its free variables. 

For the sake of defining conditions here, we will introduce some mathematical notation to make our discussion much cleaner; if `A` is a condition, expression, or set, then define `F(A)` to be the mathematical set of free variables in `P`. This notation is not to be used in the UI.

Conditions are defined inductively as follows:
- `true` and `false` are conditions with no free variables
-  If `f` and `g` are expressions and `L` is a relation, then `f L g` is a condition with `F(f L g) = F(f) ∪ F(g)`
- If `f` is an expression and `A` is a set, then `f in A` is a condition with `F(f in A) = F(f) ∪ F(A)`
- If `P` is a condition, then so is `(P)` with `F((P)) = F(P)`
- If `P` and `Q` are conditions, then `P and Q`, `P or Q`, and `not P` are conditions with `F(P and Q) = F(P or Q) = F(P) ∪ F(Q)` and `F(not P) = F(P)`

---

## 1. Connectives

Connectives are operations on conditions which create new conditions from smaller conditions whose `true` or `false` value are according to a particular ruleset. The three built-in connectives are `and`, `or`, and `not`.

The `and` and `or` connectives are binary which create new conditions from a pair of existing conditions according to the following rules:
- `P and Q` is `true` only when `P` is `true` and `Q` is `true`
- `P or Q` is `true` only when `P` is `true`, `Q` is `true`, or both `P` and `Q` are `true`.

The `not` connective is unary and creates new conditions from a single existing condition according to the following rule:
- `not P` is true if `P` is `false`

---

## 2. Declaring Conditions

A condition with no free variables may be evaluated by entering it into an expression box. For example, `2 + 2 = 4` prints `true`. A condition can be defined by a variable name and evaluated later by supplying values for its free variables. Note that this definition must use the `<-` notation and never use `=`. For example, `P(x, y) <- x + y < 3` is valid but `P(x, y) = x + y < 3` is not valid and will throw a syntax error.

---


# Philosophy

Conditions are intentionally restrictive in their capabilities to avoid non-computable functions. In particular, users cannot create conditions using quantifiers. Additionally, the system is built with a fair amount of redundancy in mind. For example, connectives can be replaced with set operations and set membership conditions on user defined sets can always be avoided by carrying conditions through sets. The purpose of these redundancies is the reinforce an elegant system that feels like a true mathematical tool, rather than requiring the user to translate work into a restricted system.
