package net.poisn.poisnmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class PoisnModClient implements ClientModInitializer {
    public static final String MOD_ID = "poisnmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, MemberEntry> MEMBERS = new HashMap<>();
    private static final Path CONFIG_FILE = Path.of("config", "poisnmod-members.json");
    private static ModConfig config = new ModConfig();

    @Override
    public void onInitializeClient() {
        loadMembers();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "clan_hud"), PoisnModClient::renderClanHud);
        LOGGER.info("PoisnMod loaded {} clan members.", MEMBERS.size());
    }

    private static void loadMembers() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            if (Files.notExists(CONFIG_FILE)) {
                try (var stream = PoisnModClient.class.getClassLoader().getResourceAsStream("poisn_members.json")) {
                    if (stream == null) {
                        LOGGER.error("Bundled poisn_members.json is missing.");
                        return;
                    }
                    Files.copy(stream, CONFIG_FILE);
                }
            }
            Type type = new TypeToken<List<MemberEntry>>() {}.getType();
            List<MemberEntry> entries;
            try (var reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                entries = GSON.fromJson(reader, type);
            }
            MEMBERS.clear();
            if (entries != null) {
                for (MemberEntry entry : entries) {
                    if (entry != null && entry.Player != null && !entry.Player.isBlank()) {
                        MEMBERS.put(entry.Player.toLowerCase(Locale.ROOT), entry);
                    }
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Could not load config/poisnmod-members.json; trying bundled defaults.", exception);
            loadBundledMembers();
        }
    }

    private static void loadBundledMembers() {
        try (var stream = PoisnModClient.class.getClassLoader().getResourceAsStream("poisn_members.json")) {
            if (stream == null) return;
            Type type = new TypeToken<List<MemberEntry>>() {}.getType();
            List<MemberEntry> entries = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), type);
            if (entries != null) for (MemberEntry entry : entries) {
                if (entry != null && entry.Player != null && !entry.Player.isBlank())
                    MEMBERS.put(entry.Player.toLowerCase(Locale.ROOT), entry);
            }
        } catch (Exception exception) {
            LOGGER.error("Could not read bundled member list.", exception);
        }
    }

    public static MemberEntry find(String playerName) {
        if (playerName == null) return null;
        return MEMBERS.get(playerName.toLowerCase(Locale.ROOT));
    }

    public static boolean isHugoServer() {
        Minecraft minecraft = Minecraft.getInstance();
        ServerData server = minecraft.getCurrentServer();
        return server != null && server.ip != null
                && (server.ip.toLowerCase(Locale.ROOT).contains("hugosmp.net")
                || server.ip.toLowerCase(Locale.ROOT).contains("hugosmp"));
    }

    private static void renderClanHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!config.showOnlineHud || !isHugoServer() || minecraft.player == null || minecraft.level == null) return;

        List<String> online = new ArrayList<>();
        for (var player : minecraft.level.players()) {
            MemberEntry member = find(player.getGameProfile().name());
            if (member != null) online.add(member.role().isBlank() ? member.Player : member.role() + " " + member.Player);
        }
        if (online.isEmpty()) return;

        int x = 8, y = 8;
        graphics.text(minecraft.font, Component.literal("§aPOISN CLAN §7(" + online.size() + ")"), x, y, 0xFFFFFFFF, true);
        y += 12;
        int shown = Math.min(online.size(), config.maxHudMembers);
        for (int i = 0; i < shown; i++) {
            graphics.text(minecraft.font, Component.literal("§f" + online.get(i)), x, y, 0xFFFFFFFF, true);
            y += 10;
        }
        if (online.size() > shown) {
            graphics.text(minecraft.font, Component.literal("§7+" + (online.size() - shown) + " weitere"), x, y, 0xFFFFFFFF, true);
        }
    }

    public static final class ModConfig {
        public boolean showOnlineHud = true;
        public boolean showClanNameplates = true;
        public boolean highlightMembers = true;
        public int maxHudMembers = 8;
    }

    public static final class MemberEntry {
        public String Player = "";
        public String UeberNamen = "";
        public String FarbeUeN = "";
        public String UnterNamen = "";
        public String FarbeUN = "";
        public String Rang = "";
        public String Clan = "POISN";
        public String FarbeClan = "#55FF55";

        public String above() { return UeberNamen == null ? "" : UeberNamen; }
        public String aboveColor() { return FarbeUeN == null || FarbeUeN.isBlank() ? FarbeClan : FarbeUeN; }
        public String below() { return UnterNamen == null ? "" : UnterNamen; }
        public String belowColor() { return FarbeUN == null || FarbeUN.isBlank() ? FarbeClan : FarbeUN; }
        public String role() { return Rang == null ? "" : Rang; }
    }
}
