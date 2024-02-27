

public class Sum {
    public static void main(String[] args) {
        int num1, num2, sum;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the first integer: ");
        num1 = scanner.nextInt();
        System.out.print("Enter the second integer: ");
        num2 = scanner.nextInt();
        sum = num1 + num2;
        System.out.println("The sum of " + num1 + " and " + num2 + " is " + sum);
    }
}


