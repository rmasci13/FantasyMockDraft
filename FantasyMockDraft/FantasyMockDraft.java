package FantasyMockDraft;


import java.util.InputMismatchException;
import java.util.Scanner;

public class FantasyMockDraft {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int[] info;
        if (args.length != 3) {
            System.out.println("Correct usage is java FantasyMockDraft [# of teams] [Draft position] [# of rounds]");
            info = getDraftInformation(in);
        }
        else {
            info = new int[3];
            for (int i = 0; i < args.length; i++) {
                info[i] = Integer.parseInt(args[i]);
            }
        }
        DraftBoard board = new DraftBoard(info[0], info[1], info[2], in);
        board.runDraft();     //Runs the draft
        board.displayBoard(); //Displays as 2D board
        board.display();      //Displays the teams and their rosters
        in.close();
    }


//-----------------------------------------------Public Operational Methods---------------------------------------------------------------------------------------
    //If command-line missing or incorrect, get user input to establish number of rounds, number of teams, and draft position
    public static int[] getDraftInformation(Scanner in) {
        int[] values = new int[3];
        while (true) {
            System.out.println();
            System.out.print("Enter number of teams in the simulation 8 - 14: ");
            try {
                values[0] = in.nextInt();
                if (numTeamsOOBCheck(values[0])) {
                    System.out.println();
                    System.out.println("Invalid input. The number must be between 8 - 14");
                    in.nextLine();
                }
                else {
                    in.nextLine();
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println();
                System.out.println("Invalid input. Please enter a number between 8 - 14");
                in.nextLine();
            }
        }
        while (true) {
            System.out.println();
            System.out.print("Enter your desired draft position 1 - " + values[0] + ": ");
            try {
                values[1] = in.nextInt();
                if (draftSpotOOBCheck(values[1], values)) {
                    System.out.println();
                    System.out.println("Invalid input. The draft position must be between 1 - " + values[0]);
                    in.nextLine();
                }
                else {
                    in.nextLine();
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println();
                System.out.println("Invalid input. Please enter a number between 1 - " + values[0]);
                in.nextLine();
            }
        }
        while (true) {
            System.out.println();
            System.out.print("Enter number of rounds for the draft between 10 - 16: ");
            try {
                values[2] = in.nextInt();
                if (numRoundsOOBCheck(values[2])) {
                    System.out.println();
                    System.out.println("Invalid input. Number of rounds must be between 10 - 16");
                    in.nextLine();
                }
                else {
                    in.nextLine();
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println();
                System.out.println("Invalid input. Please enter a number between 10 - 16");
                in.nextLine();
            }
        }
        return values;
    }


//-------------------------------------------------Private Helper Methods-------------------------------------------------------------------------
    //Check for valid number of teams
    private static boolean numTeamsOOBCheck(int input) {
        if (input < 8 || input > 14) {
            return true;
        }
        return false;
    }
    //Check for valid draft spot selection
    private static boolean draftSpotOOBCheck(int input, int[] values) {
        if (input < 1 || input > values[0]) {
            return true;
        }
        return false;
    }
    //Check for valid number of rounds
    private static boolean numRoundsOOBCheck(int input) {
        if (input < 10 || input > 16) {
            return true;
        }
        return false;
    }
}
