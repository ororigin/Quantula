package xyz.ororigin.quantula.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import xyz.ororigin.quantula.Quantula;
import xyz.ororigin.quantula.network.s2c.OpenUIPacket;
import xyz.ororigin.quantula.team.TeamManageUtils;

public class QuantulaCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("quantula")
                .requires(cs -> cs.hasPermission(2))
                .then(debug.register());


    }

    private static class debug {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("debug")
                    .then(Commands.literal("nowTeamType") // 第二级子命令
                            .executes(context -> {
                                context.getSource().sendSystemMessage(Component.literal(String.valueOf(TeamManageUtils.isInParty())));
                                return 1;
                            }))
                    .then(Commands.literal("openUI") // 第二级子命令
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                // 发送打开界面的网络包
                                Quantula.getPacketHandler().sendToPlayer(new OpenUIPacket(), player);

                                return 1;
                            }));
        }
    }
}
