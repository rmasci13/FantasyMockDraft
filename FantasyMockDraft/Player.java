package FantasyMockDraft;

public class Player {
    private String firstName;
    private String lastName;
    private String position;
    private String team;
    private int ranking;
    private int baseValue;
    private double adjValue;
    private boolean isDrafted;

//-------------------------------------------------------Constructor(s)--------------------------------------------------------------------
    Player(String firstName, String lastName, String position, String team, int ranking) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.team = team;
        this.ranking = ranking;
        this.baseValue = 1000 - (ranking * 5);
        this.adjValue = baseValue;
        this.isDrafted = false;
    }

//------------------------------------------------------Getters & Setters-------------------------------------------------------------------
    //Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPosition() {
        return position;
    }

    public String getTeam() {
        return team;
    }

    public int getRanking() {
        return ranking;
    }

    public int getBaseValue() {
        return baseValue;
    }
    public double getAdjValue() {
        return adjValue;
    }
    public boolean isDrafted() {
        return isDrafted;
    }

    //Setter 
    public void setAdjValue(double newVal) {
        this.adjValue = newVal;
    }
    public void setIsDrafted(){
        this.isDrafted = true;
    }

//-------------------------------------------------Public Operational Methods--------------------------------------------------------------
    public void displayPlayer() {
        System.out.println(this.team + " " + this.position + " - " + this.firstName + " " + this.lastName);
    }
    public void displayFirst() {
        System.out.printf("%-15s", firstName);
    }
    public void displayLast() {
        System.out.printf("%-15s", lastName);
    }




/* Defunct name printing methods
    public void displayFirst() {
        int target = 15;
        StringBuilder out = new StringBuilder(this.firstName);
        String end;
        if (out.length() < target) {
            end = applyPadding(out, target);
        }
        else {
            end = out.toString();
        }
        System.out.print(end);
    }
    public void displayLast() {
        int target = 15;
        StringBuilder out = new StringBuilder(this.lastName);
        String end;
        if (out.length() < target) {
            end = applyPadding(out, target);
        }
        else {
            end = out.toString();
        }
        System.out.print(end);
    }
    private String applyPadding(StringBuilder input, int target) {
        StringBuilder out = new StringBuilder();
        int length = input.length();
        int padding = target - length;
        int left = padding / 2;
        int right = padding - left;
        for (int i = 0; i < left; i++) {
            out.append(" ");
        }
        out.append(input);
        for (int i = 0; i < right; i++) {
            out.append(" ");
        }
        return out.toString();
    }
}
*/
}