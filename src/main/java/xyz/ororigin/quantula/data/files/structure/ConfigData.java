package xyz.ororigin.quantula.data.files.structure;

import com.google.gson.annotations.SerializedName;

public class ConfigData {
    @SerializedName("previousXLocation")
    private int previousXLocation;

    @SerializedName("previousYLocation")
    private int previousYLocation;

    // Constructors
    public ConfigData() {
        this.previousXLocation = 0;
        this.previousYLocation = 0;
    }

    public ConfigData(int x, int y) {
        this.previousXLocation = x;
        this.previousYLocation = y;
    }

    // Getters and Setters
    public int getPreviousXLocation() { return previousXLocation; }
    public void setPreviousXLocation(int previousXLocation) { this.previousXLocation = previousXLocation; }

    public int getPreviousYLocation() { return previousYLocation; }
    public void setPreviousYLocation(int previousYLocation) { this.previousYLocation = previousYLocation; }
}