package com.dorrisd.NucCafBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class Bootstrap {

    private static Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {
        // load the settings
        Properties settings = new Properties();
        File settingsFile = new File("server.properties");

        try {
            Properties defaults = new Properties();

            InputStream is = Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("server.properties");

            defaults.load(is);

            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
            }

            settings.load(new FileInputStream("server.properties"));

            for (Map.Entry<Object, Object> defaultValue : defaults.entrySet()) {
                settings.putIfAbsent(defaultValue.getKey(), defaultValue.getValue());
            }

            settings.store(new FileOutputStream("server.properties"), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JDABuilder builder = JDABuilder.createDefault(settings.getProperty("discord.token"));

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.listening("Hal's thoughts"));
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);

        JDA jda = builder.build();
        jda.updateCommands().addCommands(
                Commands.slash("destroyhalbot", "halbot will be destroyed")
        ).queue();
        jda.addEventListener(new MessageEvents());
        NucCafBot bot = new NucCafBot(jda);

        new Thread(() -> {
            while (inputScanner.hasNextLine()) {
                String input = inputScanner.nextLine();

                if (eq(input, "stop")) {
                    bot.shutdown();
                } else if (eq(input, "uptime", "gc", "stats", "statistics")) {
                    bot.reportStats();
                }
            }
        }).start();
    }

    public static boolean eq(String input, String cmd, String... aliases) {
        if (input.equalsIgnoreCase(cmd.toLowerCase())) return true;
        for (String alias : aliases) {
            if (input.equalsIgnoreCase(alias.toLowerCase())) return true;
        }
        return false;
    }
}
