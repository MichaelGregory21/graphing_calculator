# Expressions

An expression represents a real number which depends on zero or more free variables. 

For the sake of defining expressions here, we will introduce some mathematical notation to make our discussion much cleaner; if `A` is a condition, expression, or set then define `F(A)` to be the mathematical set of free variables in `A`. This notation is not to be used in the UI.

Expressions are defined inductively as follows

- Any real number, tuple, or built-in constant is an expression with no free variables
- If `E` is an expression with `n` many free variables, then so is `(E)`
- A free variable `x` is an expression with `F(x) = {x}`
- A tuple `(a_1,...,a_n)` is an expression whose free variables are `F(a_1) ∪ ... ∪ F(a_n)`
- A if `t` is a tuple and `i` is an expression then `t[i]` is an expression whose free variables are `F(t) ∪ F(i)`
- If `E_1,...,E_n` are expressions, `G` is an `n`-ary function, then `H = G(E_1,...,E_n)` is an expression with `F(H) = F(E_1) ∪ ... ∪ F(E_n)`
- If `E` is an expression with `F(E) = {x_1,...,x_n}` and `P(x_1,...,x_n)` is a condition with the same free variables, then `E if P(x_1,...,x_n)` is an expression with the same free variables as `E`
- If `E_1,...,E_n,E` are expressions and `P_1,...,P_n` are conditions with each `F(E_i) = F(P_i)`, then `G = {E_1 if P_1,..., E_n if P_n, E}` is an expression with `F(G) = F(E_1) ∪ ... ∪ F(E_n) ∪ F(E)`.

Expressions with 0, 1, 2 free variables are called *constants*, *unary*, and *binary* expressions, respectively

---

## 1. Built-in Operations and Functions

An operation is a binary function which is required to be presented in *infix* notation. For example, addition is written `x + y`, not `+(x,y)`. The built-in operations are:
- Addition `+`
- Subtraction `-`
- Multiplication `*`
- Division `/`
- Exponentiation `^`

The built-in expressions are:
- Trigonometry `sin`, `cos`, `tan`, `csc`, `sec`, `cot`
- Inverse Trigonometry `asin`, `acos`, `atan`, `acsc`, `asec`, `acot`
- Logarithm `log`, `ln`
- Integer Functions `round`, `floor`, `ceil`
- Min/Max `min`, `max`
- Square & Cube Roots `sqrt`, `cbrt`
- Absolute Value `abs`

## 2. Piecewise Expressions

A piecewise expression is an expression whose value is determined by a collection of conditions, with different expressions used on different regions of its domain. The general syntax is `{E_1 if P_1,..., E_n if P_n, E}` where `E_1,...E_n` and `E` are expressions and `P_1,...,P_n` are conditions. The expression take the form of `E_i` whenever `i` is the smallest number between `1,...,n` such that its input satisfies `P_i`. If an input satisfies none of `P_1,...,P_n`, then the function takes the value of `E` if `E` is provided. If `E` is not provided, then the piecewise function is not defined here.

## 3. Declaring Expressions

A constant expression may be evaluated by entering it into an expression box. For example, `2 + 2` prints `4`. An expression can be defined by a variable name and evaluated later by supplying values for its free variables. This definition may use either `<-` or `=` interchangeably.
