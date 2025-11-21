// ClaimRewardPacket.java
package xyz.ororigin.quantula.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.data.ingame.ResearchDataManager;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.network.NetworkUtils;

public class ClaimRewardPacket implements IModPacket {
    private final String researchId;

    public ClaimRewardPacket(String researchId) {
        this.researchId = researchId;
    }

    public static ClaimRewardPacket decode(FriendlyByteBuf buffer) {
        return new ClaimRewardPacket(NetworkUtils.readString(buffer));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(researchId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null && player.server != null) {
            boolean success = ResearchDataManager.claimReward(player, player.serverLevel(), researchId);
            // 处理奖励发放
        }
    }
}