import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;

// Token types
enum TokenType {
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

public class LexicalAnalyzer {

    // Define token patterns
    private static final String[] patterns = {
            "\\b(int|float|char|if|else|while|for|return)\\b", // keywords
            "\\b[a-zA-Z_]\\w*\\b", // identifiers
            "\\b\\d+\\b", // constants
            "\\+|-|\\*|/|=|==|!=|<|>|<=|>=|\\|\\||&&", // operators
            "[;,()\\[\\]{}]" // punctuation
    };

    public static ArrayList<Token> tokenize(String program) {
        ArrayList<Token> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile(String.join("|", patterns));

        Matcher matcher = pattern.matcher(program);
        while (matcher.find()) {
            String matched = matcher.group();
            TokenType type;
            if (matched.matches("\\b(int|float|char|if|else|while|for|return)\\b")) {
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

    public static void main(String[] args) {
        String filePath = "/Users/arianacontes/CS361/sum.c";

        try {
            String program = new String(Files.readAllBytes(Paths.get(filePath)));

            ArrayList<Token> tokens = tokenize(program);

            for (Token token : tokens) {
                System.out.println("(" + token.type + ", " + token.value + ")");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
