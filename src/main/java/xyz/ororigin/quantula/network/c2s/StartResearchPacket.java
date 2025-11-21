// StartResearchPacket.java
package xyz.ororigin.quantula.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.data.ingame.ResearchDataManager;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.network.NetworkUtils;

public class StartResearchPacket implements IModPacket {
    private final String researchId;

    public StartResearchPacket(String researchId) {
        this.researchId = researchId;
    }

    public static StartResearchPacket decode(FriendlyByteBuf buffer) {
        return new StartResearchPacket(NetworkUtils.readString(buffer));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(researchId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null && player.server != null) {
            boolean success = ResearchDataManager.startResearch(player, player.serverLevel(), researchId);
            // 可以发送操作结果包回客户端
        }
    }
}