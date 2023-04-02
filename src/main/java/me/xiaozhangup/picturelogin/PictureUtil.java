package me.xiaozhangup.picturelogin;

import com.bobacadodl.imgmessage.ImageChar;
import com.bobacadodl.imgmessage.ImageMessage;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.xiaozhangup.picturelogin.PictureLogin.plugin;

public class PictureUtil {

    public static final @NotNull Component clear = MiniMessage.miniMessage().deserialize("<newline><newline><newline><newline><newline><newline><newline><newline><newline><newline><newline><newline><newline><newline><newline><newline>");

    public PictureUtil() {
    }

    private URL newURL(String player_uuid, String player_name) {
        String url = null;
        url = PictureLogin.api
                .replace("%uuid%", player_uuid)
                .replace("%name%", player_name);
        try {
            return new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage getImage(Player player) throws IOException {

        File file = new File(plugin.dataDirectory.toFile(), player.getUniqueId().toString());

        BufferedImage bufferedImage;

        if (file.exists()) {
            bufferedImage = ImageIO.read(file);
            plugin.server.getScheduler().buildTask(plugin, () -> {
                try {
                    var stream = getBufferedImageByApi(player);
                    if (stream == null) return;
                    ImageIO.write(stream, "png", file);
                } catch (IOException ignored) {
                }
            }).schedule();
        } else {
            file.createNewFile();
            bufferedImage = getBufferedImageByApi(player);
            try {
                if (bufferedImage != null) {
                    ImageIO.write(bufferedImage, "png", file);
                } else {
                    bufferedImage = getFallback();
                }
            } catch (IOException ignored) {
            }
        }

        return bufferedImage;
    }

    @Nullable
    private BufferedImage getBufferedImageByApi(Player player) {
        URL head_image = newURL(player.getUniqueId().toString(), player.getUsername());

        // URL Formatted correctly.
        if (head_image != null) {
            try {
                //User-Agent is needed for HTTP requests
                HttpURLConnection connection = (HttpURLConnection) head_image.openConnection();
                connection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.setConnectTimeout(5000);
                return ImageIO.read(connection.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public BufferedImage getFallback() throws IOException {
        return ImageIO.read(PictureLogin.class.getResourceAsStream("/fallback.png"));
    }

    public ImageMessage getMessage(List<String> messages, BufferedImage image) {
        int imageDimensions = 8, count = 0;

        ImageMessage imageMessage = new ImageMessage(image, imageDimensions, getChar());
        String[] msg = new String[imageDimensions];

        for (String message : messages) {
            if (count > msg.length) {
                break;
            }
            msg[count++] = message;
        }

        while (count < imageDimensions) {
            msg[count++] = "";
        }

        return imageMessage.appendText(msg);
    }

    private char getChar() {
        try {
            return ImageChar.valueOf("BLOCK".toUpperCase()).getChar();
        } catch (IllegalArgumentException e) {
            return ImageChar.BLOCK.getChar();
        }
    }

    public ImageMessage createPictureMessage(Player player, List<String> messages) throws IOException {
        BufferedImage image = getImage(player);

        if (image == null) {
            return null;
        }

        return getMessage(messages, image);
    }

    public void sendImage(Player player) {
        PictureWrapper wrapper = new PictureWrapper(player);

        plugin.server.getScheduler().buildTask(plugin, wrapper).delay(1L, TimeUnit.SECONDS).schedule();
    }

}
