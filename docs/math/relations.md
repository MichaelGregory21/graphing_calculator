# Relations
A relation is a named convention involving two or more real numbers. Formally, it is a set of pairs of real numbers from the same set.

---

## 1. Syntax and User Defined Relations
A relation is any set of pairs of the form `L = {(a, b) in A * A :  P(a,b)}` for any set `A` of dimension 1 and any condition `P`.

Therefore, any such set defined by the user may be treated as a relation.

Now, for any relation `L`, the shorthand `a L b` to mean `(a, b) in L` is allowed. This is to reflect common notation around relations.

---

## 2. Built in Relations
The built-in relations are as follows:
1. Equality Relations
- `=`
- `!=`
2. Order Relations
- `<`
- `>`
- `<=`
- `>=`

For consistency, built-in relations may be thought of as predefined sets of tuples, just like user-defined relations. Internally, however, built-in relations are evaluated directly rather than being stored as ordinary sets.

This is reflected in the definitions of these relations as sets as they are not obtainable within the definition of a set in this system

### Equality Relations
The equality relations compare two values and determine whether they represent the same object. 

`=` is the set of pairs of real numbers that are the same.

`!=` is the set of pairs of real numbers which are not the same.

The relation `!=` renders as `≠`

## Order relations
Order relations compare two values according to their relative size.

`<` is the set of pairs of real numbers `(a, b)` where `a` is smaller than `b` and `<=` is the set of pairs of real numbers `(a, b)` where either `a` and `b` are the same or `a` is smaller than `b`.

`>` is the set of pairs of real numbers `(a, b)` where `b` is smaller than `a` and `>=` is the set of pairs of real numbers `(a, b)` where either `a` and `b` are the same or `b` is smaller than `a`.

The relation `<=` renders as `≤` and the relation `>=` renders as `≥`.

---

# Philosophy

For the discussion around relations, the consideration of whether a relation is `true` or `false` is reserved for `conditions`. Apart from this, the only difference between relations and condition is the `in` keyword which is notably not a relation since its components are not both real numbers and defining it as a set of pairs would introduce a notion of a set containing sets.

It is additionally worth noting that relations are only defined on sets of dimension 1 and relations on tuples are never defined. This is to keep the idea of relation definition quite simple, not only to parse, but also to understand. Within the context of this project, sets with higher dimension than 1 are not common save for some occasional domain definition for multivariable functions. If the user desperately needed user definable relations on high dimensional sets, then an `in` condition could be used with the thing left behind being the clean syntax.
