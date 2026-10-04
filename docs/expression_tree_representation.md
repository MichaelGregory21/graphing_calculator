# Expression Tree Representation

## Overview
Every mathematical expression is represented by a single tree.

Each node represents a mathematical construct and contains zero or more child nodes.

The tree is the authoritative representation of an expression; textual notation is only a representation of it.

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

The editor may treat these as separate layers, but they remain ordinary subtrees of the same expression tree.

For example:

sin(x + 1)

contains a Sin node whose child is the tree for x + 1.

## Parentheses

Parentheses used only for mathematical grouping do not require a dedicated tree node.

For example:

(x + 1)

has the same tree as:

x + 1

when the parentheses do not alter the expression's structure.

## Declarations
Declarations are represented as higher-level nodes containing the appropriate expression or definition.

For example:

f(x) = x^2

contains:
 - a function declaration target
 - parameter x
 - an expression tree for x^2
   
## Domains
A variable's domain is separate metadata associated with the variable or expression item.

Domain bounds themselves contain ordinary expression trees.

For:

0 < x < a + 1

the bounds 0 and a + 1 are represented as expression trees.

## Node Identity

Nodes may have stable identities so that editor selection, diagnostics, and other state can refer to a particular part of an expression as the tree changes.
