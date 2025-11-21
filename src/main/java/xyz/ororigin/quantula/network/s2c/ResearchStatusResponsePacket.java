// ResearchStatusResponsePacket.java
package xyz.ororigin.quantula.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.data.ingame.TeamResearchSavedData;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.network.NetworkUtils;

public record ResearchStatusResponsePacket(String researchId,
                                           TeamResearchSavedData.TeamResearchState.ResearchStatus status) implements IModPacket {

    public static ResearchStatusResponsePacket decode(FriendlyByteBuf buffer) {
        return new ResearchStatusResponsePacket(
                NetworkUtils.readString(buffer),
                TeamResearchSavedData.TeamResearchState.ResearchStatus.values()[buffer.readByte()]
        );
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(researchId);
        buffer.writeByte(status.ordinal());
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        // 客户端处理 - 更新UI显示
        // 这里应该调用客户端的UI更新方法
    }
}