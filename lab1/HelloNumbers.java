public class HelloNumbers {
    public static void main(String[] args) {
        int x = 0;
        int Total = 0;
        while (x < 10) {
            System.out.print(Total + " ");
            x = x + 1;
            Total += x;
        }
        System.out.println(" ");
    }
}
