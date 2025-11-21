// QuantumPacketHandler.java - 更新版本
package xyz.ororigin.quantula.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.ororigin.quantula.Quantula;
import xyz.ororigin.quantula.network.c2s.*;
import xyz.ororigin.quantula.network.s2c.OpenUIPacket;
import xyz.ororigin.quantula.network.s2c.ResearchProgressUpdatePacket;
import xyz.ororigin.quantula.network.s2c.ResearchStatusResponsePacket;
import xyz.ororigin.quantula.network.s2c.TeamResearchStateSyncPacket;

public class QuantumPacketHandler extends BasePacketHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";
    private final SimpleChannel networkChannel;
    private static QuantumPacketHandler instance;

    public QuantumPacketHandler(String modId) {
        // 创建网络通道，使用模组ID作为通道名
        this.networkChannel = createChannel(
                new ResourceLocation(modId, "main"),
                PROTOCOL_VERSION
        );
        instance = this;
    }

    @Override
    protected SimpleChannel getChannel() {
        return networkChannel;
    }

    @Override
    public void initialize() {
        // ========== 注册客户端→服务端包 ==========
        registerClientToServer(ChangeTeamNamePacket.class, ChangeTeamNamePacket::decode);
        registerClientToServer(CreateTeamPacket.class, CreateTeamPacket::decode);
        registerClientToServer(ResearchStatusRequestPacket.class, ResearchStatusRequestPacket::decode);
        registerClientToServer(StartResearchPacket.class, StartResearchPacket::decode);
        registerClientToServer(ClaimRewardPacket.class, ClaimRewardPacket::decode);

        // ========== 注册服务端→客户端包 ==========
        registerServerToClient(OpenUIPacket.class, OpenUIPacket::decode);
        registerServerToClient(ResearchStatusResponsePacket.class, ResearchStatusResponsePacket::decode);
        registerServerToClient(ResearchProgressUpdatePacket.class, ResearchProgressUpdatePacket::decode);
        registerServerToClient(TeamResearchStateSyncPacket.class, TeamResearchStateSyncPacket::decode);

        // 在这里继续注册更多包...
    }

    // 获取单例实例
    public static QuantumPacketHandler getInstance() {
        return instance;
    }
}