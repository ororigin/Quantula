package xyz.ororigin.quantula.data.files.structure;

import com.google.gson.annotations.SerializedName;

public class MileStone {
    @SerializedName("name")
    private String name;

    @SerializedName("x")
    private int x;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
}