package com.dorrisd.NucCafBot;

import net.dv8tion.jda.api.JDA;

public class NucCafBot {
    private static JDA jda;

    public NucCafBot(JDA jda) {
        this.jda = jda;
    }

    public void shutdown() {
        jda.shutdown();
    }

    public void reportStats() {
        System.out.println("stats");
    }
}
