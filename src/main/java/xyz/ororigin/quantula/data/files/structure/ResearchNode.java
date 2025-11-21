package xyz.ororigin.quantula.data.files.structure;


import com.google.gson.annotations.SerializedName;

public class ResearchNode {
    @SerializedName("id")
    private String id;

    @SerializedName("dependence")
    private String[] dependence;

    @SerializedName("x")
    private int x;

    @SerializedName("y")
    private int y;

    @SerializedName("requireItem")
    private String requireItem;

    @SerializedName("rewardItem")
    private String rewardItem;

    @SerializedName("rewardCommand")
    private String rewardCommand;

    @SerializedName("researchLevel")
    private int researchLevel;

    @SerializedName("requireqb")
    private int requireQb;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String[] getDependence() { return dependence; }
    public void setDependence(String[] dependence) { this.dependence = dependence; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public String getRequireItem() { return requireItem; }
    public void setRequireItem(String requireItem) { this.requireItem = requireItem; }

    public String getRewardItem() { return rewardItem; }
    public void setRewardItem(String rewardItem) { this.rewardItem = rewardItem; }

    public String getRewardCommand() { return rewardCommand; }
    public void setRewardCommand(String rewardCommand) { this.rewardCommand = rewardCommand; }

    public int getResearchLevel() { return researchLevel; }
    public void setResearchLevel(int researchLevel) { this.researchLevel = researchLevel; }

    public int getRequireQb() { return requireQb; }
    public void setRequireQb(int requireQb) { this.requireQb = requireQb; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}