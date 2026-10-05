# Product Definition and Scope

## Product

A polished, desktop 2D graphing calculator built entirely from scratch in Java.

The application is intended to be a genuine, standalone mathematical application rather than a demonstration or educational prototype. Its primary purpose is to provide a practical everyday graphing environment with a high-quality user interface, expressive mathematical input, responsive interaction, and mathematically intelligent rendering.

The application should feel closer to a modern commercial graphing application than to a traditional calculator or a programming-language REPL.

## Core Product Concept

The user interacts with mathematical expressions through a structured expression editor and a Cartesian graphing workspace.

Expressions can represent ordinary functions, equations, inequalities, sets, variables, user-defined functions, piecewise functions, datasets, and other mathematical objects. The application interprets the user's input where possible rather than requiring the user to explicitly classify every expression.

The application maintains a live mathematical environment. Changes to variables, functions, or set definitions automatically propagate to dependent expressions and graphs.

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
 - Conditional expressions
 - Set-restricted expressions
 - Discrete sets
 - Roots and intersections
 - Interactive points on graphs

Discontinuities and undefined values are particularly important. Expressions such as `1/x`, `sqrt(x)`, and `log(x)` should naturally produce gaps in their graphs rather than erroneous connecting lines.

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
 - sets
 - set-builder notation
 - set membership
 - set intersection and union
 - set difference
 - set products
 - set scaling
 - conditional restrictions

The input editor should transform ordinary typed input into properly rendered mathematical notation as the user types.

The language should allow mathematical restrictions to be expressed directly rather than requiring a separate domain configuration system.

For example:

x^2 if x in {x in R : x < 0}

restricts the expression to the set of real values satisfying the stated condition.

The same mechanism supports reusable restrictions:

A = {x in R : x < 0}
x^2 if x in A

### 3. A structured mathematical editor

The expression editor is not intended to be a conventional text field.

Mathematical structures such as fractions, radicals, functions, exponents, subscripts, sets, and piecewise expressions contain nested editable regions.

The editor should therefore operate on a structured mathematical representation that allows the user to:
 - navigate between nested expressions
 - click directly into subexpressions
 - use Tab to move between editable regions
 - insert and remove structural operators
 - preserve mathematical formatting while editing
 - copy and paste mathematical expressions

### 4. Reactive mathematical environment

Variables, user-defined functions, and named sets form a dependency network.

For example, changing a variable should automatically update functions, sets, and graphs that depend upon it.

Undefined dependencies should produce localized errors while preserving the user's expression. Restoring the missing definition should automatically allow dependent expressions to recover.

Circular dependencies should be detected and reported, while intentionally recursive functions may be supported with a fixed recursion-depth limit.

Set definitions participate in the same dependency system as ordinary variable and function definitions.

For example:

A = {x in R : x < a}

depends on `a`, and an expression such as:

x^2 if x in A

depends on `A` and therefore indirectly on `a`.

### 5. Professional presentation

Visual quality is a first-class requirement.

The application should provide:

 - anti-aliased mathematical curves
 - smooth grid and axis rendering
 - clean mathematical typography
 - configurable graph styles
 - polished menus and controls
 - clear error messages
 - responsive interaction
 - carefully designed dialogs and settings
 - consistent visual hierarchy

The application should remain visually useful even when mathematical expressions are complicated.

## Mathematical Scope

### Supported initially

The language should support:
 - arithmetic
 - variables
 - user-defined functions
 - arbitrary-arity functions
 - implicit multiplication
 - standard operator precedence
 - exponentiation
 - fractions
 - radicals
 - trigonometric functions
 - inverse trigonometric functions
 - hyperbolic functions
 - inverse hyperbolic functions
 - logarithms
 - natural logarithms
 - exponentials
 - absolute value
 - floor and ceiling
 - minimum and maximum
 - sign
 - factorial
 - constants such as pi and e
 - equations
 - inequalities
 - piecewise functions
 - conditional expressions
 - sets
 - set-builder notation
 - finite set enumeration
 - set membership
 - set intersection and union
 - set difference
 - Cartesian/set products
 - finite powers of sets
 - scalar multiplication of sets
 - N, Z, Q, and R
 - recursive functions with a finite stack-depth limit

The numerical engine uses double-precision floating-point arithmetic.

Complex numbers are explicitly outside the scope of the calculator.

### Built-in Number Sets

The calculator provides the following built-in sets:

 - `N` — natural numbers, including zero
 - `Z` — integers
 - `Q` — rational numbers
 - `R` — real numbers

These are ordinary mathematical sets available to the expression language.

They may therefore be used anywhere a user-defined set may be used.

For example:

x in N

and:

A = {x in Z : x > 10}

use the same set-membership and set-builder mechanisms.

### Sets

Sets are first-class mathematical values.

A set may be written using:
 - finite enumeration
 - set-builder notation
 - a built-in number set
 - a previously defined set
 - set operations
 - finite set construction rules supported by the language

Examples include:

{0, 1, 2, 3, 4}

{x in R : x^2 > 10}

{x in N : x! < 5}

A = {x in 2Z : x / 2 in 2Z}

The calculator does not attempt to resolve arbitrarily complicated infinite sets. Set evaluation uses explicit computational limits and only the interpretations supported by the language.

### Set Operations

The language supports set operations including:

 - intersection
 - union
 - set difference
 - Cartesian product
 - finite set powers
 - scalar multiplication

Intersection and union are rendered using the mathematical symbols `∩` and `∪`.

For example:

A ∩ B
A ∪ B
A - B

For a finite positive integer `n`, `A^n` denotes the n-fold Cartesian product of `A`.

Thus:

A^2

represents:

A × A

Scalar multiplication is also defined:

nA = {na : a in A}

where `n` is a finite scalar.

The language therefore permits constructions such as:

n(A × A)

and:

n(A^m)

when the corresponding mathematical objects are defined.

The calculator intentionally gives up on complex or computationally infeasible set constructions rather than attempting to resolve them indefinitely.

### Angle modes

The application supports:
 - radians
 - degrees
 - gradians

The selected angle mode affects trigonometric evaluation globally.

### Declaration semantics

The language distinguishes between declarations and graphable expressions.

A valid function declaration using `=` both declares the function and requests visualization when visualization is possible.

For example:

f(x,a) = x+a

declares the binary function `f` and plots `f` only if `a` is defined and the resulting function is graphable.

Using `<-` declares the mathematical object without requesting visualization.

For example:

A <- {x in R : x < 0}

defines a set without requesting that the set itself be visualized.

Ordinary equations and inequalities are interpreted as graphing expressions when they are not valid declarations.

The language therefore supports both convenient interpretation and explicit declaration semantics without requiring a separate declaration mode.

### Conditional Restrictions

An expression may be conditionally restricted using `if`.

For example:

x^2 if x < 0

restricts the expression to real values satisfying `x < 0`.

A set may be used explicitly:

x^2 if x in {x in R : x < 0}

A named set may also be used:

A = {x in R : x < 0}
x^2 if x in A

This mechanism replaces the need for a separate editor-owned domain configuration system.

## Graph Interaction

The Cartesian graphing workspace supports:
 - mouse-based panning
 - zooming
 - viewport reset
 - coordinate display
 - graph selection
 - graph highlighting
 - graph visibility controls
 - graph tracing
 - root selection
 - intersection selection

Clicking empty graph space and dragging pans the viewport.

Clicking and dragging directly on a graph traces the graph and displays the current coordinate.

Roots and intersections are discovered interactively rather than automatically displaying every possible root in the viewport. Clicking cycles their persistent display state.

## Expression Management

Each expression exists as an independent item in an expandable expression list.

The list supports:
 - creating expressions
 - deleting expressions
 - duplicating expressions
 - renaming expressions
 - hiding/showing expressions
 - reordering expressions for bookkeeping
 - graph styling

List ordering does not determine rendering order.

Newly defined expressions initially render above older expressions, while clicking a graph brings it to the front.

Each expression may have configurable:
 - visibility
 - color
 - opacity
 - line thickness
 - line style

Mathematical restrictions are represented by ordinary expressions and sets rather than by separate per-expression domain metadata.

Invalid expressions remain in the document and display their error beneath the corresponding expression.

## Persistence

Documents should preserve the complete mathematical state of the graphing workspace, including:
 - expressions
 - definitions
 - set definitions
 - dependencies
 - expression ordering
 - visibility
 - graph styling
 - viewport state
 - graph settings

Restrictions expressed through sets and conditional expressions are preserved as part of the expressions themselves.

Invalid expressions should also be preserved.

Saving and loading should restore the user's mathematical document rather than merely storing an image of the current graph.

JSON is the intended initial persistence format.

## Image Export

The application should support exporting the currently visible graph as an image.

The default export represents what the user currently sees.

Export options may allow the user to include or exclude selected visual elements such as:

 - labels
 - axes
 - grid
 - other annotations

Image export should ignore local expression errors rather than failing the entire export operation.

## Architecture

The application should maintain a strict separation between the mathematical engine and the graphical user interface.

The mathematical core must not depend on Swing.

The major architectural layers are:
 - Mathematical language and expression model
 - Semantic analysis and dependency management
 - Numerical evaluation
 - Set evaluation
 - Graphing and numerical algorithms
 - Rendering
 - Structured mathematical editor
 - Swing user interface
 - Persistence and export

Set evaluation is part of the mathematical engine. It should not depend on the expression-list UI or any graphical domain editor.

This separation should allow the mathematical engine and graphing algorithms to be tested independently of the UI.

## Performance Philosophy

The application should prioritize high-quality rendering and correctness over intentionally degraded interactive rendering.

Graphs should not normally switch to visibly lower-quality representations while the user pans or zooms.

Expensive numerical operations should nevertheless have finite computational budgets so that pathological expressions or set constructions cannot freeze the application indefinitely.

If an operation cannot be completed within its computational budget, the application should report that it was unable to evaluate the graph or set rather than hanging.

An initial target of approximately 1,000 expression items is acceptable, with performance limits to be introduced later only if practical testing demonstrates a need.

## Explicitly Out of Scope

The initial product does not attempt to become a general-purpose computer algebra system or mathematical programming environment.

The following are outside the initial scope:

 - complex numbers
 - vectors (apart from ordered pairs representing points)
 - matrices
 - symbolic algebraic simplification
 - symbolic derivatives
 - symbolic integration
 - general numerical solving tools
 - 3D graphing
 - units of measurement
 - cloud synchronization
 - collaboration
 - plugins
 - scripting
 - mobile applications
 - localization/internationalization
 - touch/stylus-specific interaction
 - accessibility-specific engineering beyond normal good UI practice
 - regression/statistical analysis
 - advanced CAS functionality
 - arbitrary symbolic set-theoretic reasoning
 - unrestricted infinite set enumeration

Parametric graphing, CSV datasets, more advanced set expressions, advanced CAS functionality, and global coordinate transformations are possible future extensions but should not dictate the initial implementation.

## Product Success Criterion

The project is successful when it behaves and feels like a genuine, polished desktop graphing application that could reasonably be offered as a commercial product.

The standard is not merely:

"Can it graph an equation?"

The standard is:

"Would a user willingly use this as their everyday graphing calculator?"
