package FantasyMockDraft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class DraftBoard {
    private List<Player> availablePlayers;
    private HashMap<String, List<Player>> positionsLists;
    private Team[] teams;
    private Team[] reversedTeams;
    private Player[] topPlayers;
    private int numRounds;
    private int numTeams;
    private Scanner in;
    private Random r;
    Player[][] boardViewSelections;
    int currentRound;
    int currentSpot;

//--------------------------------------------------------Constructor(s)----------------------------------------------------------------------
    DraftBoard(int numTeams, int draftPosition, int numRounds, Scanner in) {
        this.numRounds = numRounds;
        this.numTeams = numTeams; 
        this.in = in;
        this.r = new Random();
        this.availablePlayers = new ArrayList<>();
        this.positionsLists = new HashMap<>();
        this.teams = new Team[numTeams];
        this.reversedTeams = new Team[numTeams];
        this.topPlayers = new Player[25];
        this.boardViewSelections = new Player[numRounds][numTeams];
        this.currentRound = 0;
        this.currentSpot = 0;
        //Set up the teams and players
        setUpTeams();
        try {
            setUpAvailablePlayers();
        }
        catch (IOException e) {
            System.err.println("Error reading players from file: " + e.getMessage());
        }
        setUpPositionsLists();
        setUpTopPlayers();
        teams[draftPosition - 1].setHuman(true);
    }

//---------------------------------------------------Public Operational Methods------------------------------------------------------------------------------
    //Called by object to initiate draft process. Passes the forward or reverse team list into the draftRound function so it snakes
    public void runDraft() {
        //Bool and currentRound are helpers for the 2D draft board
        boolean forward = true;
        for (int i = 0; i < numRounds; i++) {
            if (forward) {
                draftRound(teams, forward); 
            }
            else {
                draftRound(reversedTeams, forward);
            }
            forward = !forward;
            currentRound++;
        }
    }

    //Display each team's roster
    public void display() {
        for (Team t : teams) {
            t.displayTeam();
        }
    }
    
    //Display a 2D draft board of the results
    public void displayBoard() {
        displayTeamsRow();
        for (int i = 0; i < numRounds; i++) {
            if (boardViewSelections[i][0] == null && boardViewSelections[i][numTeams - 1] == null) {
                break;
            }
            System.out.print("    ");
            for (int j = 0; j < numTeams; j++) {
                if (boardViewSelections[i][j] == null) {
                    System.out.print("               ");
                }
                else {
                    boardViewSelections[i][j].displayFirst();
                }
            }
            System.out.println();
            System.out.print("    ");
            for (int j = 0; j < numTeams; j++) {
                if (boardViewSelections[i][j] == null) {
                    System.out.print("               ");
                }
                else {
                    boardViewSelections[i][j].displayLast();
                }
            }
            System.out.println();
            System.out.println();
        }
    }
    
//------------------------------------------------------Private Helper Methods-----------------------------------------------------------------------------------
    //Input the teams into array and do it in reverse for the reversed array
    private void setUpTeams() {
        for (int i = 0; i < numTeams; i++) {
            Team newTeam = new Team(i + 1);
            teams[i] = newTeam;
            reversedTeams[numTeams - i - 1] = newTeam;
        }
    }

    //Reads CSV of players with rankings that will determine the values for each player and be the basis of CPU draft
    private void setUpAvailablePlayers() throws FileNotFoundException, IOException {
        String filePath = "FantasyMockDraft/RANKINGS.csv";
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 10) {
                    int ranking = Integer.parseInt(columns[0].replaceAll("\"", ""));
                    String[] name = columns[2].replaceAll("\"", "").split(" ", 2); //Splitting name because it makes printing draft board easier
                    String firstName = name[0];
                    String lastName = name[1];
                    String team = columns[3];
                    String position = columns[4];
                    if (position.contains("DST")) {
                        position = "DST";
                    }
                    else {
                        position = position.substring(1,3);
                    }
                    if (position.startsWith("K")) {
                        continue;
                    }
                    Player newPlayer = new Player(firstName, lastName, position, team, ranking);
                    availablePlayers.add(newPlayer);
                }
                
            }
            availablePlayers.sort(Comparator.comparingInt(Player::getBaseValue).reversed());
        }
    }

    //Helper that fills a Player[] with top 5 players at each position based on their base value derived from AvailPlayer pool
    //Also called in the draftRound to refresh it after a selection made
    private void setUpTopPlayers() {
        HashMap<String, Integer> count = new HashMap<>();
        count.put("QB", 0);
        count.put("WR", 0);
        count.put("RB", 0);
        count.put("TE", 0);
        count.put("DST", 0);
        int i = 0;
        for (int j = 0; j < availablePlayers.size(); j++) {
            Player current = availablePlayers.get(j);
            if (count.get(current.getPosition()) < 5) {
                topPlayers[i++] = current;
                count.merge(current.getPosition(), 1, Integer::sum);
                if (i > 24) {
                    break;
                } 
            }
        }
    }

    //Helper that establishes the Lists of players for each position group
    private void setUpPositionsLists() {
        for (Player current : availablePlayers) {
            String position = current.getPosition();
            positionsLists.computeIfAbsent(position, _ -> new ArrayList<>()).add(current);
        }
    }
    
    //Helper to iterate through the teams in the current round to get their selections and remove players from pool
    private void draftRound(Team[] teams, boolean forwardFlag) {
        //Flag and currentSpot just help 2D board construction and filling
        if (forwardFlag) {
            currentSpot = 0;
        }
        else {
            currentSpot = numTeams - 1;
        }
        for (Team t : teams) {
            Player selection = null;
            if (t.getIsHuman()) {
                selection = userSelection(t);
            }
            else {
                selection = makeSelection(t); //Player choice function
            }
            t.addPlayer(selection);              //Add to roster
            selection.setIsDrafted();                   //Change player variable to drafted true
            availablePlayers.remove(selection);         //Remove from player pool
            positionsLists.get(selection.getPosition()).remove(selection); //Remove player from positions list
            boardViewSelections[currentRound][currentSpot] = selection; //Fill 2D board view slot
            setUpTopPlayers();                            //Reset top player pool
            System.out.println("Team " + t.getTeamNum() + " has selected " + selection.getFullName()); //Display selection in terminal
            if (forwardFlag) {
                currentSpot++;
            }
            else {
                currentSpot--;
            }
        }
    }

    //Helper for user draft selection
    private Player userSelection(Team team) {
        Player selection = null;
        System.out.println();
        while (selection == null) {
            System.out.println("Please select an option below by typing the number associated: ");
            System.out.println("1) Display Roster");
            System.out.println("2) Display Top 10 Available Players");
            System.out.println("3) Display Top 10 Players at a Specific Position");
            System.out.println("4) Display Draft Board");
            System.out.println();
            System.out.print("Enter your choice: ");

            int choice = -1;
            while (true) {
                try {
                    choice = in.nextInt();
                    in.nextLine();
                    break;
                }
                catch (InputMismatchException e) {
                    System.out.println();
                    System.out.println("Invalid input. Please input a number from the options displayed above");
                    System.out.println();
                    in.nextLine();
                    System.out.print("Enter your choice: ");
                }
            }
            
            switch (choice) {
                case 1:
                    team.displayTeam();
                    System.out.println();
                    break;
                case 2:
                    selection = displayTop10();
                    break;
                case 3:
                    String position = "";
                    while (position == "") {
                        System.out.print("Enter a position (QB, RB, WR, TE, DST): ");
                        String temp = in.nextLine().toUpperCase();
                        if (!positionsLists.containsKey(temp)) {
                            System.out.println("Invalid input. Please try again");
                        }
                        else {
                            position = temp;
                        }
                    }
                    selection = displayTopByPos(position);
                    break;
                case 4:
                    displayBoard();
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
            if (selection != null) {
                break;
            }
        }
        return selection;
    }
    
    //Helper to display top 10 available players to the user, ability to select from it
    private Player displayTop10() {
        Player selection = null;
        int start = 0;
        boolean redo = true;
        System.out.println();
        while (redo == true) {
            if (start > 0) {
                System.out.println("0) See previous 10 players");
                System.out.println();
            }
            for (int i = 0; i < 10; i++) {
                System.out.print((i + 1) + ") ");
                availablePlayers.get(i + start).displayPlayer();
            }
            System.out.println();
            System.out.println("11) See next 10 players");
            System.out.println();
            System.out.println("999) Go back to main menu");
            System.out.println();
            System.out.print("Enter your choice: ");

            int choice = -1;
            while (true) {
                try {
                    choice = in.nextInt();
                    in.nextLine();
                    break;
                }
                catch (InputMismatchException e) {
                    System.out.println();
                    System.out.println("Invalid input. Please input a number from the options displayed above");
                    System.out.println();
                    in.nextLine();
                    System.out.print("Enter your choice: ");
                }
            }

            switch (choice) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    selection = availablePlayers.get(choice + start - 1); 
                    redo = false;
                    break;
                case 0:
                    start = Math.max(0, start - 10);
                    break;
                case 11:
                    start += 10;
                    break;
                case 999:
                    return null;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
        }
        return selection;
    }

    //Helper to display top 10 at a specific position to the user, ability to select from the players
    private Player displayTopByPos(String position) {
        System.out.println();
        List<Player> desiredList = positionsLists.get(position);
        Player selection = null;
        int start = 0;
        boolean redo = true;
        while (redo == true) {
            if (start > 0) {
                System.out.println("0) See previous 10 players");
                System.out.println();
            }
            for (int i = 0; i < 10; i++) {
                System.out.print((i + 1) + ") ");
                desiredList.get(i + start).displayPlayer();
            }
            System.out.println();
            System.out.println("11) See next 10 players");
            System.out.println();
            System.out.println("999) Go back to main menu");
            System.out.println();
            System.out.print("Enter your choice: ");

            int choice = -1;
            while (true) {
                try {
                    choice = in.nextInt();
                    in.nextLine();
                    break;
                }
                catch (InputMismatchException e) {
                    System.out.println();
                    System.out.println("Invalid input. Please input a number from the options displayed above");
                    System.out.println();
                    in.nextLine();
                    System.out.print("Enter your choice: ");
                }
            }

            switch (choice) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    selection = desiredList.get(choice + start - 1); 
                    redo = false;
                    break;
                case 0:
                    start = Math.max(0, start - 10);
                    break;
                case 11:
                    start += 10;
                    break;
                case 999:
                    return null;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
        }
        return selection;
    }

    //Helper to determine which player the current team should draft based on needs and RNG
    private Player makeSelection(Team team) {
        //Priority Queue to facilitate the randomness choosing, sorted by adjusted value and added as its calculated
        PriorityQueue<Player> q = new PriorityQueue<>(Comparator.comparingDouble(Player::getAdjValue).reversed());
        for (Player toMod : topPlayers) {
            double modifier = team.getPosWeight(toMod.getPosition());
            toMod.setAdjValue(modifier * toMod.getBaseValue());
            q.offer(toMod);
        }
        //Add some element of randomness, ~66% will take top player, 20% take 2nd player, rest take 3rd player
        int random = r.nextInt(100);
        int skips;
        if (random < 65) {
            skips = 0;
        }
        else if (random < 85) {
            skips = 1;
        }
        else {
            skips = 2;
        }
        for (int i = 0; i < skips; i++) {
            q.poll();
        }
        return q.peek();
    }

    //Helper for the 2D draft board to print out team names in a row
    private void displayTeamsRow() {
        System.out.print("+");
        for (int i = 0; i < numTeams; i++) {
            if (i == numTeams - 1) {
                System.out.print("--------------");
            }
            else {
                System.out.print("---------------");
            }
        }
        System.out.println("+");
        System.out.print("|    ");
        for (Team t: teams) {
            if (t.getTeamNum() < 10) {
                System.out.print("Team " + t.getTeamNum() + "    |    ");
            }
            else {
                System.out.print("Team " + t.getTeamNum() + "   |    ");
            }
        }
        System.out.println();
        System.out.print("+");
        for (int i = 0; i < numTeams; i++) {
            if (i == numTeams - 1) {
                System.out.print("--------------");
            }
            else {
                System.out.print("---------------");
            }
        }
        System.out.println("+");
    }
}
