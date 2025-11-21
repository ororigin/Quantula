package xyz.ororigin.quantula.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.function.Function;

/**
 * 网络包处理器基类
 * 提供包的注册和发送方法
 */
public abstract class BasePacketHandler {

    private int packetId = 0; // 包ID计数器

    /**
     * 创建网络通道
     */
    protected static SimpleChannel createChannel(ResourceLocation channelName, String version) {
        return NetworkRegistry.ChannelBuilder.named(channelName)
                .clientAcceptedVersions(version::equals)
                .serverAcceptedVersions(version::equals)
                .networkProtocolVersion(() -> version)
                .simpleChannel();
    }

    /**
     * 获取网络通道
     */
    protected abstract SimpleChannel getChannel();

    /**
     * 初始化方法，在子类中注册所有包
     */
    public abstract void initialize();

    /**
     * 注册客户端→服务端的包
     */
    protected <PACKET extends IModPacket> void registerClientToServer(Class<PACKET> packetClass, Function<FriendlyByteBuf, PACKET> decoder) {
        registerPacket(packetClass, decoder, NetworkDirection.PLAY_TO_SERVER);
    }

    /**
     * 注册服务端→客户端的包
     */
    protected <PACKET extends IModPacket> void registerServerToClient(Class<PACKET> packetClass, Function<FriendlyByteBuf, PACKET> decoder) {
        registerPacket(packetClass, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * 内部注册方法
     */
    private <PACKET extends IModPacket> void registerPacket(Class<PACKET> packetClass, Function<FriendlyByteBuf, PACKET> decoder, NetworkDirection direction) {
        getChannel().registerMessage(
                packetId++,
                packetClass,
                IModPacket::encode,     // 统一的编码方法
                decoder,                // 包特定的解码方法
                IModPacket::handlePacket, // 统一的处理方法
                Optional.of(direction)  // 包方向
        );
    }

    // ========== 包发送方法 ==========

    /**
     * 发送包到服务器
     */
    public <PACKET extends IModPacket> void sendToServer(PACKET packet) {
        getChannel().sendToServer(packet);
    }

    /**
     * 发送包给特定玩家
     */
    public <PACKET extends IModPacket> void sendToPlayer(PACKET packet, ServerPlayer player) {
        if (!(player instanceof FakePlayer)) { // 避免发送给假玩家
            getChannel().send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    /**
     * 发送包给所有玩家
     */
    public <PACKET extends IModPacket> void sendToAll(PACKET packet) {
        getChannel().send(PacketDistributor.ALL.noArg(), packet);
    }

    /**
     * 发送包给特定维度的所有玩家
     */
    public <PACKET extends IModPacket> void sendToDimension(PACKET packet, ResourceKey<Level> dimension) {
        getChannel().send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
    }

    /**
     * 发送包给所有追踪实体的玩家
     */
    public <PACKET extends IModPacket> void sendToAllTracking(PACKET packet, Entity entity) {
        getChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
    }

    /**
     * 发送包给所有追踪实体的玩家（包括自己）
     */
    public <PACKET extends IModPacket> void sendToAllTrackingAndSelf(PACKET packet, Entity entity) {
        getChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
    }

    /**
     * 发送包给所有追踪方块实体的玩家
     */
    public <PACKET extends IModPacket> void sendToAllTracking(PACKET packet, BlockEntity blockEntity) {
        sendToAllTracking(packet, blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    /**
     * 发送包给所有追踪特定位置的玩家
     */
    public <PACKET extends IModPacket> void sendToAllTracking(PACKET packet, Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            // 优化：直接获取追踪该区块的玩家
            serverLevel.getChunkSource().chunkMap.getPlayers(
                    new net.minecraft.world.level.ChunkPos(pos), false
            ).forEach(player -> sendToPlayer(packet, player));
        } else {
            // 备用方法
            getChannel().send(PacketDistributor.TRACKING_CHUNK.with(() ->
                    level.getChunkAt(pos)), packet);
        }
    }

    /**
     * 服务器启动后发送包给所有玩家
     */
    public <PACKET extends IModPacket> void sendToAllIfLoaded(PACKET packet) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            sendToAll(packet);
        }
    }
}