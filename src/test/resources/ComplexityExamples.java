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
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    System.out.println("Zero found at (" + i + "," + j + ")");
                } else {
                    System.out.println("Non-zero at (" + i + "," + j + ")");
                }
            }
        }
    }

    public void example6(List<Integer> numbers) {
        numbers.stream()
                .filter(n -> n % 2 == 0)
                .forEach(System.out::println);

        long count = numbers.stream()
                .filter(n -> n > 5)
                .count();

        System.out.println("Count of numbers greater than 5: " + count);
    }
}
