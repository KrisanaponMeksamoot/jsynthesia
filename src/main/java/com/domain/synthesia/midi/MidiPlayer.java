package com.domain.synthesia.midi;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import com.domain.synthesia.gui.Piano;
import com.domain.synthesia.piano.NoteMap;

public class MidiPlayer {
    public static boolean rgtAutoPlayEnabled = false;

    private final Sequence sequence;
    private final Sequencer sequencer;
    private Piano piano;
    public final NoteMap fallingNotes;

    private RgtAutoPlay rgtAutoPlay;

    public MidiPlayer(File file) throws InvalidMidiDataException, IOException, MidiUnavailableException {
        this(MidiSystem.getSequence(file));
    }

    public MidiPlayer(Sequence sequence) throws MidiUnavailableException, InvalidMidiDataException {
        this(sequence, MidiSystem.getSequencer(false));
    }

    public MidiPlayer(Sequence sequence, Sequencer sequencer) throws InvalidMidiDataException {
        this.sequence = sequence;
        this.sequencer = sequencer;

        this.sequencer.setSequence(sequence);
        this.fallingNotes = new NoteMap(sequence);

        try {
            rgtAutoPlay = rgtAutoPlayEnabled ? new RgtAutoPlay() : null;
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void open() throws MidiUnavailableException {
        this.sequencer.open();
        // this.sequencer.addControllerEventListener(this::listenFlow,
        // new int[] { START, STOP, CONTINUE });
        if (rgtAutoPlay != null) {
            this.sequencer.getTransmitters().forEach(t -> t.close());
            this.sequencer.getTransmitter().setReceiver(new Receiver() {
                @Override
                public void send(MidiMessage msg, long timeStamp) {
                    if (msg instanceof ShortMessage) {
                        notePlayed((ShortMessage) msg);
                    }
                }

                @Override
                public void close() {
                }
            });
        }
        // if (rgtAutoPlay != null)
        // this.sequencer.addControllerEventListener(this::controlChange,
        // new int[] { ShortMessage.CONTROL_CHANGE, ShortMessage.NOTE_ON,
        // ShortMessage.NOTE_OFF,
        // ShortMessage.PROGRAM_CHANGE });
    }

    // private void listenFlow(ShortMessage msg) {

    // }

    private void notePlayed(ShortMessage msg) {
        if (piano == null)
            return;
        if (msg.getCommand() == ShortMessage.NOTE_ON) {
            rgtAutoPlay.playNote(msg.getData1());
        }
    }

    public void setPiano(Piano piano) {
        this.piano = piano;
        piano.player = this;
    }

    public Sequence getSequence() {
        return this.sequence;
    }

    public Sequencer getSequencer() {
        return this.sequencer;
    }

    public void close() {
        sequencer.close();
        piano.player = null;
    }
}
