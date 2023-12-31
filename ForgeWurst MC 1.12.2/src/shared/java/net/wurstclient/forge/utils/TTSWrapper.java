package net.wurstclient.forge.utils;

import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.NarratorWindows;

public class TTSWrapper {

    public static Narrator narrator = new NarratorWindows();

    public void TTS() {

    }

    public static void say(String message) {
        narrator.say(message);
    }

    public static void clear() {
        narrator.clear();
    }

    public static boolean active() {
        return narrator.active();
    }
}
