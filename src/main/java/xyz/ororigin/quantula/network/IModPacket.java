package xyz.ororigin.quantula.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 所有网络包必须实现的接口
 * 提供统一的编码、解码和处理流程
 */
public interface IModPacket {

    /**
     * 统一的包处理方法
     * 确保所有包都在主线程中安全处理
     */
    static <PACKET extends IModPacket> void handlePacket(PACKET message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            NetworkEvent.Context context = ctx.get();
            // 将包处理放入主游戏线程，避免线程安全问题
            context.enqueueWork(() -> message.handle(context));
            context.setPacketHandled(true);
        }
    }

    /**
     * 处理包的核心逻辑
     *
     * @param context 网络上下文，包含发送者等信息
     */
    void handle(NetworkEvent.Context context);

    /**
     * 将数据写入缓冲区（序列化）
     *
     * @param buffer 数据缓冲区
     */
    void encode(FriendlyByteBuf buffer);
}