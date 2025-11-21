// TeamResearchSavedData.java - 队伍研究状态全局存储
package xyz.ororigin.quantula.data.ingame;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;

public class TeamResearchSavedData extends SavedData {
    private static final String NAME = "quantula_team_research";

    // 队伍研究状态：队伍ID -> 研究状态
    private final Map<String, TeamResearchState> teamResearchStates = new HashMap<>();

    public TeamResearchSavedData() {
        super();
    }

    public TeamResearchSavedData(CompoundTag tag) {
        this.load(tag);
    }

    // 获取或创建队伍研究状态
    public TeamResearchState getOrCreateTeamState(String teamId) {
        return teamResearchStates.computeIfAbsent(teamId, k -> {
            this.setDirty();
            return new TeamResearchState();
        });
    }

    // 获取队伍研究状态
    public Optional<TeamResearchState> getTeamState(String teamId) {
        return Optional.ofNullable(teamResearchStates.get(teamId));
    }

    // 移除队伍研究状态
    public void removeTeamState(String teamId) {
        if (teamResearchStates.remove(teamId) != null) {
            this.setDirty();
        }
    }

    // 获取所有队伍ID
    public Set<String> getAllTeamIds() {
        return Collections.unmodifiableSet(teamResearchStates.keySet());
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag teamsTag = new CompoundTag();

        for (Map.Entry<String, TeamResearchState> entry : teamResearchStates.entrySet()) {
            teamsTag.put(entry.getKey(), entry.getValue().serialize());
        }

        compound.put("teams", teamsTag);
        return compound;
    }

    private void load(CompoundTag compound) {
        teamResearchStates.clear();

        if (compound.contains("teams", Tag.TAG_COMPOUND)) {
            CompoundTag teamsTag = compound.getCompound("teams");

            for (String teamId : teamsTag.getAllKeys()) {
                CompoundTag teamTag = teamsTag.getCompound(teamId);
                TeamResearchState state = new TeamResearchState();
                state.deserialize(teamTag);
                teamResearchStates.put(teamId, state);
            }
        }
    }

    // 获取单例实例
    public static TeamResearchSavedData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(
                TeamResearchSavedData::new,
                TeamResearchSavedData::new,
                NAME
        );
    }

    // 队伍研究状态内部类
    public static class TeamResearchState {
        private final Set<String> completed = new HashSet<>();    // 已完成的研究
        private final Set<String> finished = new HashSet<>();     // 已完成但未领取奖励的研究
        private final Set<String> unlocked = new HashSet<>();     // 已解锁的研究
        private final Set<String> researching = new HashSet<>();  // 正在研究的研究

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();

            // 序列化各个集合
            tag.put("completed", createStringList(completed));
            tag.put("finished", createStringList(finished));
            tag.put("unlocked", createStringList(unlocked));
            tag.put("researching", createStringList(researching));

            return tag;
        }

        public void deserialize(CompoundTag tag) {
            completed.clear();
            finished.clear();
            unlocked.clear();
            researching.clear();

            // 反序列化各个集合
            if (tag.contains("completed", Tag.TAG_LIST)) {
                readStringList(tag.getList("completed", Tag.TAG_STRING), completed);
            }
            if (tag.contains("finished", Tag.TAG_LIST)) {
                readStringList(tag.getList("finished", Tag.TAG_STRING), finished);
            }
            if (tag.contains("unlocked", Tag.TAG_LIST)) {
                readStringList(tag.getList("unlocked", Tag.TAG_STRING), unlocked);
            }
            if (tag.contains("researching", Tag.TAG_LIST)) {
                readStringList(tag.getList("researching", Tag.TAG_STRING), researching);
            }
        }

        private ListTag createStringList(Set<String> strings) {
            ListTag list = new ListTag();
            for (String str : strings) {
                list.add(StringTag.valueOf(str));
            }
            return list;
        }

        private void readStringList(ListTag list, Set<String> target) {
            for (Tag tag : list) {
                target.add(tag.getAsString());
            }
        }

        // Getter 方法
        public Set<String> getCompleted() { return Collections.unmodifiableSet(completed); }
        public Set<String> getFinished() { return Collections.unmodifiableSet(finished); }
        public Set<String> getUnlocked() { return Collections.unmodifiableSet(unlocked); }
        public Set<String> getResearching() { return Collections.unmodifiableSet(researching); }

        // 状态操作方法
        public boolean completeResearch(String researchId) {
            if (researching.remove(researchId) && completed.add(researchId)) {
                return true;
            }
            return false;
        }

        public boolean finishResearch(String researchId) {
            if (researching.remove(researchId) && finished.add(researchId)) {
                return true;
            }
            return false;
        }

        public boolean startResearch(String researchId) {
            return unlocked.remove(researchId) && researching.add(researchId);
        }

        public boolean unlockResearch(String researchId) {
            return unlocked.add(researchId);
        }

        public boolean claimReward(String researchId) {
            return finished.remove(researchId) && completed.add(researchId);
        }

        public ResearchStatus getResearchStatus(String researchId) {
            if (completed.contains(researchId)) return ResearchStatus.COMPLETED;
            if (finished.contains(researchId)) return ResearchStatus.FINISHED;
            if (researching.contains(researchId)) return ResearchStatus.RESEARCHING;
            if (unlocked.contains(researchId)) return ResearchStatus.UNLOCKED;
            return ResearchStatus.LOCKED;
        }

        public enum ResearchStatus {
            LOCKED, UNLOCKED, RESEARCHING, FINISHED, COMPLETED
        }
    }
}