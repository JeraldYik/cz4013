package main.client;

import java.util.Scanner;

/**
 * This class provides utility to read user input via stdin.
 */
public class Util {
    private static final Scanner reader = new Scanner(System.in);

    /**
     * Reads an integer from stdin.
     * This method will attempt to read user input until it encounters
     * a valid integer.
     *
     * @param prompt the prompt
     * @return an integer from stdin
     */
    public static int safeReadInt(String prompt) {
        try {
            System.out.print("\n" + prompt);
            return Integer.parseInt(reader.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please input an integer number!");
            return safeReadInt(prompt);
        }
    }

    /**
     * Reads a double from stdin.
     * This method will attempt to read user input until it encounters
     * a valid double.
     *
     * @param prompt the prompt
     * @return an double from stdin
     */
    public static double safeReadDouble(String prompt) {
        try {
            System.out.print("\n" + prompt);
            return Double.parseDouble(reader.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please input a number!");
            return safeReadDouble(prompt);
        }
    }

    /**
     * Reads a line from stdin.
     *
     * @param prompt the prompt
     * @return a line from stdin
     */
    public static String readLine(String prompt) {
        System.out.print("\n" + prompt);
        String input = reader.nextLine();
        while (input.equals("")) {
            System.out.println("Input is empty!");
            input = readLine(prompt);
        }
        return input;
    }

    /**
     * Closes the associated reader. Used when exiting from main execution.
     */
    public static void closeReader() {
        reader.close();
    }
}

