# Sets

A set is an unordered collection of expressions which may constitute the graph of a function, form a user-defined relation, act as a domain for a function, or exist as a standalone object. A set comes equipped with a *dimension* component which restricts some set operations to maintain well-definedness. Sets may also include free variables which may be later substitutes for values.

For the sake of defining sets here, we will introduce some mathematical notation to make our discussion much cleaner; if `A` is a condition, expression, or set, then define `F(A)` to be the mathematical set of free variables in `P`. This notation is not to be used in the UI.

Sets are defined inductively as follows:
- Built in sets are sets with no free variables
- Finite sets `A={a_1,...,a_n}`, where every element is specified and has the same size, are sets with the same dimension of any of its elements and `F(A) = F(a_1) ∪ ... ∪ F(a_n)`
- Discrete, infinite sets of dimension 1, defined by a recognizable pattern of integers are sets of dimension 1 with no free variables. Such sequences may be increasing, decreasing, or double sided
- If `A` is a set of dimension `n` and `P` is a condition, then `A={(a_1,...,a_n) in A : P(a_1,...,a_n,b_1,...,b_m)}` is a set of dimension `n` and `F(A) = F(P) \ (F(a_1) ∪ ... ∪ F(a_n))`
- If `A` and `B` are sets of dimension `n` and `m`, respectively, then `A*B` is a set of dimension `n+m` with `F(A*B) = F(A) ∪ F(B)`
- If `A` is a set of dimension `n` and `m` is a positive integer, then `A^m` is a set of dimension `nm` with `F(A^m)=F(A)`, defined inductively by `A^1=A` and `A^{m+1}=A*A^m`
- If `A` and `B` are sets of the same dimension, `n`, then `A cup B`, `A cap B`, and `A - B` are sets which are either `∅`, or also of dimension `n` with `F(A cup B) = F(A cap B) = F(A - B) = F(A) cup F(B)`
--- 

## 1. Built-in Sets

There are four built in sets in this system. Sets are defined in such a way that every set there is some dimension `n` such that it will truly be contained in one of `R^n`, `Z^n`, or `N^n`. In fact, since every natural number and integer is ultimately a real number, every user definable set is ultimately a subset of some power of the pseudo-universal set `R`.

### The Empty Set `∅`

The empty set is the only set of dimension 0 and is defined by the property that for any expression f, `f(x_1,...,x_n) in ∅` is constantly `false`. The syntax `{}` and `empty` render as `∅`.

### The Natural Numbers `N`

The set of natural numbers is denoted `N` and contains all counting numbers `0, 1, 2, ...`. Graphable functions defined on `N` will plot a series of discrete points.

Whole positive numbers including those with 0 decimal place are members of `N`. For example, `3 in N` and `3.0 in N` are both `true`.

### The Integers `Z`

The set of integers is the completion of `N` with respect to additive inverses. That is, `Z` consists of all positive natural numbers, and their negative counterparts.

Similar to `N`, Whole numbers including those with 0 decimal place are members of `Z`.

---

## 2. Dimensions

The dimension of a set is obtained upon definition. If the system fails to calculate the dimension of a set, then an run time error is thrown.

The dimension of a set reflects the size of the tuples it contains. The empty set has dimension 0, a set containing real numbers has dimension 1, and a set containing `n`-tuples has dimension `n`.

By the restrictive nature of set definition, the type of a single element is enough to determine the dimension of a non-empty set since the size of elements in a set will be uniform.

The principle is built-in to the system to avoid issues with defining the domain of multi-variable functions. 

---

## 3. Set Comprehension

Set comprehension definitions of a set follows the following syntax in general: `{(a_1,...,a_n) in A : P(a_1,...,a_n)}`. Here, `A` is a set of dimension `n`, called the *source set*, and `P` is a condition with exactly `n`-many free variables, called the *set condition*. The source set clause and the set condition must be separated by a separator, either `:` or `if`, and the entire set must be enclosed by braces. 

As shorthand, the source set clause may be omitted entirely, leaving a set of the form `{P(a_1,...,a_n)}` where `P` is a condition with exactly `n`-many free variables. This set is defined to be the same as `{(a_1,...,a_n) in R^n : P(a_1,...,a_n)}`.

The set condition may *not* reference the set that is being defined. For example, `E={n in N : n = 0 or n - 1 in E}` will throw a definition error.

If the size of the tuple presented is not the same as the dimension of the source set, then a definition error is thrown.

---

## 4. Cartesian Products

If `A` and `B` are sets of dimension `n` and `m`, respectively, then the Cartesian Product of `A` and `B`, denoted either as `A*B` or `AB` is the set whose elements are `(a_1,...,a_n,b_1,...,b_m)` where `(a_1,...,a_n)` comes from `A` and `(b_1,...,b_m)` comes from `B`, in that order. This set has dimension `n+m`, as is made evident by the size of the tuples therein.

Note that for any set `A`, it must be that `A*∅=∅`.

### Powers

If `n` is a positive integer, then the set `A^n` is defined inductively by `A^1=A` and `A^{n+1}=A*A^n`. 

---

## 5. Set Operations 

Set operations are operations on pairs of sets of the same dimension which creates new sets according to a particular rule set. The three built-in set operations are `cup`, `cap`, and `-`. The first two render as `∪` and `∩`, respectively.

Attempting to apply set operations to sets of differing dimension will throw a definition error.

Given two arbitrary sets, `A` and `B`, of the same dimension, the set operations are defined as follows:
- `A cup B = {a in A or a in B}`
- `A cap B = {a in A and a in B}`
- `A - B = {a in A and not a in B}`




