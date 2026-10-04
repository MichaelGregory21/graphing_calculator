# Product Definition and Scope
## Product
A polished, desktop 2D graphing calculator built entirely from scratch in Java.

The application is intended to be a genuine, standalone mathematical application rather than a demonstration or educational prototype. Its primary purpose is to provide a practical everyday graphing environment with a high-quality user interface, expressive mathematical input, responsive interaction, and mathematically intelligent rendering.

The application should feel closer to a modern commercial graphing application than to a traditional calculator or a programming-language REPL.


## Core Product Concept
The user interacts with mathematical expressions through a structured expression editor and a Cartesian graphing workspace.

Expressions can represent ordinary functions, equations, inequalities, variables, user-defined functions, piecewise functions, datasets, and other mathematical objects. The application interprets the user's input where possible rather than requiring the user to explicitly classify every expression.

The application maintains a live mathematical environment. Changes to variables or function definitions automatically propagate to dependent expressions and graphs.

The central design principle is: **The user describes the mathematics; the application determines how it can be represented and visualized.**

When interpretation is genuinely impossible, the application should provide a precise and localized error rather than silently producing an unexpected result.

## Primary Goals

### 1. High-quality graphing
The graphing engine should produce smooth, visually polished mathematical graphs across arbitrary zoom levels.

It must handle:
 - Explicit functions
 - Implicit equations
 - Inequalities
 - Multiple simultaneous graphs
 - Piecewise functions
 - Restricted domains
 - Discrete domains
 - Roots and intersections
 - Interactive points on graphs

Discontinuities and undefined values are particularly important. Expressions such as 1/x, sqrt(x), and log(x) should naturally produce gaps in their graphs rather than erroneous connecting lines.

### 2. Expressive mathematical input
The calculator should support natural mathematical notation without requiring users to learn a programming language.

Examples include:
 - juxtapositional multiplication
 - fractions
 - radicals
 - exponents
 - subscripts
 - standard mathematical functions
 - piecewise definitions
 - domain restrictions

The input editor should transform ordinary typed input into properly rendered mathematical notation as the user types.

