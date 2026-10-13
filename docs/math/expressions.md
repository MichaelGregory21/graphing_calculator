# Expressions

An expression represents a real number which depends on zero or more free variables. An expression comes equipped with a dimension component which restricts some operations to maintain well-definedness.

For the sake of defining expressions here, we will introduce some mathematical notation to make our discussion much cleaner; if `A` is a condition, expression, or set then define `F(A)` to be the mathematical set of free variables in `A`. This notation is not to be used in the UI.

Expressions are defined inductively as follows

- Any real number, tuple, or built-in constant is an expression with no free variables. The dimension of a number is 1. The dimension of a tuple is the size of that tuple
- If `E` is an expression with `n` many free variables, then so is `(E)`. The dimension of `(E)` is the same as that of `E`
- A free variable `x` is an expression with `F(x) = {x}` and dimension 1
- A tuple `(a_1,...,a_n)` is an expression whose free variables are `F(a_1) ∪ ... ∪ F(a_n)` and whose dimension is `n`
- If `E_1,...,E_n` are expressions, all of the same dimension, `G` is an `n`-ary function, then `H = G(E_1,...,E_n)` is an expression with `F(H) = F(E_1) ∪ ... ∪ F(E_n)` and the same dimension
- If `E` is an expression, then `-E` is an expression with `F(-E) = F(E)` and the same dimension as that of `-E`
- A if `t` is a tuple and `i` is an expression then `t[i]` is an expression whose free variables are `F(t) ∪ F(i)` and whose dimension is 1
- If `E` is an expression with `F(E) = {x_1,...,x_n}` and `P(x_1,...,x_n)` is a condition with the same free variables, then `E if P(x_1,...,x_n)` is an expression with the same free variables as `E` and the same dimension as `E`
- If `E_1,...,E_n,E` are expressions, all of the same dimension, and `P_1,...,P_n` are conditions with each `F(E_i) = F(P_i)`, then `G = {E_1 if P_1,..., E_n if P_n, E}` is an expression with `F(G) = F(E_1) ∪ ... ∪ F(E_n) ∪ F(E)` and the same dimension
- 
---

## 2. Piecewise Expressions

A piecewise expression is an expression whose value is determined by a collection of conditions, with different expressions used on different regions of its domain. The general syntax is `{E_1 if P_1,..., E_n if P_n, E}` where `E_1,...E_n` and `E` are expressions and `P_1,...,P_n` are conditions. The expression take the form of `E_i` whenever `i` is the smallest number between `1,...,n` such that its input satisfies `P_i`. If an input satisfies none of `P_1,...,P_n`, then the function takes the value of `E` if `E` is provided. If `E` is not provided, then the piecewise function is not defined here.

## 3. Expression Dimension

The dimension of an expression is stored to ensure that well-definedness is maintained. This is particularly relevant when plugging an expression into a tuple or free variable. For example, `x + y` may be plugged into a free variable but `(x, y)` may not.

Additionally, some built-in operations are defined on expressions of a particular dimension. For example, 2-tuple addition is only defined on expressions of dimension 2.

## 4. Declaring Expressions

A constant expression may be evaluated by entering it into an expression box. For example, `2 + 2` prints `4`. An expression can be defined by a variable name and evaluated later by supplying values for its free variables. This definition may use either `<-` or `=` interchangeably.
