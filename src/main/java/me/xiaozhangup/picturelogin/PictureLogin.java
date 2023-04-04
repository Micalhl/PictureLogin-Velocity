package me.xiaozhangup.picturelogin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@Plugin(id = "picturelogin", name = "PictureLogin", version = "0.0.1", authors = {"xiaozhangup"})
public class PictureLogin {
    public static final String api = "https://minepic.org/avatar/8/%uuid%";
    public static final Yaml yaml = new Yaml();
    public static PictureUtil pictureUtil;
    public static PictureLogin plugin;
    public static List<String> message = List.of("HAPPYLAND", "Please set this in config file!");
    public final ProxyServer server;
    public final Logger logger;
    public final Path dataDirectory;

    @Inject
    public PictureLogin(ProxyServer server, Logger logger, @DataDirectory Path dataDirector) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirector;

        PictureLogin.pictureUtil = new PictureUtil();
        PictureLogin.plugin = this;

        if (!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdirs();
        }

        try {
            var f = new File(dataDirectory.toFile(), "config.yml");
            var image = new File(dataDirectory.toFile() + "/image");
            if (!image.exists()) {
                image.mkdirs();
            }

            createConfig(f);
            var stream = new FileInputStream(f);
            message = yaml.load(stream);

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void on(ServerConnectedEvent event) {
        if (event.getPreviousServer().isEmpty()) {
            pictureUtil.sendImage(event.getPlayer());
        }
    }

    private void createConfig(File f) throws IOException {
        InputStream is = PictureLogin.class.getResourceAsStream("/config.yml");
        File fp = new File(f.getParent());
        if (!fp.exists()) {
            fp.mkdirs();
        }
        if (!f.exists()) {
            f.createNewFile();
        } else {
            return;
        }
        OutputStream os = new FileOutputStream(f);
        int index = 0;
        byte[] bytes = new byte[1024];
        while ((index = is.read(bytes)) != -1) {
            os.write(bytes, 0, index);
        }
        os.flush();
        os.close();
        is.close();
    }
}
