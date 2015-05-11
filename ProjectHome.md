## javaCalc: an open-source Java calculus library by Duyun Chen and Seth Shannin ##


### Introduction ###
Java is one of, if not the most common programming language taught today. Java is taught in high school through the AP curriculum and is also featured in many universities’ introductory computer science courses. As such, it is considered somewhat essential that the language be backed with the full functionality that could reasonably be expected of it even in these beginner level classes. The aim of this project is to fix one such deficiency, that of a lack of support for standard calculus-handling libraries in Java.

As previously indicated, Java is perhaps the most mainstream of all programming languages. Although it certainly has a huge library (both official and third-party) of available functionality, there is no freely available, let alone open source, package or library that introduces calculus functionality to Java.
Many developers currently use internal wrappers to the C++ Programming language or the powerful python scripting for their existing calculus libraries. While these wrappers are very effective (in fact running faster than code directly executed through the Java Virtual Machine), there is definitely merit to having native calculus functionality in a Java development environment. Given that many users of Java are beginning-level students, it is impractical to require them to incorporate other programming languages which they have no knowledge of.
Creating a Java API would allow users to utilize calculus concepts by importing only a single library. This is in contrast with what would otherwise require knowledge of wrappers and syntax of another programming language.

### Goal ###
The main goal of this project is to develop a symbolic library for Java that can handle regular algebraic expressions as well as standard calculus functions.  Specifically, the library should support:
•	Parsing standard algebraic expressions (syntax tree) from a string.
•	Simplifying algebraic expressions (factoring, common denominator, trigonometric identities, etc).
•	Applying symbolic standard calculus functions (differentiation, integration) to algebraic expressions.
•	Common calculus tools (Taylor series, limits, numerical approximations).
•	Graphing tools (using swing).
•	If time permits, differential equation support (symbolic solver, Euler's approximation, Laplace transform).

## Contributions ##
Special thanks to Seva Luchianov (seva.luchianov@gmail.com) for his contribution on polynomial expansion and integration.

### TODOs ###
  1. More flexible integration (rational expressions first)
  1. Definite integration (numeric)
  1. Differential Equations
  1. Statistical Functions (SUM, MEAN, STDEV, etc)
  1. Slope and Vector Fields in 2-D and 3-D
  1. Complex numbers
  1. Matrix inverses
  1. Eigenvalues and Eigenvectors of Matrices
  1. Symbolic algebraic solver (single and system)
  1. Symbolic differential solver (single and system)
  1. Laplace Transforms
  1. Fast Fourier Transform (FFT)
  1. Discrete Cosine Transform (DCT) and Discrete Sine Transform (DST)
  1. Riemann Zeta Function (and other special functions)
  1. Fourier Series and Fourier Transforms

---
