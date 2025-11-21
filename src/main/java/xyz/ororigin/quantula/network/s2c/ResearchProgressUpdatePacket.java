// ResearchProgressUpdatePacket.java
package xyz.ororigin.quantula.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.network.NetworkUtils;

public class ResearchProgressUpdatePacket implements IModPacket {
    private final String researchId;
    private final double progress; // 0.0 - 1.0
    private final String researchStationId; // 研究站ID

    public ResearchProgressUpdatePacket(String researchId, double progress, String researchStationId) {
        this.researchId = researchId;
        this.progress = progress;
        this.researchStationId = researchStationId;
    }

    public static ResearchProgressUpdatePacket decode(FriendlyByteBuf buffer) {
        return new ResearchProgressUpdatePacket(
                NetworkUtils.readString(buffer),
                buffer.readDouble(),
                NetworkUtils.readString(buffer)
        );
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(researchId);
        buffer.writeDouble(progress);
        buffer.writeUtf(researchStationId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        // 客户端处理 - 更新进度条显示
    }

    // Getter方法
    public String getResearchId() { return researchId; }
    public double getProgress() { return progress; }
    public String getResearchStationId() { return researchStationId; }
}