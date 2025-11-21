package xyz.ororigin.quantula.data.files.structure;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResearchData {
    @SerializedName("research")
    private List<ResearchNode> research;

    @SerializedName("landmarks")
    private List<MileStone> milestone;

    // Getters and Setters
    public List<ResearchNode> getResearch() { return research; }
    public void setResearch(List<ResearchNode> research) { this.research = research; }

    public List<MileStone> getMileStone() { return milestone; }
    public void setLandmarks(List<MileStone> landmarks) { this.milestone = landmarks; }
}
