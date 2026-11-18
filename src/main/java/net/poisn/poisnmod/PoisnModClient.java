package net.poisn.poisnmod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class PoisnModClient implements ClientModInitializer {
    public static final String MOD_ID = "poisnmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Map<String, MemberEntry> MEMBERS = new HashMap<>();

    @Override
    public void onInitializeClient() {
        loadMembers();
        LOGGER.info("PoisnMod loaded {} member entries.", MEMBERS.size());
    }

    private static void loadMembers() {
        try (var stream = PoisnModClient.class.getClassLoader().getResourceAsStream("poisn_members.json")) {
            if (stream == null) {
                LOGGER.error("Bundled poisn_members.json is missing.");
                return;
            }
            Type type = new TypeToken<List<MemberEntry>>() {}.getType();
            List<MemberEntry> entries = new Gson().fromJson(
                    new InputStreamReader(stream, StandardCharsets.UTF_8), type);
            if (entries == null) return;
            for (MemberEntry entry : entries) {
                if (entry != null && entry.player != null && !entry.player.isEmpty()) {
                    MEMBERS.put(entry.player, entry);
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Could not read bundled poisn_members.json", exception);
        }
    }

    public static MemberEntry find(String exactPlayerName) {
        return MEMBERS.get(exactPlayerName);
    }

    public static boolean isHugoServer() {
        Minecraft minecraft = Minecraft.getInstance();
        ServerData server = minecraft.getCurrentServer();
        if (server == null || server.ip == null) return false;
        return server.ip.toLowerCase(Locale.ROOT).contains("hugosmp.net");
    }

    public static final class MemberEntry {
        public String Player = "";
        public String UeberNamen = "";
        public String FarbeUeN = "";
        public String UnterNamen = "";
        public String FarbeUN = "";

        public String player() { return Player == null ? "" : Player; }
        public String above() { return UeberNamen == null ? "" : UeberNamen; }
        public String aboveColor() { return FarbeUeN == null ? "" : FarbeUeN; }
        public String below() { return UnterNamen == null ? "" : UnterNamen; }
        public String belowColor() { return FarbeUN == null ? "" : FarbeUN; }
    }
}
