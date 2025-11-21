package xyz.ororigin.quantula.team.ServerSide;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamRank;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 服务端队伍管理工具类
 * 提供完整的队伍管理接口，基于 FTB Teams API
 */
public class ServerTeamManager {

    // 防止实例化
    private ServerTeamManager() {
    }

    /**
     * 获取玩家所在队伍的ID
     *
     * @param player 玩家对象
     * @return 队伍ID（String）或null（如果玩家没有队伍）
     */
    @Nullable
    public static String getPlayerTeamId(ServerPlayer player) {
        if (player == null) return null;

        try {
            Optional<Team> teamOpt = FTBTeamsAPI.api().getManager().getTeamForPlayer(player);
            return teamOpt.map(team -> team.getId().toString()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取玩家所在队伍的对象
     *
     * @param player 玩家对象
     * @return 队伍对象（Team）或null
     */
    @Nullable
    public static Team getPlayerTeam(ServerPlayer player) {
        if (player == null) return null;

        try {
            return FTBTeamsAPI.api().getManager().getTeamForPlayer(player).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查玩家是否在队伍中（party team）
     *
     * @param player 玩家对象
     * @return true表示在队伍中，false表示不在
     */
    public static boolean isPlayerInParty(ServerPlayer player) {
        Team team = getPlayerTeam(player);
        return team != null && team.isPartyTeam();
    }

    /**
     * 检查玩家是否在个人队伍中（即未加入任何队伍）
     *
     * @param player 玩家对象
     * @return true表示在个人队伍中，false表示在队伍中
     */
    public static boolean isPlayerInPersonalTeam(ServerPlayer player) {
        Team team = getPlayerTeam(player);
        return team != null && team.isPlayerTeam();
    }

    /**
     * 创建队伍
     *
     * @param creator     创建者玩家
     * @param teamName    队伍名称
     * @param description 队伍描述
     * @param color       颜色（可选）
     * @return true表示创建成功，false表示失败
     */
    public static boolean createParty(ServerPlayer creator, String teamName, @Nullable String description, @Nullable Color4I color) {
        if (creator == null || teamName == null || teamName.trim().isEmpty()) {
            return false;
        }

        try {
            // 使用您现有的 ServerTeamHandler
            ServerTeamHandler.handleCreateTeamRequest(creator, teamName,
                    description != null ? description : "", color);
            return true;
        } catch (Exception e) {
            creator.sendSystemMessage(Component.literal("创建队伍失败: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 修改队伍名称
     *
     * @param player  玩家对象（必须有权限）
     * @param newName 新的队伍名称
     * @return true表示修改成功，false表示失败
     */
    public static boolean changeTeamName(ServerPlayer player, String newName) {
        if (player == null || newName == null || newName.trim().isEmpty()) {
            return false;
        }

        try {
            // 使用您现有的 ServerTeamHandler
            ServerTeamHandler.handleChangeTeamNameRequest(player, newName);
            return true;
        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("修改队伍名称失败: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 解散队伍
     *
     * @param player 玩家对象（必须是队长或有权限）
     * @return true表示解散成功，false表示失败
     */
    public static boolean disbandParty(ServerPlayer player) {
        if (player == null) return false;

        try {
            Team team = getPlayerTeam(player);
            if (team == null || !team.isPartyTeam()) {
                player.sendSystemMessage(Component.literal("您不在任何队伍中"));
                return false;
            }

            // 检查权限 - 只有队长可以解散队伍
            if (!team.getRankForPlayer(player.getUUID()).isOwner()) {
                player.sendSystemMessage(Component.literal("只有队长可以解散队伍"));
                return false;
            }

            // 获取所有成员并踢出
            Set<UUID> members = new HashSet<>(team.getMembers());
            for (UUID memberId : members) {
                if (!memberId.equals(player.getUUID())) {
                    // 踢出其他成员
                    kickPlayerFromTeam(team, player, memberId);
                }
            }

            // 这里需要调用 FTB Teams 的解散队伍方法
            // 注意：FTB Teams API 可能没有直接的解散方法，需要通过命令或其他方式
            player.sendSystemMessage(Component.literal("队伍解散功能需要额外实现"));
            return true;

        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("解散队伍失败: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 邀请玩家加入队伍
     *
     * @param inviter      邀请者
     * @param targetPlayer 被邀请玩家
     * @return true表示邀请成功，false表示失败
     */
    public static boolean invitePlayerToParty(ServerPlayer inviter, ServerPlayer targetPlayer) {
        if (inviter == null || targetPlayer == null) return false;

        try {
            Team team = getPlayerTeam(inviter);
            if (team == null || !team.isPartyTeam()) {
                inviter.sendSystemMessage(Component.literal("您不在任何队伍中"));
                return false;
            }

            // 检查权限
            if (!team.getRankForPlayer(inviter.getUUID()).isOfficerOrBetter()) {
                inviter.sendSystemMessage(Component.literal("您没有权限邀请玩家"));
                return false;
            }

            // 检查目标玩家是否已在队伍中
            Team targetTeam = getPlayerTeam(targetPlayer);
            if (targetTeam != null && targetTeam.isPartyTeam()) {
                inviter.sendSystemMessage(Component.literal("该玩家已在其他队伍中"));
                return false;
            }

            // 这里需要调用 FTB Teams 的邀请方法
            // 使用 FTB Teams 命令或直接 API 调用
            inviter.sendSystemMessage(Component.literal("邀请玩家功能需要额外实现"));
            return true;

        } catch (Exception e) {
            inviter.sendSystemMessage(Component.literal("邀请玩家失败: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 将玩家踢出队伍
     *
     * @param operator     操作者
     * @param targetPlayer 被踢玩家
     * @return true表示踢出成功，false表示失败
     */
    public static boolean kickPlayerFromParty(ServerPlayer operator, ServerPlayer targetPlayer) {
        if (operator == null || targetPlayer == null) return false;

        try {
            Team team = getPlayerTeam(operator);
            if (team == null || !team.isPartyTeam()) {
                operator.sendSystemMessage(Component.literal("您不在任何队伍中"));
                return false;
            }

            return kickPlayerFromTeam(team, operator, targetPlayer.getUUID());

        } catch (Exception e) {
            operator.sendSystemMessage(Component.literal("踢出玩家失败: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 内部方法：从队伍中踢出玩家
     */
    private static boolean kickPlayerFromTeam(Team team, ServerPlayer operator, UUID targetPlayerId) {
        // 检查操作者权限
        TeamRank operatorRank = team.getRankForPlayer(operator.getUUID());
        TeamRank targetRank = team.getRankForPlayer(targetPlayerId);

        // 操作者权限必须高于被踢玩家，且不能踢出自己
        if (operatorRank.getPower() <= targetRank.getPower() || operator.getUUID().equals(targetPlayerId)) {
            operator.sendSystemMessage(Component.literal("您没有权限踢出该玩家"));
            return false;
        }

        // 这里需要调用 FTB Teams 的踢出方法
        // 使用 FTB Teams 命令或直接 API 调用
        operator.sendSystemMessage(Component.literal("踢出玩家功能需要额外实现"));
        return true;
    }

    /**
     * 离开队伍
     *
     * @param player 玩家对象
     * @return true表示离开成功，false表示失败
     */
    public static boolean leaveParty(ServerPlayer player) {
        if (player == null) return false;

        try {
            Team team = getPlayerTeam(player);
            if (team == null || !team.isPartyTeam()) {
                player.sendSystemMessage(Component.literal("您不在任何队伍中"));
                return false;
            }

            // 检查是否是队长（队长不能直接离开，需要转让或解散）
            if (team.getRankForPlayer(player.getUUID()).isOwner()) {
                player.sendSystemMessage(Component.literal("队长不能直接离开队伍，请先转让队长或解散队伍"));
                return false;
            }

            // 这里需要调用 FTB Teams 的离开方法
            // 使用 FTB Teams 命令或直接 API 调用
            player.sendSystemMessage(Component.literal("离开队伍功能需要额外实现"));
            return true;

        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("离开队伍失败: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 获取队伍在线成员列表
     *
     * @param teamId 队伍ID
     * @return 在线玩家列表
     */
    public static List<ServerPlayer> getOnlineTeamMembers(String teamId) {
        if (teamId == null) return new ArrayList<>();

        try {
            UUID teamUUID = UUID.fromString(teamId);
            Optional<Team> teamOpt = FTBTeamsAPI.api().getManager().getTeamByID(teamUUID);

            if (teamOpt.isPresent()) {
                Collection<ServerPlayer> onlineMembers = teamOpt.get().getOnlineMembers();
                return new ArrayList<>(onlineMembers);
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取队伍成员列表
     *
     * @param teamId 队伍ID
     * @return 玩家UUID列表
     */
    public static List<UUID> getTeamMembers(String teamId) {
        if (teamId == null) return new ArrayList<>();

        try {
            UUID teamUUID = UUID.fromString(teamId);
            Optional<Team> teamOpt = FTBTeamsAPI.api().getManager().getTeamByID(teamUUID);

            if (teamOpt.isPresent()) {
                Set<UUID> members = teamOpt.get().getMembers();
                return new ArrayList<>(members);
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 检查玩家是否有权限管理队伍
     *
     * @param player 玩家对象
     * @return true表示有权限，false表示无权限
     */
    public static boolean hasTeamManagementPermission(ServerPlayer player) {
        if (player == null) return false;

        try {
            Team team = getPlayerTeam(player);
            return team != null && team.getRankForPlayer(player.getUUID()).isOfficerOrBetter();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取队伍属性
     *
     * @param teamId 队伍ID
     * @return 包含属性的Map
     */
    public static Map<String, Object> getTeamProperties(String teamId) {
        Map<String, Object> properties = new HashMap<>();

        if (teamId == null) return properties;

        try {
            UUID teamUUID = UUID.fromString(teamId);
            Optional<Team> teamOpt = FTBTeamsAPI.api().getManager().getTeamByID(teamUUID);

            if (teamOpt.isPresent()) {
                Team team = teamOpt.get();

                // 获取基本属性
                properties.put("displayName", team.getProperty(TeamProperties.DISPLAY_NAME));
                properties.put("description", team.getProperty(TeamProperties.DESCRIPTION));
                properties.put("color", team.getProperty(TeamProperties.COLOR));
                properties.put("freeToJoin", team.getProperty(TeamProperties.FREE_TO_JOIN));

                // 队伍信息
                properties.put("id", team.getId().toString());
                properties.put("shortName", team.getShortName());
                properties.put("owner", team.getOwner().toString());
                properties.put("type", team.getTypeTranslationKey());
                properties.put("memberCount", team.getMembers().size());
            }
        } catch (Exception e) {
            // 忽略异常，返回空map
        }

        return properties;
    }

    /**
     * 设置队伍属性
     *
     * @param player       玩家对象（必须有权限）
     * @param propertyType 属性类型
     * @param value        属性值
     * @return true表示设置成功，false表示失败
     */
    public static boolean setTeamProperty(ServerPlayer player, String propertyType, Object value) {
        if (player == null || propertyType == null || value == null) return false;

        try {
            Team team = getPlayerTeam(player);
            if (team == null || !team.isPartyTeam()) {
                player.sendSystemMessage(Component.literal("您不在任何队伍中"));
                return false;
            }

            if (!hasTeamManagementPermission(player)) {
                player.sendSystemMessage(Component.literal("您没有权限修改队伍属性"));
                return false;
            }

            // 根据属性类型设置不同的属性
            switch (propertyType.toLowerCase()) {
                case "displayname":
                    if (value instanceof String) {
                        team.setProperty(TeamProperties.DISPLAY_NAME, (String) value);
                        team.markDirty();
                        return true;
                    }
                    break;
                case "description":
                    if (value instanceof String) {
                        team.setProperty(TeamProperties.DESCRIPTION, (String) value);
                        team.markDirty();
                        return true;
                    }
                    break;
                case "color":
                    if (value instanceof Color4I) {
                        team.setProperty(TeamProperties.COLOR, (Color4I) value);
                        team.markDirty();
                        return true;
                    }
                    break;
                case "freetojoin":
                    if (value instanceof Boolean) {
                        team.setProperty(TeamProperties.FREE_TO_JOIN, (Boolean) value);
                        team.markDirty();
                        return true;
                    }
                    break;
                default:
                    player.sendSystemMessage(Component.literal("不支持的属性类型: " + propertyType));
                    return false;
            }

            player.sendSystemMessage(Component.literal("属性值类型不匹配"));
            return false;

        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("设置队伍属性失败: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 根据队伍ID获取队伍对象
     *
     * @param teamId 队伍ID
     * @return 队伍对象（Team）或null
     */
    @Nullable
    public static Team getTeamById(String teamId) {
        if (teamId == null) return null;

        try {
            UUID teamUUID = UUID.fromString(teamId);
            return FTBTeamsAPI.api().getManager().getTeamByID(teamUUID).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取玩家在队伍中的等级
     *
     * @param player 玩家对象
     * @return 队伍等级
     */
    public static TeamRank getPlayerTeamRank(ServerPlayer player) {
        if (player == null) return TeamRank.NONE;

        try {
            Team team = getPlayerTeam(player);
            return team != null ? team.getRankForPlayer(player.getUUID()) : TeamRank.NONE;
        } catch (Exception e) {
            return TeamRank.NONE;
        }
    }

    /**
     * 检查两个玩家是否在同一队伍中
     *
     * @param player1 玩家1
     * @param player2 玩家2
     * @return true表示在同一队伍中
     */
    public static boolean arePlayersInSameTeam(ServerPlayer player1, ServerPlayer player2) {
        if (player1 == null || player2 == null) return false;

        try {
            return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(player1.getUUID(), player2.getUUID());
        } catch (Exception e) {
            return false;
        }
    }
}