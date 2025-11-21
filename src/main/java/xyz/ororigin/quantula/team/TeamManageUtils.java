package xyz.ororigin.quantula.team;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import xyz.ororigin.quantula.Quantula;
import xyz.ororigin.quantula.network.QuantumPacketHandler;
import xyz.ororigin.quantula.network.c2s.ChangeTeamNamePacket;
import xyz.ororigin.quantula.network.c2s.CreateTeamPacket;

/**
 * 客户端团队管理工具类
 * 所有操作都通过向服务端发送网络包实现
 */
public class TeamManageUtils {
    private static final Minecraft MC = Minecraft.getInstance();
    private static QuantumPacketHandler PACKET_HANDLER;

    /**
     * 初始化网络包处理器
     */
    public static void setPacketHandler(QuantumPacketHandler handler) {
        PACKET_HANDLER = handler;
    }

    /**
     * 检查客户端是否在队伍中
     */
    public static boolean isInParty() {
        if (!isClientEnvironment()) return false;

        try {
            Team playerTeam = FTBTeamsAPI.api().getClientManager().selfTeam();
            return playerTeam != null && playerTeam.isValid() && playerTeam.isPartyTeam();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查客户端是否在个人团队中
     */
    public static boolean isInPersonalTeam() {
        if (!isClientEnvironment()) return false;

        try {
            Team playerTeam = FTBTeamsAPI.api().getClientManager().selfTeam();
            return playerTeam != null && playerTeam.isValid() && playerTeam.isPlayerTeam();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 向服务端发送创建队伍请求
     */
    public static boolean requestCreateTeam(String partyName, String partyDescription, @Nullable Color4I color) {
        if (!validateClientEnvironment("创建队伍")) return false;

        // 输入验证
        if (partyName == null || partyName.trim().isEmpty()) {
            return false;
        }

        if (partyName.length() > 32) {
            return false;
        }

        if (!isInPersonalTeam()) {
            sendClientMessage("您已经在队伍中，无法创建新队伍");
            return false;
        }

        // 直接使用 Quantula.getPacketHandler() 获取包处理器
        QuantumPacketHandler packetHandler = Quantula.getPacketHandler();
        if (packetHandler != null) {
            CreateTeamPacket packet = new CreateTeamPacket(
                    partyName.trim(),
                    partyDescription != null ? partyDescription : "",
                    color
            );
            packetHandler.sendToServer(packet);
            return true;
        } else {
            sendClientMessage("网络通信未初始化");
            // 添加调试信息
            System.err.println("DEBUG: Packet handler is null in requestCreateTeam");
            return false;
        }
    }

    /**
     * 向服务端发送修改队伍名称请求
     */
    public static boolean requestChangeTeamName(String newName) {
        if (!validateClientEnvironment("修改队伍名称")) return false;

        if (!isInParty()) {
            sendClientMessage("您不在任何队伍中");
            return false;
        }

        if (newName == null || newName.trim().isEmpty()) {
            sendClientMessage("队伍名称不能为空");
            return false;
        }

        if (newName.length() > 32) {
            sendClientMessage("队伍名称不能超过32个字符");
            return false;
        }

        if (PACKET_HANDLER != null) {
            ChangeTeamNamePacket packet = new ChangeTeamNamePacket(newName.trim());
            PACKET_HANDLER.sendToServer(packet);
            return true;
        } else {
            sendClientMessage("网络通信未初始化");
            return false;
        }
    }

    /**
     * 获取当前队伍名称
     */
    @Nullable
    public static String getCurrentTeamName() {
        if (!isInParty()) return null;

        try {
            Team team = FTBTeamsAPI.api().getClientManager().selfTeam();
            return team.getProperty(TeamProperties.DISPLAY_NAME);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取队伍成员数量
     */
    public static int getTeamMemberCount() {
        if (!isInParty()) return 0;

        try {
            Team team = FTBTeamsAPI.api().getClientManager().selfTeam();
            return team.getMembers().size();
        } catch (Exception e) {
            return 0;
        }
    }

    // ========== 私有工具方法 ==========

    /**
     * 检查是否为客户端环境
     */
    private static boolean isClientEnvironment() {
        return MC.level != null && MC.player != null;
    }

    /**
     * 验证客户端环境并发送错误消息
     */
    private static boolean validateClientEnvironment(String operation) {
        if (!isClientEnvironment()) {
            sendClientMessage(operation + "失败: 只能在客户端环境中使用");
            return false;
        }

        if (!FTBTeamsAPI.api().isClientManagerLoaded()) {
            sendClientMessage(operation + "失败: 团队管理器未加载");
            return false;
        }

        return true;
    }

    /**
     * 向客户端发送聊天消息
     */
    private static void sendClientMessage(String message) {
        if (MC.player != null) {
            MC.player.displayClientMessage(Component.literal(message), false);
        }
    }

    // 防止实例化
    private TeamManageUtils() {}
}