# Root and Intersection Detection

## Purpose

The root/intersection system locates notable points on graphs for interactive inspection.

It must support:

- roots of ordinary functions;
- intersections between ordinary functions;
- roots of expressions represented as implicit equations where practical;
- numerical coordinates suitable for trace and point displays;
- graph restrictions expressed through conditional expressions and sets.

Detection is numerical and does not perform symbolic solving.

## Root Detection

For:

$$
f(x)=0,
$$

the initial algorithm should combine **interval sampling** with a **bracketing root solver**.

The visible coordinate interval is sampled to identify candidate intervals where:

- the function changes sign;
- a sampled value is sufficiently close to zero;
- a discontinuity or sharp feature suggests that additional subdivision is necessary;
- the applicable conditional/set restriction changes.

Sign-changing intervals are refined using **Brent's method** or a comparable bracketed method.

Only inputs for which the expression is applicable and numerically defined are considered valid root candidates.

## Restricted Roots

A function may be restricted using an `if` expression:

$$
f(x)=x^2-1\text{ if }x\in A.
$$

A root outside `A` is not a root of the graphable restricted expression.

For example:

$$
x^2-1\text{ if }x\in\{x\in R:x>0\}
$$

contains the root `x = 1`, but not `x = -1`.

The root detector must therefore respect the expression's resolved condition rather than consulting separate domain metadata.

## Discrete Restrictions

A restricted expression may use a discrete set:

$$
x^2-4\text{ if }x\in N.
$$

The root detector must recognize that only natural-number inputs are applicable.

In this example, `x = 2` is a valid root while `x = -2` is not part of the graph.

Root detection for discrete sets should inspect applicable set elements rather than assuming that the function is defined continuously between them.

## Tangential Roots

A root does not necessarily produce a sign change. For example:

$$
f(x)=x^2
$$

touches zero without crossing it.

The detection algorithm should therefore also identify sufficiently small local extrema or near-zero samples and refine those regions before deciding that no root exists.

This detection is inherently heuristic and must operate within a computational budget.

## Function Intersections

To find intersections of:

$$
f(x)
$$

and:

$$
g(x),
$$

the system instead solves:

$$
h(x)=f(x)-g(x)=0.
$$

The same root-detection algorithm can then be reused.

The resulting coordinate is:

$$
(x,f(x)).
$$

Undefined values in either function invalidate the candidate.

Both functions' conditional restrictions must be satisfied for the intersection to be valid.

## Implicit Intersections

For implicit graphs, intersections can be located from the generated geometric representation or by solving the corresponding numerical equations where appropriate.

The initial implementation should prioritize ordinary-function roots and intersections. More general implicit-curve intersection detection may use the same spatial subdivision techniques employed by the implicit graphing system.

## Accuracy and Deduplication

Detected roots and intersections are refined until their numerical error is below a configurable tolerance or the iteration limit is reached.

Multiple detections representing the same point must be merged using a numerical tolerance.

Results should be returned in coordinate order and limited to the visible region and applicable mathematical restrictions relevant to the current graph.

## Interaction

Root and intersection detection should be performed on demand rather than continuously recomputed every frame.

The interaction layer may request nearby roots or intersections when the user interacts with a graph.

Detection must respect conditional expressions and set membership.

A numerically undefined point is not a valid root or intersection, even if the input satisfies the graph's set restriction.

## Computational Limits

Root and intersection detection must operate under finite computational budgets.

Set membership, adaptive subdivision, and root refinement may all require numerical work.

If a candidate cannot be resolved within the available budget, the detector should discard or mark the candidate as unresolved rather than blocking the application indefinitely.
