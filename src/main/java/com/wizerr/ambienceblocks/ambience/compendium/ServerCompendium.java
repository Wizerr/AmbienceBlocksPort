package com.wizerr.ambienceblocks.ambience.compendium;

import com.google.gson.JsonSyntaxException;
import com.wizerr.ambienceblocks.packets.PacketCompendium;
import com.wizerr.ambienceblocks.util.JsonUtil;
import com.wizerr.ambienceblocks.util.PacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class ServerCompendium extends BaseCompendium {
    private final Logger logger;

    private final static Field fieldStorage;
    static {
        fieldStorage = ObfuscationReflectionHelper.findField(MinecraftServer.class, "f_129744_");
        fieldStorage.setAccessible(true);
    }

    public static ServerCompendium instance;

    public ServerCompendium(Logger logger) {
        instance = this;
        this.logger = logger;
    }

    @SubscribeEvent
    public void worldLoad(ServerStartedEvent e) {
        try {
            LevelStorageSource.LevelStorageAccess storage = (LevelStorageSource.LevelStorageAccess) fieldStorage.get(e.getServer());
            init(storage.getWorldDir());
        } catch (IllegalAccessException illegalAccessException) {
            logger.error("Couldn't access the world's data.");
            illegalAccessException.printStackTrace();
        }
    }

    @SubscribeEvent
    public void worldSave(ServerStoppingEvent e) {
        try {
            LevelStorageSource.LevelStorageAccess storage = (LevelStorageSource.LevelStorageAccess) fieldStorage.get(e.getServer());
            end(storage.getWorldDir());
        } catch (IllegalAccessException illegalAccessException) {
            logger.error("Couldn't access the world's data.", illegalAccessException);
        }
        instance = null;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        ServerPlayer player = (ServerPlayer) e.getEntity();
        PacketHandler.NET.send(
                PacketDistributor.PLAYER.with(() -> player),
                new PacketCompendium(getAllEntries())
        );
    }
    //called when the world begins
    public void init(Path savePath) {
        File ambienceFolder = new File(savePath.toFile(), "ambience");
        if (!ambienceFolder.exists()){
            return;
        }

        File file = new File(ambienceFolder, "compendium.json");

        try{
            if(file.exists()) {
                String jsonInput = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                CompendiumEntry[] compendiumArray = JsonUtil.GSON.fromJson(jsonInput, CompendiumEntry[].class);
                addAllEntries(Arrays.asList(compendiumArray));
            }
        }
        catch (IOException e){
            logger.error("Failed IO.", e);
        } catch (JsonSyntaxException | NullPointerException e) {
            logger.error("JSON file failed to parse.", e);
        }
    }

    //called when the world ends, write the entries saved to disk
    public void end(Path savePath) {
        File ambienceFolder = new File(savePath.toFile(), "ambience");
        if (!ambienceFolder.exists()){
            if(size() != 0)
                ambienceFolder.mkdir();
            else
                return;
        }

        File file = new File(ambienceFolder, "compendium.json");

        try{
            FileUtils.writeStringToFile(file, JsonUtil.toJson(getAllEntries()), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            logger.error("Failed IO.", e);
        }
    }

    public void updateAllCompendiums() {
        PacketHandler.NET.send(PacketDistributor.ALL.noArg(), new PacketCompendium(getAllEntries()));
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("ServerCompendium");
        string.append("{");
        int i = 1;
        for(CompendiumEntry entry : getAllEntries()) {
            string.append(entry.getData().getSoundName());
            i++;
            if(i == getAllEntries().size())
            string.append(", ");
        }
        string.append("}");
        return string.toString();
    }
}
