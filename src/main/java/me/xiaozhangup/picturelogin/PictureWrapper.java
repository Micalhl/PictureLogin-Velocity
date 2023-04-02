package me.xiaozhangup.picturelogin;

import com.bobacadodl.imgmessage.ImageMessage;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.io.IOException;

public class PictureWrapper implements Runnable {
    public final static PictureUtil pictureUtil = new PictureUtil();
    private final Player player;

    public PictureWrapper(Player player) {
        this.player = player;
    }

    @Override
    public void run() {

        ImageMessage pictureMessage = null;
        player.sendMessage(PictureUtil.clear);
        try {
            pictureMessage = getMessage();
        } catch (IOException ignored) {
        }
        if (pictureMessage == null) {
            return;
        }

        pictureMessage.sendToPlayer(player);
        player.sendMessage(Component.empty());
    }

    private ImageMessage getMessage() throws IOException {
        return pictureUtil.createPictureMessage(player, PictureLogin.message);
    }

}
