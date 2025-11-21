package xyz.ororigin.quantula.network.c2s;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.network.NetworkUtils;
import xyz.ororigin.quantula.team.ServerSide.ServerTeamHandler;

/**
 * 客户端→服务端：创建队伍请求包
 */
public class CreateTeamPacket implements IModPacket {

    private final String teamName;
    private final String description;
    private final Color4I color;

    public CreateTeamPacket(String teamName, String description, Color4I color) {
        this.teamName = teamName;
        this.description = description;
        this.color = color;
    }

    // 修复：改为静态decode方法
    public static CreateTeamPacket decode(FriendlyByteBuf buffer) {
        return new CreateTeamPacket(
                NetworkUtils.readString(buffer),
                NetworkUtils.readString(buffer),
                readColor4I(buffer)
        );
    }

    // 修复：改为静态方法
    private static Color4I readColor4I(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            int rgb = buffer.readInt();
            return Color4I.rgb(rgb);
        }
        return null;
    }

    // 修复：改为静态方法
    private static void writeColor4I(FriendlyByteBuf buffer, Color4I color) {
        if (color != null) {
            buffer.writeBoolean(true);
            buffer.writeInt(color.rgb());
        } else {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        // 在服务端处理创建队伍逻辑
        context.enqueueWork(() -> {
            ServerTeamHandler.handleCreateTeamRequest(context.getSender(), teamName, description, color);
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(teamName != null ? teamName : "", 32767);
        buffer.writeUtf(description != null ? description : "", 32767);
        writeColor4I(buffer, color);
    }

    public String getTeamName() {
        return teamName;
    }

    public String getDescription() {
        return description;
    }

    public Color4I getColor() {
        return color;
    }
}