# Root and Intersection Detection

## Purpose

The root/intersection system locates notable points on graphs for interactive inspection.

It must support:

- roots of ordinary functions;
- intersections between ordinary functions;
- roots of expressions represented as implicit equations where practical;
- numerical coordinates suitable for trace and point displays.

Detection is numerical and does not perform symbolic solving.

## Root Detection

For

$$
f(x)=0,
$$

the initial algorithm should combine **interval sampling** with a **bracketing root solver**.

The visible/domain interval is sampled to identify candidate intervals where:

- the function changes sign;
- a sampled value is sufficiently close to zero;
- a discontinuity or sharp feature suggests that additional subdivision is necessary.

Sign-changing intervals are refined using **Brent's method** or a comparable bracketed method.

Bracketed methods are preferred because they are substantially more robust than relying exclusively on Newton's method.

## Tangential Roots

A root does not necessarily produce a sign change. For example,

$$
f(x)=x^2
$$

touches zero without crossing it.

The detection algorithm should therefore also identify sufficiently small local extrema or near-zero samples and refine those regions before deciding that no root exists.

This detection is inherently heuristic and must operate within a computational budget.

## Function Intersections

To find intersections of

$$
f(x)
$$

and

$$
g(x),
$$

the system instead solves

$$
h(x)=f(x)-g(x)=0.
$$

The same root-detection algorithm can then be reused.

The resulting coordinate is

$$
(x,f(x)).
$$

Undefined values in either function invalidate the candidate.

## Implicit Intersections

For implicit graphs, intersections can be located from the generated geometric representation or by solving the corresponding numerical equations where appropriate.

The initial implementation should prioritize ordinary-function roots and intersections. More general implicit-curve intersection detection may use the same spatial subdivision techniques employed by the implicit graphing system.

## Accuracy and Deduplication

Detected roots and intersections are refined until their numerical error is below a configurable tolerance or the iteration limit is reached.

Multiple detections representing the same point must be merged using a numerical tolerance.

Results should be returned in coordinate order and limited to the visible/domain region relevant to the current graph.

## Interaction

Root and intersection detection should be performed on demand rather than continuously recomputed every frame.

The interaction layer may request nearby roots or intersections when the user interacts with a graph.

Detection must respect the expression's domain and treat numerically undefined points as gaps rather than valid results.
