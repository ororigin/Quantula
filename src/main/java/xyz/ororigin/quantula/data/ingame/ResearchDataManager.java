// ResearchDataManager.java - 完整版本
package xyz.ororigin.quantula.data.ingame;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xyz.ororigin.quantula.team.ServerSide.ServerTeamManager;

public class ResearchDataManager {

    /**
     * 获取玩家所在队伍的研究状态
     */
    public static TeamResearchSavedData.TeamResearchState getPlayerTeamResearchState(ServerPlayer player, ServerLevel level) {
        String teamId = ServerTeamManager.getPlayerTeamId(player);
        if (teamId == null) {
            return null;
        }

        TeamResearchSavedData savedData = TeamResearchSavedData.get(level);
        return savedData.getOrCreateTeamState(teamId);
    }

    /**
     * 检查研究状态
     */
    public static TeamResearchSavedData.TeamResearchState.ResearchStatus getResearchStatus(
            ServerPlayer player, ServerLevel level, String researchId) {

        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return TeamResearchSavedData.TeamResearchState.ResearchStatus.LOCKED;
        }

        return state.getResearchStatus(researchId);
    }

    /**
     * 开始研究
     */
    public static boolean startResearch(ServerPlayer player, ServerLevel level, String researchId) {
        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return false;
        }

        boolean success = state.startResearch(researchId);
        if (success) {
            TeamResearchSavedData.get(level).setDirty();
        }

        return success;
    }

    /**
     * 完成研究
     */
    public static boolean finishResearch(ServerPlayer player, ServerLevel level, String researchId) {
        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return false;
        }

        boolean success = state.finishResearch(researchId);
        if (success) {
            TeamResearchSavedData.get(level).setDirty();
        }

        return success;
    }

    /**
     * 领取研究奖励
     */
    public static boolean claimReward(ServerPlayer player, ServerLevel level, String researchId) {
        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return false;
        }

        boolean success = state.claimReward(researchId);
        if (success) {
            TeamResearchSavedData.get(level).setDirty();
            // 这里可以添加奖励发放逻辑
            grantResearchReward(player, researchId);
        }

        return success;
    }

    /**
     * 解锁研究
     */
    public static boolean unlockResearch(ServerPlayer player, ServerLevel level, String researchId) {
        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return false;
        }

        boolean success = state.unlockResearch(researchId);
        if (success) {
            TeamResearchSavedData.get(level).setDirty();
        }

        return success;
    }

    /**
     * 获取队伍所有研究状态
     */
    public static TeamResearchSavedData.TeamResearchState getAllResearchState(ServerPlayer player, ServerLevel level) {
        return getPlayerTeamResearchState(player, level);
    }

    /**
     * 检查研究是否可开始
     */
    public static boolean canStartResearch(ServerPlayer player, ServerLevel level, String researchId) {
        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return false;
        }

        return state.getResearchStatus(researchId) == TeamResearchSavedData.TeamResearchState.ResearchStatus.UNLOCKED;
    }

    /**
     * 检查是否可以领取奖励
     */
    public static boolean canClaimReward(ServerPlayer player, ServerLevel level, String researchId) {
        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return false;
        }

        return state.getResearchStatus(researchId) == TeamResearchSavedData.TeamResearchState.ResearchStatus.FINISHED;
    }

    /**
     * 发放研究奖励
     */
    private static void grantResearchReward(ServerPlayer player, String researchId) {
        // TODO: 从研究配置中获取奖励并发放给玩家
        // 这里需要从研究配置文件中读取该研究的奖励

        // 示例奖励发放逻辑：
        // ResearchConfig researchConfig = ResearchConfigManager.getResearch(researchId);
        // if (researchConfig != null) {
        //     // 发放物品奖励
        //     if (researchConfig.getRewardItem() != null) {
        //         player.addItem(researchConfig.getRewardItem().copy());
        //     }
        //
        //     // 执行命令奖励
        //     if (researchConfig.getRewardCommand() != null) {
        //         player.server.getCommands().performPrefixedCommand(
        //             player.createCommandSourceStack(),
        //             researchConfig.getRewardCommand()
        //         );
        //     }
        // }

        // 临时示例：发送成功消息
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "成功领取研究 " + researchId + " 的奖励！"
        ));
    }

    /**
     * 重置队伍研究状态（用于调试或管理命令）
     */
    public static boolean resetTeamResearch(ServerPlayer player, ServerLevel level) {
        String teamId = ServerTeamManager.getPlayerTeamId(player);
        if (teamId == null) {
            return false;
        }

        TeamResearchSavedData savedData = TeamResearchSavedData.get(level);
        savedData.removeTeamState(teamId);
        savedData.setDirty();
        return true;
    }

    /**
     * 强制完成研究（用于调试或管理命令）
     */
    public static boolean forceCompleteResearch(ServerPlayer player, ServerLevel level, String researchId) {
        TeamResearchSavedData.TeamResearchState state = getPlayerTeamResearchState(player, level);
        if (state == null) {
            return false;
        }

        // 强制完成研究，无论当前状态如何
        state.getResearching().remove(researchId);
        state.getFinished().remove(researchId);
        state.getUnlocked().remove(researchId);
        boolean success = state.getCompleted().add(researchId);

        if (success) {
            TeamResearchSavedData.get(level).setDirty();
            grantResearchReward(player, researchId);
        }

        return success;
    }
}