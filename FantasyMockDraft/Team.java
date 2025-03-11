package FantasyMockDraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team {
    private int teamNum;
    private boolean isHuman;
    private List<Player> roster;
    private Map<String, Integer> posCount;
    private Map<String, double[]> posWeight;

//---------------------------------------------------Constructor(s)--------------------------------------------------------------------
    Team(int teamNum) {
        this.teamNum = teamNum;
        this.isHuman = false;
        this.roster = new ArrayList<>(15);
        this.posCount = new HashMap<>();
        this.posWeight = new HashMap<>();
        posCount.put("QB", 0);
        posCount.put("WR", 0);
        posCount.put("RB", 0);
        posCount.put("TE", 0);
        posCount.put("DST", 0);
        //Values adjust the weight applied to players values based on how many of a position already on roster
        posWeight.put("QB", new double[]{1.0, 0.75, 0});
        posWeight.put("WR", new double[]{1.0, 1.0, 0.95, 0.9, 0.85, 0.80, 0.75});
        posWeight.put("RB", new double[]{1.0, 1.0, 0.95, 0.9, 0.85, 0.80, 0.75});
        posWeight.put("TE", new double[]{1.0, 0.8, 0});
        posWeight.put("DST", new double[]{1.0, 0});
    }

//-----------------------------------------------Getters & Setters-------------------------------------------------------------------

    //Getters
    public boolean getIsHuman() {
        return isHuman;
    }
    public Map<String, Integer> getPosCount() {
        return Collections.unmodifiableMap(posCount);
    }
    public int getPosCount(String position) {
        return posCount.get(position);
    }
    public double getPosWeight(String position) {
        int positionCount = getPosCount(position);
        int index = Math.min(positionCount, posWeight.get(position).length - 1);
        return posWeight.get(position)[index];
    }
    public int getTeamNum() {
        return teamNum;
    }

    //Setters
    public void setHuman(boolean isHuman) {
        this.isHuman = isHuman;
    }
//-----------------------------------------Public Operational Methods---------------------------------------------------

    //Adding player to roster and keeping it desired sorted order so display is easy
    public void addPlayer(Player player) {
        roster.add(player);
        posCount.merge(player.getPosition(), 1, Integer::sum);
        sortRoster();
    }

    //Display Roster in a listed out format
    public void displayTeam() {
        displayTeamName();
        for (Player player : roster) {
            player.displayPlayer();
        }
    }

//----------------------------------------------Private Methods----------------------------------------------------

    //Display team name in a box for displayTeam function
    private void displayTeamName() {
        if (teamNum <= 9) {
            System.out.println("+------------+");
            System.out.println("|   Team " + teamNum + "   |");
            System.out.println("+------------+");
        }
        else {
            System.out.println("+-------------+");
            System.out.println("|   Team " + teamNum + "   |");
            System.out.println("+-------------+");
        }
    }

    //Two methods to sort the roster how I want
    private void sortRoster() {
        roster.sort(Comparator.comparingInt(this::getPositionPriority));
    }
    private int getPositionPriority(Player player) {
        switch (player.getPosition()) {
            case "QB":
                return 1;
            case "WR":
                return 2;
            case "RB":
                return 3;
            case "TE": 
                return 4;
            case "DST": 
                return 5;
            default: 
                return 6;
        }
    }
}
