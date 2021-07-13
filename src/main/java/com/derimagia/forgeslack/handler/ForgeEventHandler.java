package com.derimagia.forgeslack.handler;

import com.derimagia.forgeslack.ForgeSlack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.text.MessageFormat;

public class ForgeEventHandler {
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        String message = new Composer(event.getMessage()).getOutput();
        ForgeSlack.getSlackRelay().sendMessage(message, event.getPlayer());
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        // @TODO: Localize this?
        ForgeSlack.getSlackRelay().sendMessage("_[Joined the Game]_", event.player);
    }

    @SubscribeEvent
    public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        // @TODO: Localize this?
        ForgeSlack.getSlackRelay().sendMessage("_[Left the Game]_", event.player);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !(event.getEntityLiving() instanceof FakePlayer) && !event.getEntity().world.isRemote) {
            ForgeSlack.getSlackRelay().sendMessage("_" + ((EntityPlayer) event.getEntity()).getCombatTracker().getDeathMessage().getUnformattedText() + "_", (EntityPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onPlayerReceiveAdvancement(AdvancementEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer && !(event.getEntityLiving() instanceof FakePlayer) && !event.getEntity().world.isRemote)) {
            return;
        }

        if (!(event.getAdvancement().getDisplay() != null && event.getAdvancement().getDisplay().shouldAnnounceToChat())) {
            return;
        }

        String achievementText = event.getAdvancement().getDisplayText().getUnformattedText();
        EntityPlayer player = event.getEntityPlayer();
        String playerName = getName(player);
        String msg = MessageFormat.format("_{0} has earned the achievement: {1}_", playerName, achievementText);

        ForgeSlack.getSlackRelay().sendMessage(msg, player);
    }

    private static String getName(EntityPlayer player) {
        return ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getDisplayName().getUnformattedText());
    }

    private class Composer {
        private final String States = "SVOUEIKNRLM";
        private final String Functions[] = {
            "VVVVVVVVVVVVVVVVVVVSSSSSSSSSSSSSS",
            "VVVVVVVVVVVVVVVVVVVIIIIIIIIIIIOUE",
            "KNLRLKLLLLLLLLLVVLVIISSSSSSSSISSS",
            "KNLRLKLLLLLLLLLVVLVSSSSIISSSSISSS",
            "KNLRLKLLLLLLLLLVVLVSSSSSSSSSSISSS",
            "KNLRLKLLLLLLLLLVVLVSSSSSSSSSSSSSS",
            "VVVVVVMVVVVVVVVVVVVIIIIIIIIIIIOUE",
            "VVVVVVVVMVVVVMVVVVVIIIIIIIIIIIOUE",
            "MVVVMMMVVVVMMMVVVVVIIIIIIIIIIIOUE",
            "VVVVVVVVVVVVVVVVVVVIIIIIIIIIIIOUE",
            "VVVVVVVVVVVVVVVVVVVIIIIIIIIIIIOUE"
        };
        private final String OutputFunctions[] = {
            "aaaaaaaaaaaaaaaaaaallllllllllllll",
            "bbbbbbbbbbbbbbbbbbbcccccccccccccc",
            "eeeeeeeeeeeeeeeggegddlllllllldlll",
            "eeeeeeeeeeeeeeeggegllllddlllldlll",
            "eeeeeeeeeeeeeeeggeglllllllllldlll",
            "eeeeeeeeeeeeeeeggegllllllllllllll",
            "iiiiiifiiiiiiiiiiiihhhhhhhhhhhhhh",
            "iiiiiiiifiiiifiiiiihhhhhhhhhhhhhh",
            "fiiifffiiiifffiiiiihhhhhhhhhhhhhh",
            "iiiiiiiiiiiiiiiiiiihhhhhhhhhhhhhh",
            "kkkkkkkkkkkkkkkkkkkjjjjjjjjjjjjjj"
        };
        private final String Jamo = "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎㄲㄸㅃㅆㅉㅏㅐㅑㅒㅓㅔㅕㅖㅛㅠㅣㅗㅜㅡ";

        private String Output = "";

        private char State;
        private char I, M, F;

        private Composer(String message) {
            ImeInitialize2kr();
            for (char ch : message.toCharArray()) {
                ImeEatSymbol2kr(ch);
            }
            Output += ImeGetComposingChar2kr();
        }

        private String getOutput() {
            return Output;
        }

        private char ComposeMadi(char i, char m, char f)
        {
            if (m == ' ' && f == ' ') {
                return i;
            } else if (f == ' ' && i == ' ') {
                return m;
            } else if (i == ' ' && m == ' ') {
                return f;
            } else {
                String is = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
                String ms = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
                String fs = " ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
                int ii = is.indexOf(i);
                int mi = ms.indexOf(m);
                int fi = fs.indexOf(f);
                return (char) ('가' + ((ii * 21) + mi) * 28 + fi);
            }
        }

        private void ImeInitialize2kr()
        {
            State = 'S';
            I = ' ';
            M = ' ';
            F = ' ';
        }

        private char ImeGetComposingChar2kr()
        {
            return ComposeMadi(I, M, F);
        }

        private void ImeEatSymbol2kr(char Symbol)
        {
            if (Jamo.indexOf(Symbol) < 0) {
                char last = ImeGetComposingChar2kr();
                if (last != ' ')
                    Output += last;
                Output += Symbol;
                ImeInitialize2kr();
                return;
            }

            int r = States.indexOf(State);
            int c = Jamo.indexOf(Symbol);
            State = Functions[r].charAt(c);

            char input = Jamo.charAt(c);
            char madi;
            switch (OutputFunctions[r].charAt(c)) {
                case 'a':
                    I = input;
                    M = F = ' ';
                    break;
                case 'b':
                    Output += I;
                    I = input;
                    break;
                case 'c':
                    M = input;
                    break;
                case 'd':
                    M = ComposeMedial(M, input);
                    break;
                case 'e':
                    F = input;
                    break;
                case 'f':
                    F = ComposeFinal(F, input);
                    break;
                case 'g':
                    madi = ComposeMadi(I, M, F);
                    if (madi != ' ')
                        Output += madi;
                    I = input;
                    M = F = ' ';
                    break;
                case 'h':
                    madi = ComposeMadi(I, M, ' ');
                    if (madi != ' ')
                        Output += madi;
                    I = F;
                    M = input;
                    F = ' ';
                    break;
                case 'i':
                    madi = ComposeMadi(I, M, F);
                    if (madi != ' ')
                        Output += madi;
                    I = input;
                    M = F = ' ';
                    break;
                case 'j':
                    String ab = DecomposeFinal(F);
                    char a = ab.charAt(0);
                    char b = ab.charAt(1);
                    madi = ComposeMadi(I, M, a);
                    if (madi != ' ')
                        Output += madi;
                    I = b;
                    M = input;
                    F = ' ';
                    break;
                case 'k':
                    madi = ComposeMadi(I, M, F);
                    if (madi != ' ')
                        Output += madi;
                    I = input;
                    M = F = ' ';
                    break;
                case 'l':
                    madi = ComposeMadi(I, M, F);
                    if (madi != ' ')
                        Output += madi;
                    Output += input;
                    I = M = F = ' ';
                    break;
            }
        }

        private char ComposeMedial(char a, char b)
        {
            if (a == 'ㅗ')
                if (b == 'ㅏ')
                    return 'ㅘ';
                else if (b == 'ㅐ')
                    return 'ㅙ';
                else
                    return 'ㅚ';
            else if (a == 'ㅜ')
                if (b == 'ㅓ')
                    return 'ㅝ';
                else if (b == 'ㅔ')
                    return 'ㅞ';
                else
                    return 'ㅟ';
            else
                return 'ㅢ';
        }

        private char ComposeFinal(char a, char b)
        {
            if (a == 'ㄱ')
                return 'ㄳ';
            else if (a == 'ㄴ')
                if (b == 'ㅈ')
                    return 'ㄵ';
                else
                    return 'ㄶ';
            else if (a == 'ㄹ')
                if (b == 'ㄱ')
                    return 'ㄺ';
                else if (b == 'ㅁ')
                    return 'ㄻ';
                else if (b == 'ㅂ')
                    return 'ㄼ';
                else if (b == 'ㅅ')
                    return 'ㄽ';
                else if (b == 'ㅌ')
                    return 'ㄾ';
                else if (b == 'ㅍ')
                    return 'ㄿ';
                else
                    return 'ㅀ';
            else
                return 'ㅄ';
        }

        private String DecomposeFinal(char c)
        {
            String ab = null;
            if (c == 'ㄳ') {
                ab = "ㄱㅅ";
            } else if (c == 'ㄵ') {
                ab = "ㄴㅈ";
            } else if (c == 'ㄶ') {
                ab = "ㄴㅎ";
            } else if (c == 'ㄺ') {
                ab = "ㄹㄱ";
            } else if (c == 'ㄻ') {
                ab = "ㄹㅁ";
            } else if (c == 'ㄼ') {
                ab = "ㄹㅂ";
            } else if (c == 'ㄽ') {
                ab = "ㄹㅅ";
            } else if (c == 'ㄾ') {
                ab = "ㄹㅌ";
            } else if (c == 'ㄿ') {
                ab = "ㄹㅍ";
            } else if (c == 'ㅀ') {
                ab = "ㄹㅎ";
            } else if (c == 'ㅄ') {
                ab = "ㅂㅅ";
            }
            return ab;
        }
    }
}
