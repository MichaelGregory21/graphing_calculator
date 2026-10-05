# Expression Tree Representation

## Overview

Every mathematical expression is represented by a single tree.

Each node represents a mathematical construct and contains zero or more child nodes.

The tree is the authoritative representation of an expression; textual notation is only a representation of it.

The tree must be capable of representing both scalar-valued expressions and set-valued expressions.

## Node Types

The tree contains nodes for:
 - numeric literals
 - constants
 - variables
 - unary operators
 - binary operators
 - function calls
 - user-defined function calls
 - piecewise expressions
 - conditional expressions
 - set enumeration
 - set comprehension
 - set membership
 - set intersection
 - set union
 - set difference
 - Cartesian products
 - set powers
 - set scaling
 - declarations
 - equations
 - inequalities

For example:

2x + sin(x)

is structurally equivalent to:

Add
├── Multiply
│   ├── Number(2)
│   └── Variable(x)
└── Sin
    └── Variable(x)

## Operators

Operators are represented explicitly rather than being inferred from their surrounding text.

For:

x + 2 * y

the tree is:

Add
├── Variable(x)
└── Multiply
    ├── Number(2)
    └── Variable(y)

Implicit and explicit multiplication therefore produce the same tree.

The same principle applies to set operations.

For:

A * A

the tree contains a CartesianProduct node whose children are the two expressions for `A`.

For:

2A

the tree contains a SetScale node containing the scalar expression `2` and the set expression `A`.

## Function Calls

A function node owns its argument expressions.

For:

f(x + 1)

the structure is:

FunctionCall(f)
└── Add
    ├── Variable(x)
    └── Number(1)

Functions with multiple arguments contain multiple ordered child expressions.

f(x, y + 1)

becomes:

FunctionCall(f)
├── Variable(x)
└── Add
    ├── Variable(y)
    └── Number(1)

## Structural Expression Layers

Some nodes contain editable child expression layers.

Examples include:
 - function arguments
 - radical contents
 - fraction numerator and denominator
 - piecewise branch expressions and conditions
 - set-builder element expressions and predicates
 - finite set elements

The editor may treat these as separate layers, but they remain ordinary subtrees of the same expression tree.

For example:

sin(x + 1)

contains a Sin node whose child is the tree for `x + 1`.

## Parentheses

Parentheses used only for mathematical grouping do not require a dedicated tree node.

For example:

(x + 1)

has the same tree as:

x + 1

when the parentheses do not alter the expression's structure.

## Sets

Sets are represented as ordinary expression-tree values.

### Finite Sets

For:

{0, 1, 2}

the tree may be represented as:

SetEnumeration
├── Number(0)
├── Number(1)
└── Number(2)

### Set Comprehensions

For:

{x in R : x^2 > 10}

the structure is:

SetComprehension
├── Domain
│   └── VariableSet(R)
└── Predicate
    └── GreaterThan
        ├── Power
        │   ├── Variable(x)
        │   └── Number(2)
        └── Number(10)

The bound variable `x` is local to the set comprehension.

The element expression may be implicit in the simple form where the bound variable itself represents the resulting set element.

More general set-builder expressions may represent an element transformation explicitly where supported.

### Set Membership

For:

x in A

the structure is:

Membership
├── Variable(x)
└── Variable(A)

Membership is a Boolean-valued expression.

### Set Operations

For:

A ∪ B

the tree contains a Union node.

For:

A ∩ B

the tree contains an Intersection node.

For:

A - B

the tree contains a SetDifference node.

For:

A * B

the tree contains a CartesianProduct node.

For:

A^2

the tree contains a SetPower node with the set expression `A` and exponent `2`.

For:

2A

the tree contains a SetScale node with scalar expression `2` and set expression `A`.

## Conditional Expressions

For:

x^2 if x in A

the tree contains a Conditional node:

Conditional
├── Expression
│   └── Power
│       ├── Variable(x)
│       └── Number(2)
└── Condition
    └── Membership
        ├── Variable(x)
        └── Variable(A)

This is fundamentally different from storing a separate domain object beside the expression.

The restriction is part of the mathematical expression itself.

## Declarations

Declarations are represented as higher-level nodes containing the appropriate expression or definition.

For example:

f(x) = x^2

contains:
 - a function declaration target
 - parameter x
 - an expression tree for x^2

A set declaration:

A = {x in R : x < 0}

contains:
 - a set declaration target
 - a set expression tree

The declaration node does not itself determine whether the result is graphable. Semantic analysis resolves the declaration into the appropriate mathematical model.

## Domains and Restrictions

The expression tree does not contain editor-owned domain metadata.

Restrictions that affect an expression are represented explicitly in the mathematical expression tree.

For example:

x^2 if x < 0

contains a Conditional node whose condition is:

x < 0

A reusable restriction:

A = {x in R : x < 0}
x^2 if x in A

contains a Membership node referring to the named set `A`.

Set-builder bounds and predicates are ordinary expression trees and therefore participate in symbol resolution and dependency analysis.

## Node Identity

Nodes may have stable identities so that editor selection, diagnostics, and other state can refer to a particular part of an expression as the tree changes.
