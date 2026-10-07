# Expressions

An expression represents a real number which depends on zero or more free variables. 

For the sake of defining expressions here, we will introduce some mathematical notation to make our discussion much cleaner; if `H` is an expression or condition, then define `F(H)` to be the mathematical set of free variables in `H`. This notation is not to be used in the UI.

Expressions are defined inductively as follows

- Any built-in constant is an expression with 0 free variables
- Any real number is an expression with 0 free variables
- A user defined variable is an expression with 0 free variables
- A variable `x` which has no definition is an expression with 1 free variable. Namely, `F(x)={x}`
- If `E` is an expression with `n` many free variables, then so is `(E)`
- If `E_1,...,E_n` are expressions and `G` is either an `n`-ary built in function or an expression with `F(G)={x_1,...,x_n}`, then `G(E_1,...,E_n)` is an expression with `F(G)=F(E_1)∪...∪F(E_n)`
- If `E` is an expression with `F(E)={x_1,...,x_n}` and `P(x_1,...,x_n)` is a condition with the same free variables, then `E if P(x_1,...,x_n)` is an expression with the same free variables as `E`
- If `E_1,...,E_n,E` are expressions and `P_1,...,P_n` are conditions with each `F(E_i)=F(P_i)`, then `G={E_1 if P_i,..., E_n if P_n, E}` is an expression with `F(G)=F(E_1)∪...∪F(E_n)∪F(E)`

---

