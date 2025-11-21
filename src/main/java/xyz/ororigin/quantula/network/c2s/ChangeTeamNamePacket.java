package xyz.ororigin.quantula.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.network.NetworkUtils;
import xyz.ororigin.quantula.team.ServerSide.ServerTeamHandler;

/**
 * 客户端→服务端：修改队伍名称请求包
 */
public class ChangeTeamNamePacket implements IModPacket {

    private final String newName;

    public ChangeTeamNamePacket(String newName) {
        this.newName = newName;
    }

    // 修复：改为静态decode方法
    public static ChangeTeamNamePacket decode(FriendlyByteBuf buffer) {
        return new ChangeTeamNamePacket(NetworkUtils.readString(buffer));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerTeamHandler.handleChangeTeamNameRequest(context.getSender(), newName);
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(newName != null ? newName : "", 32767);
    }

    public String getNewName() {
        return newName;
    }
}