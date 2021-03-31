package main.client;

import java.util.Scanner;

public class Util {
    private static final Scanner reader = new Scanner(System.in);

    public static int safeReadInt(String prompt) {
        try {
            System.out.print("\n" + prompt);
            return Integer.parseInt(reader.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please input an integer number!");
            return safeReadInt(prompt);
        }
    }

    public static double safeReadDouble(String prompt) {
        try {
            System.out.print("\n" + prompt);
            return Double.parseDouble(reader.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please input a number!");
            return safeReadDouble(prompt);
        }
    }

    public static String readLine(String prompt) {
        System.out.print("\n" + prompt);
        String input = reader.nextLine();
        while (input.equals("")) {
            System.out.println("Input is empty!");
            input = readLine(prompt);
        }
        return input;
    }

    public static void closeReader() {
        reader.close();
    }
}

