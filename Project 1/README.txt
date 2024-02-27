# Compiler Design - Project 1 

### Team Members
Ashley Peleg
Ariana Contes 
Andres Rodriguez 
Aaron Amalraj
Stephanie Sicilian


This Java program is a simple lexical analyzer that tokenizes Java source code into different token types such as keywords, identifiers, constants, operators, and punctuation. It reads a Java program file and tokenizes its contents based on predefined regular expression patterns.

### Usage

1. Compile the `LexicalAnalyzer.java` file using `javac LexicalAnalyzer.java`.
2. Run the `LexicalAnalyzer` class with the path to the Java program file as an argument.
    ```
    java LexicalAnalyzer /path/to/your/Java/file.java
    ```

### Sample Input

The sample input provided is a Java program in the `Sum.java` file, which calculates and prints the sum of two integers entered by the user.

### Output

The program will output the tokens identified in the Java program file along with their corresponding token types. Each token will be displayed in the format `(TOKEN_TYPE, value)`.

### Token Types

- `KEYWORD`: Represents Java keywords such as `int`, `float`, `if`, `else`, etc.
- `IDENTIFIER`: Represents variable names and identifiers.
- `CONSTANT`: Represents numerical constants.
- `OPERATOR`: Represents operators in Java (+, -, *, etc.).
- `PUNCTUATION`: Represents punctuation marks (; , ( ) [ ], etc.).

### Error Handling

If the program encounters an unrecognized token during tokenization, it will throw an `IllegalStateException` with an error message indicating the unrecognized token.

### External Libraries Used

- `java.util.regex.Matcher`: Used for pattern matching with regular expressions.
- `java.nio.file.Files`: Used for reading the contents of a file.
- `java.nio.file.Paths`: Used for representing file paths.
- `java.util.ArrayList`: Used to store the tokens generated during tokenization.



