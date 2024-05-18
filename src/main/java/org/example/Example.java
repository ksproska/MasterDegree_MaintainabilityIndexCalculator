package org.example;
import java.util.ArrayList;
public class Example {
    public static int calculateScore(int baseScore, int bonus, boolean isSpecial) {
        int score = baseScore + bonus;  // '+' is the operator, 'baseScore' and 'bonus' are operands
        if (score > 100) {  // '>' is the operator, 'score' and '100' are operands
            score -= 10;  // '-=' is the operator, 'score' and '10' are operands
        }
        if (isSpecial && !isHighScore(score)) {  // '&&' and '!' are operators, 'isSpecial' and the result of isHighScore(score) are operands
            score = score < 50 ? score + 50 : score + 20;  // '?' and ':' are operators, 'score < 50', 'score + 50', and 'score + 20' are operands
        }
        score = score & 0xFF;  // '&' is the operator, 'score' and '0xFF' are operands
        Object obj = "This is a string";  // String literal is an operand
        if (obj instanceof String) {  // 'instanceof' is the operator, 'obj' and 'String' are operands
            score += ((String) obj).length();  // '.' is the operator to access 'length', 'obj' is an operand
        }
        final var arr = new ArrayList<>();
        arr.add(obj);
        return score;
    }
    // Helper method to determine if score is considered high
    public static boolean isHighScore(int score) {
        return score >= 80;  // '>=', 'score', and '80' are used here as operator and operands
    }
}
