# Semantic Analyzer - Project 3

### Team Members
Ashley Peleg
Ariana Contes 
Andres Rodriguez 
Aaron Amalraj
Stephanie Sicilian


## Build a semantic analyzer for the context-free grammar implemented in Project 2

A simple compiler developed in Java that performs lexical analysis, builds an Abstract Syntax Tree (AST), and conducts semantic analysis on C code. The compiler reads in a C file, tokenizes the code, constructs an AST, and performs semantic checks to ensure that the code follows correct syntax and semantics.

## Usage

To compile the compiler, run the following command:

```
javac Compiler.java
```

To execute the compiler and analyze a C file, run the following command:

```
java Compiler <path_to_c_file>
```

Replace `<path_to_c_file>` with the path to the C file you want to analyze.

## Semantic Analysis

The compiler performs semantic analysis on the AST to ensure correct variable declaration before use and type compatibility in expressions. It checks if variables are declared before use and verifies that operands of binary operations are of compatible types.

## AST Visualization

The compiler outputs the Abstract Syntax Tree (AST) for the input C code. The AST is displayed in a structured format showing the nodes and their relationships.

## Token Types

The compiler recognizes the following token types during lexical analysis:

- Preprocessor Directive
- Keyword
- Identifier
- Constant
- Operator
- Punctuation


