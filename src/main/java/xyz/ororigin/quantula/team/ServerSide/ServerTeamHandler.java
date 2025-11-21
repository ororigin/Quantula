package xyz.ororigin.quantula.team.ServerSide;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * 服务端团队请求处理程序
 */
public class ServerTeamHandler {

    /**
     * 处理创建队伍请求
     */
    public static void handleCreateTeamRequest(ServerPlayer player, String teamName, String description, Color4I color) {
        if (player == null) return;

        try {
            // 获取玩家当前团队
            Optional<Team> currentTeamOpt = FTBTeamsAPI.api().getManager().getTeamForPlayer(player);
            if (currentTeamOpt.isEmpty()) {
                player.sendSystemMessage(Component.literal("无法获取您的团队信息"));
                return;
            }

            Team currentTeam = currentTeamOpt.get();

            // 验证前置条件
            if (!currentTeam.isPlayerTeam()) {
                player.sendSystemMessage(Component.literal("您已经在队伍中，无法创建新队伍"));
                return;
            }

            if (!currentTeam.isValid()) {
                player.sendSystemMessage(Component.literal("团队数据无效"));
                return;
            }

            // 验证名称
            if (teamName == null || teamName.trim().isEmpty()) {
                player.sendSystemMessage(Component.literal("队伍名称不能为空"));
                return;
            }

            if (teamName.length() > 32) {
                player.sendSystemMessage(Component.literal("队伍名称不能超过32个字符"));
                return;
            }

            // 创建队伍
            Team newTeam = currentTeam.createParty(description, color);

            // 设置队伍名称
            newTeam.setProperty(TeamProperties.DISPLAY_NAME, teamName.trim());
            newTeam.markDirty();


        } catch (Exception e) {
            player.sendSystemMessage(
                    Component.literal("创建队伍失败: " + e.getMessage())
            );
        }
    }

    /**
     * 处理修改队伍名称请求
     */
    public static void handleChangeTeamNameRequest(ServerPlayer player, String newName) {
        if (player == null) return;

        try {
            // 获取玩家当前团队
            Optional<Team> teamOpt = FTBTeamsAPI.api().getManager().getTeamForPlayer(player);
            if (teamOpt.isEmpty()) {
                player.sendSystemMessage(Component.literal("您不在任何队伍中"));
                return;
            }

            Team team = teamOpt.get();

            // 验证权限
            if (!team.getRankForPlayer(player.getUUID()).isOfficerOrBetter()) {
                player.sendSystemMessage(Component.literal("您没有权限修改队伍名称"));
                return;
            }

            // 验证名称
            if (newName == null || newName.trim().isEmpty()) {
                player.sendSystemMessage(Component.literal("队伍名称不能为空"));
                return;
            }

            if (newName.length() > 32) {
                player.sendSystemMessage(Component.literal("队伍名称不能超过32个字符"));
                return;
            }

            // 修改名称
            team.setProperty(TeamProperties.DISPLAY_NAME, newName.trim());
            team.markDirty();


        } catch (Exception e) {
            player.sendSystemMessage(
                    Component.literal("修改队伍名称失败: " + e.getMessage())
            );
        }
    }
}
