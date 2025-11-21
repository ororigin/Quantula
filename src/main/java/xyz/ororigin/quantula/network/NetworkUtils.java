// NetworkUtils.java - 增强版本
package xyz.ororigin.quantula.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 网络工具类
 * 提供常用的序列化/反序列化方法
 */
public class NetworkUtils {

    /**
     * 安全读取字符串，避免长度问题
     */
    public static String readString(FriendlyByteBuf buffer) {
        return buffer.readUtf(32767); // Minecraft的最大字符串长度
    }

    /**
     * 读取三维向量
     */
    public static Vec3 readVector3d(FriendlyByteBuf buffer) {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    /**
     * 写入三维向量
     */
    public static void writeVector3d(FriendlyByteBuf buffer, Vec3 vector) {
        buffer.writeDouble(vector.x);
        buffer.writeDouble(vector.y);
        buffer.writeDouble(vector.z);
    }

    /**
     * 写入可选值（可为null）
     */
    public static <T> void writeOptional(FriendlyByteBuf buffer, @Nullable T value, BiConsumer<FriendlyByteBuf, T> writer) {
        if (value == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            writer.accept(buffer, value);
        }
    }

    /**
     * 读取可选值
     */
    @Nullable
    public static <T> T readOptional(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> reader) {
        return buffer.readBoolean() ? reader.apply(buffer) : null;
    }

    /**
     * 写入字符串集合
     */
    public static void writeStringSet(FriendlyByteBuf buffer, Set<String> set) {
        buffer.writeInt(set.size());
        for (String str : set) {
            buffer.writeUtf(str);
        }
    }

    /**
     * 读取字符串集合
     */
    public static Set<String> readStringSet(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(buffer.readUtf());
        }
        return set;
    }

    /**
     * 写入字符串列表
     */
    public static void writeStringList(FriendlyByteBuf buffer, List<String> list) {
        buffer.writeInt(list.size());
        for (String str : list) {
            buffer.writeUtf(str);
        }
    }

    /**
     * 读取字符串列表
     */
    public static List<String> readStringList(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(buffer.readUtf());
        }
        return list;
    }

    /**
     * 写入枚举值
     */
    public static <T extends Enum<T>> void writeEnum(FriendlyByteBuf buffer, T enumValue) {
        buffer.writeByte(enumValue.ordinal());
    }

    /**
     * 读取枚举值
     */
    public static <T extends Enum<T>> T readEnum(FriendlyByteBuf buffer, Class<T> enumClass) {
        return enumClass.getEnumConstants()[buffer.readByte()];
    }
}