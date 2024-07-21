package com.domain.synthesia;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import com.domain.synthesia.gui.Window;
import com.domain.synthesia.midi.MidiPlayer;

public class Synthesia {
    private static Synthesia instance;

    private Window window;

    private MidiPlayer player;
    private Synthesizer synthesizer;
    private Soundbank defaultSoundbank;
    private Soundbank currentSoundbank;

    public Synthesia() {
        instance = this;
    }

    public static Synthesia getInstance() {
        return instance;
    }

    public void init() {
        window = new Window(this);
        window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                close();
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }

    public void start() {
        window.setLocation(80, 80);
        window.start();
        try {
            synthesizer = MidiSystem.getSynthesizer();
            defaultSoundbank = synthesizer.getDefaultSoundbank();
            useDefaultSoundbank();
            synthesizer.open();
        } catch (MidiUnavailableException mue) {
            reportError(mue);
        }
    }

    public void openFile(File file) throws InvalidMidiDataException, IOException, MidiUnavailableException {
        if (player != null) {
            player.close();
        }
        player = new MidiPlayer(file);
        player.setPiano(window.getPiano());
        player.getSequencer().getTransmitter().setReceiver(synthesizer.getReceiver());
        player.open();
        window.getPiano().reset();
        window.update();
    }

    public void unloadMidi() {
        if (player != null) {
            player.close();
            player = null;
        }
    }

    public boolean setSoundbank(Soundbank soundbank) {
        if (currentSoundbank != null)
            synthesizer.unloadAllInstruments(currentSoundbank);
        boolean b = synthesizer.loadAllInstruments(soundbank);
        currentSoundbank = soundbank;
        return b;
    }

    public boolean useDefaultSoundbank() {
        return setSoundbank(defaultSoundbank);
    }

    public MidiPlayer getPlayer() {
        return player;
    }

    public Window getWindow() {
        return window;
    }

    public void close() {
        System.exit(0);
    }

    public void reportError(Throwable t) {
        t.printStackTrace();
        window.reportError(t.toString());
    }
}
