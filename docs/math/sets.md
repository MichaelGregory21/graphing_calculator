# Sets

A set is an unordered collection of valid objects which may contain the graph of a function, act as a domain for a function, or exist as a standalone object within the system. A set comes equipped with a well-defined *dimension* and some set operations are restricted to maintain this well-definedness. Sets are defined inductively as follows:
- `∅` is a set of dimension 0
- `N`, `Z`, `R` are sets each of dimension 1
- Finite sets where every element is specified and has the same dimension are sets with the same dimension of any of its elements
- Discrete, infinite sets of dimension 1, defined by a recognizable pattern of integers are sets of dimension 1. Such sequences may be increasing, decreasing, or double sided
- If `A` is a set of dimension `n` and `P` is a condition, then `{(a_1,...,a_n) in A : P(a_1,...,a_n)}` is a set of dimension `n`
- If `A` and `B` are sets of dimension `n` and `m`, respectively, then `A*B` is a set of dimension `n+m`
- If `A` is a set of dimension `n` and `m` is a natural number, then `A^m` is a set of dimension `nm` defined inductively by `A^0=∅` and `A^{m+1}=A*A^m`
- If `A` and `B` are sets of the same dimension, `n`, then `A cup B`, `A cap B`, and `A - B` are sets which are either `∅`, or also of dimension `n`
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

## 2. Dimensions

The dimension of a set is obtained upon definition. If the system fails to calculate the dimension of a defined set, then an run time error is thrown.

The dimension of a set reflects the size of the tuples it contains. The empty set has dimension 0, a set containing real numbers has dimension 1, and a set containing `n`-tuples has dimension `n`.

By the restrictive nature of set definition, the type of a single element is enough to determine the dimension of a non-empty set.

The principle is built-in to the system to avoid issues with defining the domain of multi-variable functions. 

## 3. Finite Sets

## 4. Infinite Sets

## 5. Set Comprehension

## 6. Set Operations 

## 7. Scalar Operations on Sets

### Multiplication

-A

### Addition

## 7. Cartesian Products

### Powers

## 8. Plots Using Sets
