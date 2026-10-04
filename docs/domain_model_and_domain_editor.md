# Domain Model and Domain Editor
## Domain Model
Each expression may have a collection of domain specifications, one for each variable selected by the user.

A domain specification consists of:
 - a variable
 - a number set: N, Z, Q, or R
 - one or more intervals
 - a lower bound for each interval
 - an upper bound for each interval
 - an inclusive or strict flag for each finite endpoint

Intervals are combined as a union.

For example:

$$
\leftarrow 2 < \overset{R}{x} \leq \frac{x^2}{3} \rightarrow
$$

represents the real values of x satisfying both bounds.

A domain does not change the mathematical expression itself. It restricts the values over which the expression is considered for graphing and evaluation in the relevant context.

## Default Domains

The default domain for a variable is:

$$
-\infty < \overset{R}{x} < \infty
$$

with number set $\mathbb R$.

A function with several variables has this default domain independently for every variable:

$$
-\infty < \overset{R}{x} < \infty
\qquad
-\infty < \overset{R}{a} < \infty
$$

The default domain is intentionally broad. Built-in functions such as sqrt and log do not automatically change the displayed domain when their mathematical definition is restricted.

Instead, invalid points are handled during numerical evaluation.

## Domain Editor

The domain editor is accessed through the dropdown menu associated with an expression.

It contains an appendable list of variable-domain specifications.

When a new specification is added, the user first selects the variable through a popup.

The list is functionally unbounded.

A variable does not need to occur in the expression. A domain specified for an unused variable simply has no effect and does not produce an error. This allows a domain to be configured before the corresponding variable is introduced.

When a variable is added to an expression and the expression is confirmed, that variable is automatically added to the domain list using the default domain if it is not already present.

## Domain Display

The domain editor displays each variable using its selected number set as an overset:

$$
-\infty < \overset{R}{x} < \infty
$$

Multiple intervals are separated by |\:

$$
-\infty < \overset{R}{x} \leq 2 \rightarrow
;|;
\leftarrow 2 < \overset{R}{x} \leq \frac{x^2}{3} \rightarrow
$$

The expression editor's mathematical formatting algorithm is used to display bounds.

Consequently, bounds are displayed using the same mathematical notation as ordinary expressions.

## Bounds

Each interval has a lower and upper bound.

A bound may contain any valid expression, including variables and function calls.

Examples include:

$$
\leftarrow 0 < \overset{R}{x} < a \rightarrow
$$

$$
\leftarrow 0 < \overset{R}{x} < \sin(x+1) \rightarrow
$$

$$
\leftarrow 0 < \overset{R}{x} < \frac{1}{x} \rightarrow
$$

The bound is stored as an expression rather than as formatted text.

When entering a bound, the user may use the same expression syntax supported elsewhere in the calculator. Parentheses or braces must be used where necessary to make the intended structure unambiguous.

Leaving a bound empty represents an unbounded endpoint:
an empty lower bound represents -\infty
an empty upper bound represents \infty

## Bound Editor

Clicking a finite or unbounded inequality opens a bound-editing popup.

The popup contains:
 - a text box for the bound
 - a strict bound checkbox
 - an unbounded checkbox
 - a confirm button
 - a cancel button

The strict bound checkbox is checked by default.

An unchecked strict-bound checkbox produces an inclusive endpoint.

The unbounded checkbox forces the endpoint to be unbounded. When checked:
 - strict bound is forced on
 - the text box becomes disabled
 - the existing text remains stored internally
 - confirming produces the same effective bound as an empty bound

If the user later disables unbounded, the previously entered expression is restored, regardless of whether the unbounded selection was previously confirmed.

## Editing an Interval

Suppose the initial domain is:

$$
-\infty < \overset{R}{x} < \infty
$$

The user can set the lower bound to 2 while keeping it strict:

$$
\leftarrow 2 < \overset{R}{x} < \infty
$$

The upper bound can then be changed independently. For example, setting it to x²/3 with an inclusive endpoint produces:

$$
\leftarrow 2 < \overset{R}{x} \leq \frac{x^2}{3} \rightarrow
$$

## Multiple Intervals

The arrows at either end of a domain allow additional intervals to be appended.

Adding an interval automatically creates a boundary adjacent to the existing interval.

For example:

$$
\leftarrow 2 < \overset{R}{x} \leq \frac{x^2}{3} \rightarrow
$$

can be extended using the left arrow to produce:

$$
-\infty < \overset{R}{x} \leq 2 \rightarrow
;|;
\leftarrow 2 < \overset{R}{x} \leq \frac{x^2}{3} \rightarrow
$$

The new interval initially uses the existing lower boundary as its upper boundary, with opposite strictness.

The user can then edit the new interval normally.

The | symbol represents the union of the intervals.

## Number Sets

Clicking the number-set label above a variable opens a selection popup.

The available sets are:

- N — natural numbers, including zero
- Z — integers
- nZ, nN — integer ideals for input n
- Q — rational numbers
- R — real numbers

The selected set determines which values within the specified intervals belong to the domain.

For example:

$$
\leftarrow 0 \leq \overset{Z}{x} \leq 10 \rightarrow
$$

contains only the integers from 0 through 10, while:

$$
\leftarrow 0 \leq \overset{R}{x} \leq 10 \rightarrow
$$

contains every real value in that interval.

## Graphing Behavior

When a domain is specified for a graphable expression, the graph is evaluated only over the values permitted by that domain.

Nothing is plotted outside the specified domain.

For a function with multiple variables, each variable's domain independently restricts the values that may be used when evaluating the expression.

Domain restrictions do not override numerical undefinedness. A point may belong to the specified domain while still producing an undefined numerical result.

For example, the default domain of:

sqrt(x)

remains:

$$
-\infty < \overset{R}{x} < \infty
$$

but negative values do not produce graph points because sqrt(x) is undefined there.

Likewise, the default domain of:

log(x)

remains unrestricted in the domain editor, while non-positive values produce no graph points because the numerical evaluation is undefined.

This keeps the domain feature optional: users who do not need explicit domain control can rely on ordinary numerical behavior, while users who need precise restrictions can configure them explicitly.

This keeps the domain feature optional: users who do not need explicit domain control can rely on ordinary numerical behavior, while users who need precise restrictions can configure them explicitly.
