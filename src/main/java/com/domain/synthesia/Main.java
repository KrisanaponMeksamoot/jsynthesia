/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.domain.synthesia;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import com.domain.synthesia.midi.MidiPlayer;

public class Main {
    public static void main(String[] args) {
        Synthesia s = new Synthesia();
        File f = null;
        for (String arg : args) {
            if (arg.equals("--rgt-auto-play")) {
                MidiPlayer.rgtAutoPlayEnabled = true;
            } else if (arg.equals("--help")) {
                System.err.println(
                        "Option:\r\n  --rgt-auto-play  Enable Roblox Got Talent piano auto play\r\n  --help           Show help message and close.");
                return;
            } else {
                f = new File(arg);
            }
        }
        s.init();
        s.start();
        if (f != null)
            try {
                s.openFile(f);
                s.getPlayer().getSequencer().start();
            } catch (InvalidMidiDataException | IOException | MidiUnavailableException e) {
                s.reportError(e);
            }
    }
}
