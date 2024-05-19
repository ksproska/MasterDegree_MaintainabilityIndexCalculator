public class Example {
    public void example(int x) {
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
}
