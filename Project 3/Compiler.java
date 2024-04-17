//to compile do:javac Compiler.java to run do:java Compiler sum.c

//Compiler.java updated code for semantic analyzer:

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

// Token types
enum TokenType {
    PREPROCESSOR_DIRECTIVE,
    KEYWORD,
    IDENTIFIER,
    CONSTANT,
    OPERATOR,
    PUNCTUATION
}

// Token class
class Token {
    TokenType type;
    String value;

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
}

// Lexical Analyzer class
class LexicalAnalyzer {
    public static ArrayList<Token> tokenize(String code) {
        ArrayList<Token> tokens = new ArrayList<>();
        // Define token patterns
        String[] patterns = {
                "#include <\\w+\\.h>",
                "\\b(int|float|char|if|else|while|for|return)\\b", // keywords
                "\\b[a-zA-Z_]\\w*\\b", // identifiers
                "\\b\\d+\\b", // constants
                "\\+|-|\\*|/|=|==|!=|<|>|<=|>=|\\|\\||&&", // operators
                "[;,()\\[\\]{}]" // punctuation
        };
        Pattern pattern = Pattern.compile(String.join("|", patterns));

        Matcher matcher = pattern.matcher(code);
        while (matcher.find()) {
            String matched = matcher.group();
            TokenType type;
            if (matched.matches("#include <\\w+\\.h>")) {
                type = TokenType.PREPROCESSOR_DIRECTIVE;
            } else if (matched.matches("\\b(int|float|char|if|else|while|for|return)\\b")) {
                type = TokenType.KEYWORD;
            } else if (matched.matches("\\b[a-zA-Z_]\\w*\\b")) {
                type = TokenType.IDENTIFIER;
            } else if (matched.matches("\\b\\d+\\b")) {
                type = TokenType.CONSTANT;
            } else if (matched.matches("\\+|-|\\*|/|=|==|!=|<|>|<=|>=|\\|\\||&&")) {
                type = TokenType.OPERATOR;
            } else if (matched.matches("[;,()\\[\\]{}]")) {
                type = TokenType.PUNCTUATION;
            } else {
                throw new IllegalStateException("Unrecognized token: " + matched);
            }
            tokens.add(new Token(type, matched));
        }

        return tokens;
    }
}

// Parser class
class Parser {
    private ASTNode parseProgram(ArrayList<Token> tokens) {
        ASTNode root = new ASTNode("Program", "");

        ASTNode currentBlock = root; // Track the current block being filled

        // Iterate through tokens to build the AST
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.type == TokenType.PREPROCESSOR_DIRECTIVE) {
                root.addChild(new ASTNode("Preprocessor Directive", token.value));
            } else if (token.value.equals("int") && i + 1 < tokens.size() && tokens.get(i + 1).value.equals("main")) {
                // Found the main function declaration, create a block for its body
                ASTNode mainBlock = new ASTNode("Block", "");
                currentBlock.addChild(mainBlock);
                currentBlock = mainBlock;
            } else if (token.value.equals("{")) {
                // Found the start of a new block, create a nested block
                ASTNode newBlock = new ASTNode("Block", "");
                currentBlock.addChild(newBlock);
                currentBlock = newBlock;
            } else if (token.value.equals("}")) {
                // Found the end of the current block, move back to the parent block
                if (currentBlock != root) {
                    currentBlock = getParentBlock(root, currentBlock);
                }
            } else {
                // Add the token as a child of the current block
                currentBlock.addChild(new ASTNode(token.type.toString(), token.value));
            }
        }

        return root;
    }

    private ASTNode getParentBlock(ASTNode root, ASTNode currentBlock) {
        // Traverse the tree to find the parent block of the current block
        if (root.children.contains(currentBlock)) {
            return root;
        }
        for (ASTNode child : root.children) {
            ASTNode parent = getParentBlock(child, currentBlock);
            if (parent != null) {
                return parent;
            }
        }
        return null;
    }

    public ASTNode buildAST(ArrayList<Token> tokens) {
        return parseProgram(tokens);
    }

    // Semantic analysis methods
    public boolean performSemanticAnalysis(ASTNode root) {
        // Perform semantic analysis here
        // Example: Check for variable declaration before use
        return checkVariableDeclarationBeforeUse(root) && checkTypeCompatibility(root);
    }

    // Method to check if variables are declared before use
    private boolean checkVariableDeclarationBeforeUse(ASTNode node) {
        // Track declared variables
        Set<String> declaredVariables = new HashSet<>();

        // Track semantic analysis result
        boolean success = true;

        // Traverse the AST
        for (ASTNode child : node.children) {
            if (child.type.equals("Declaration")) {
                // If a variable is declared, add it to declaredVariables set
                declaredVariables.add(child.children.get(0).value);
            } else if (child.type.equals("Identifier")) {
                // If an identifier is found, check if it's declared
                if (!declaredVariables.contains(child.value)) {
                    System.err.println("Error: Variable '" + child.value + "' used before declaration.");
                    success = false;
                }
            }

            // Recursively check children
            success &= checkVariableDeclarationBeforeUse(child);
        }

        return success;
    }

    // Method to perform type compatibility checks
    private boolean checkTypeCompatibility(ASTNode node) {
        // Track semantic analysis result
        boolean success = true;

        // Traverse the AST
        for (ASTNode child : node.children) {
            // If the node is an expression, perform type checking
            if (child.type.equals("Expression")) {
                // Check if both operands of binary operators are of compatible types
                if (!checkExpressionTypeCompatibility(child)) {
                    success = false;
                }
            }

            // Recursively check children
            success &= checkTypeCompatibility(child);
        }

        return success;
    }

    // Method to check type compatibility in expressions
    private boolean checkExpressionTypeCompatibility(ASTNode node) {
        // Track semantic analysis result
        boolean success = true;

        // For simplicity, assume binary expressions consist of two children
        if (node.children.size() != 3) { // Operator and two operands
            System.err.println("Error: Invalid expression structure.");
            return false;
        }

        // Extract operator and operand nodes
        ASTNode operatorNode = node.children.get(1);
        ASTNode leftOperand = node.children.get(0);
        ASTNode rightOperand = node.children.get(2);

        // Check if operator and operands are of compatible types
        String operator = operatorNode.value;
        if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/")) {
            // Arithmetic operations: operands must be numeric
            if (!isNumericType(leftOperand) || !isNumericType(rightOperand)) {
                System.err.println("Error: Type mismatch in arithmetic expression.");
                success = false;
            }
        }

        return success;
    }

    // Helper method to check if a node represents a numeric type
    private boolean isNumericType(ASTNode node) {
        // For simplicity, assume numeric types are 'int' and 'float'
        return node.value.equals("int") || node.value.equals("float");
    }
}

// Compiler class
public class Compiler {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Compiler <path_to_c_file>");
            return;
        }

        String filePath = args[0];
        try {
            // Read the content of the C file
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            StringBuilder codeBuilder = new StringBuilder();
            for (String line : lines) {
                codeBuilder.append(line).append("\n");
            }
            String code = codeBuilder.toString();

            // Tokenize the C code
            ArrayList<Token> tokens = LexicalAnalyzer.tokenize(code);

            // Build the Abstract Syntax Tree (AST)
            Parser parser = new Parser();
            ASTNode ast = parser.buildAST(tokens);

            // Perform semantic analysis
            boolean semanticAnalysisSuccess = parser.performSemanticAnalysis(ast);

            if (semanticAnalysisSuccess) {
                // Print AST
                System.out.println("Abstract Syntax Tree (AST):");
                ast.printTree();
                System.out.println("Semantic analysis successful. Parsing successful");
            } else {
                System.err.println("Semantic analysis failed. AST will not be printed.");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}

// AST node classes
class ASTNode {
    String type;
    String value;
    ArrayList<ASTNode> children;

    ASTNode(String type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    void addChild(ASTNode child) {
        children.add(child);
    }

    void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + type + ": " + value);
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).print(prefix + (isTail ? "    " : "│   "), true);
        }
    }

    void printTree() {
        print("", true);
    }
}
