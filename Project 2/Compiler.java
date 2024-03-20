import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/*to test it do: 
javac Compiler.java
java Compiler sum.c */

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

            // Print AST
            System.out.println("Abstract Syntax Tree (AST):");
            ast.printTree();
            System.out.println("Parsing successful");
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