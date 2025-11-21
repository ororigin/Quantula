// ResearchStatusRequestPacket.java
package xyz.ororigin.quantula.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.Quantula;
import xyz.ororigin.quantula.data.ingame.ResearchDataManager;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.network.NetworkUtils;
import xyz.ororigin.quantula.network.s2c.ResearchStatusResponsePacket;

public class ResearchStatusRequestPacket implements IModPacket {
    private final String researchId;

    public ResearchStatusRequestPacket(String researchId) {
        this.researchId = researchId;
    }

    public static ResearchStatusRequestPacket decode(FriendlyByteBuf buffer) {
        return new ResearchStatusRequestPacket(NetworkUtils.readString(buffer));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(researchId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null && player.server != null) {
            // 获取研究状态并发送响应
            var status = ResearchDataManager.getResearchStatus(player, player.serverLevel(), researchId);
            Quantula.getPacketHandler().sendToPlayer(
                    new ResearchStatusResponsePacket(researchId, status),
                    player
            );
        }
    }
}