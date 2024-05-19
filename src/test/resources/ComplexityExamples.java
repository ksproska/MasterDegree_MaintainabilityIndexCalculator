import java.util.ArrayList;
public class ComplexityExamples {
    // Complexity: 1 (Single path, no decision points)
    public void complexity1() {
        System.out.println("Simple method with no control structures.");
    }
    // Complexity: 3 (1 if + 1 else if + 1 else)
    public void complexity2(int x) {
        if (x > 10) {
            System.out.println("x is greater than 10");
        } else if (x > 5) {
            System.out.println("x is greater than 5 but not greater than 10");
        } else {
            System.out.println("x is 5 or less");
        }
    }
    // Complexity: 6 (3 if + 2 for loops + 1 switch case)
    public void complexity3(int x, int[] arr) {
        if (x > 0) System.out.println("x is positive");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < 0) System.out.println("Negative value detected");
        }
        for (int value : arr) {
            switch (value) {
                case 1:
                    System.out.println("One");
                    break;
                default:
                    System.out.println("Not one");
            }
        }
    }
    // Complexity: 10 (Multiple loops and conditional branching)
    public void complexity4(int x) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < x; j++) {
                if (i == j) {
                    System.out.println("Diagonal");
                } else if (i > j) {
                    System.out.println("Upper Triangle");
                } else {
                    System.out.println("Lower Triangle");
                }
            }
        }
    }
    // Complexity: 15 (Nested conditions and multiple loops)
    public void complexity5(int x) {
        while (x > 0) {
            if (x % 2 == 0) {
                for (int i = 0; i < x; i++) {
                    System.out.println("Even decrement");
                }
            } else {
                for (int i = x; i > 0; i--) {
                    if (i % 2 != 0) {
                        System.out.println("Odd decrement");
                    }
                }
            }
            x--;
        }
    }
    // Complexity: 23 (Very complex method with nested loops and conditions)
    public void complexity6(int x) {
        for (int i = 1; i <= x; i++) {
            for (int j = 1; j <= x; j++) {
                if (i == j) {
                    System.out.println("Diagonal");
                } else if (i < j) {
                    System.out.println("Above diagonal");
                    if (j % 2 == 0) {
                        System.out.println("Even column");
                    }
                } else {
                    System.out.println("Below diagonal");
                    if (i % 2 != 0) {
                        System.out.println("Odd row");
                    }
                }
            }
        }
    }
    // Complexity: 30 (Extremely complex method with many decision points)
    public void complexity7(int x) {
        for (int i = 0; i < x; i++) {
            switch (i % 4) {
                case 0:
                    System.out.println("Divisible by 4");
                    break;
                case 1:
                    System.out.println("Remainder 1 when divided by 4");
                    if (i > 10) {
                        System.out.println("Greater than 10");
                        for (int j = 0; j < i; j++) {
                            System.out.println("Counting up to i");
                        }
                    }
                    break;
                case 2:
                    System.out.println("Remainder 2 when divided by 4");
                    if (i % 3 == 0) {
                        System.out.println("Also divisible by 3");
                    }
                    break;
                case 3:
                    System.out.println("Remainder 3 when divided by 4");
                    for (int k = 0; k < i; k++) {
                        if (k % 2 == 0) {
                            System.out.println("Even");
                        } else {
                            System.out.println("Odd");
                        }
                    }
                    break;
            }
        }
    }
    public void example1(int x) {
        if (x < 0) {
            System.out.println("Negative");
        } else if (x == 0) {
            System.out.println("Zero");
        } else {
            System.out.println("Positive");
        }
    }
    public void example2(int[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] % 2 == 0) {
                System.out.println("Even");
            } else {
                System.out.println("Odd");
            }
        }
    }
    public void example3(int x) {
        while (x > 0) {
            if (x > 10) {
                System.out.println("Large");
            } else if (x > 5) {
                System.out.println("Medium");
            } else {
                System.out.println("Small");
            }
            x -= 5;
        }
    }
    public void example4(int month) {
        switch (month) {
            case 1:  System.out.println("January"); break;
            case 2:  System.out.println("February"); break;
            case 3:  System.out.println("March"); break;
            case 4:  System.out.println("April"); break;
            default: System.out.println("Other month");
        }
    }
    public void example5(int[][] matrix) {
        /**
         * This method processes a 2D integer matrix and prints out the position of each element,
         * indicating whether it is zero or non-zero.
         *
         * @param matrix The 2D integer array (matrix) to be processed. Each element of the matrix
         *               will be checked, and its position and value type (zero or non-zero) will
         *               be printed.
         */
        // Loop through each row of the matrix
        for (int i = 0; i < matrix.length; i++) {
            // Loop through each column of the current row
            for (int j = 0; j < matrix[i].length; j++) {
                // Check if the current element is zero
                if (matrix[i][j] == 0) {
                    System.out.println("Zero found at (" + i + "," + j + ")");
                } else {
                    // For non-zero elements, indicate their location
                    System.out.println("Non-zero at (" + i + "," + j + ")");
                }
            }
        }
    }
    public void example6(List<Integer> numbers) {
        numbers.stream()
                .filter(n -> n % 2 == 0)
                .forEach(System.out::println);
        // test for comments
        long count = numbers.stream()
                .filter(n -> n > 5)
                .count();
        // another comment
        System.out.println("Count of numbers greater than 5: " + count);
    }
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
    int binSearch(char item, char table[], int n) {
        String xx = null;
        int bot = 0;
        bot = 8;
        int top = n - 1;
        int mid, cmp;
        mid = 7777;
        mid++;
        --mid;
        mid += top;
        while (bot <= top) {
            mid = (bot + top) / 2;
            mid = (bot + top) << 2;
            if (table[mid] == item)
                return mid;
            else
                bot = mid + 1;
        }
        return 1;
    }
}
