# Expressions

An expression represents a real number which depends on zero or more free variables. 

For the sake of defining expressions here, we will introduce some mathematical notation to make our discussion much cleaner; if `A` is a condition, expression, or set then define `F(A)` to be the mathematical set of free variables in `A`. This notation is not to be used in the UI.

Expressions are defined inductively as follows

- Any real number, tuple, or built-in constant is an expression with no free variables
- If `E` is an expression with `n` many free variables, then so is `(E)`
- A free variable `x` is an expression with `F(x)={x}`
- A tuple `(a_1,...,a_n)` is an expression whose free variables are `F(a_1) ∪ ... ∪ F(a_n)`
- A if `t` is a tuple and `i` is an expression then `t[i]` is an expression whose free variables are `F(t) ∪ F(i)`
- If `E_1,...,E_n` are expressions, `G` is an `n`-ary expression, then `H = G(E_1,...,E_n)` is an expression with `F(H)=F(E_1) ∪ ... ∪ F(E_n)`
- If `E` is an expression with `F(E)={x_1,...,x_n}` and `P(x_1,...,x_n)` is a condition with the same free variables, then `E if P(x_1,...,x_n)` is an expression with the same free variables as `E`
- If `E_1,...,E_n,E` are expressions and `P_1,...,P_n` are conditions with each `F(E_i)=F(P_i)`, then `G={E_1 if P_i,..., E_n if P_n, E}` is an expression with `F(G)=F(E_1)∪...∪F(E_n)∪F(E)`

---

