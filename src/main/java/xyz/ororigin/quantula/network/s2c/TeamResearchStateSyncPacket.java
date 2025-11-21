// TeamResearchStateSyncPacket.java
package xyz.ororigin.quantula.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.data.ingame.TeamResearchSavedData;
import xyz.ororigin.quantula.network.IModPacket;

import java.util.HashSet;
import java.util.Set;

public class TeamResearchStateSyncPacket implements IModPacket {
    private final Set<String> completed;
    private final Set<String> finished;
    private final Set<String> unlocked;
    private final Set<String> researching;

    public TeamResearchStateSyncPacket(TeamResearchSavedData.TeamResearchState state) {
        this.completed = state.getCompleted();
        this.finished = state.getFinished();
        this.unlocked = state.getUnlocked();
        this.researching = state.getResearching();
    }

    public static TeamResearchStateSyncPacket decode(FriendlyByteBuf buffer) {
        Set<String> completed = readStringSet(buffer);
        Set<String> finished = readStringSet(buffer);
        Set<String> unlocked = readStringSet(buffer);
        Set<String> researching = readStringSet(buffer);

        // 创建一个临时的TeamResearchState来构建包
        var tempState = new TeamResearchSavedData.TeamResearchState();
        // 注意：这里只是数据传输，不调用业务方法
        return new TeamResearchStateSyncPacket(completed, finished, unlocked, researching);
    }

    private TeamResearchStateSyncPacket(Set<String> completed, Set<String> finished,
                                        Set<String> unlocked, Set<String> researching) {
        this.completed = completed;
        this.finished = finished;
        this.unlocked = unlocked;
        this.researching = researching;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        writeStringSet(buffer, completed);
        writeStringSet(buffer, finished);
        writeStringSet(buffer, unlocked);
        writeStringSet(buffer, researching);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        // 客户端处理 - 同步整个队伍的研究状态
    }

    // 辅助方法
    private static Set<String> readStringSet(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(buffer.readUtf());
        }
        return set;
    }

    private static void writeStringSet(FriendlyByteBuf buffer, Set<String> set) {
        buffer.writeInt(set.size());
        for (String str : set) {
            buffer.writeUtf(str);
        }
    }

    // Getter方法
    public Set<String> getCompleted() { return completed; }
    public Set<String> getFinished() { return finished; }
    public Set<String> getUnlocked() { return unlocked; }
    public Set<String> getResearching() { return researching; }
}