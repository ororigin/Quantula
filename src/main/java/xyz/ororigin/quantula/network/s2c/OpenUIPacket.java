package xyz.ororigin.quantula.network.s2c;

import icyllis.modernui.mc.MuiModApi;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.ororigin.quantula.network.IModPacket;
import xyz.ororigin.quantula.researchGUI.QuantulaResearchUI;

import static com.mojang.text2speech.Narrator.LOGGER;

/**
 * 打开主界面的网络包
 */
public class OpenUIPacket implements IModPacket {

    public OpenUIPacket() {
        // 空构造，用于反序列化
    }

    // 添加静态decode方法
    public static OpenUIPacket decode(FriendlyByteBuf buffer) {
        return new OpenUIPacket();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        // 在客户端主线程中打开界面
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                openMainUI();
            }
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        // 这个包不需要传输数据
    }

    /**
     * 在客户端打开主界面
     */
    private void openMainUI() {
        try {
            // 打开Modern UI界面
            try {
                MuiModApi.openScreen(new QuantulaResearchUI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to open main UI", e);
        }
    }
}