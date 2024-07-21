package com.domain.synthesia.piano;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class NoteMap {
    private Sequence sequence;
    private ConcurrentSkipListSet<Note> notes = new ConcurrentSkipListSet<>(Note::compare);
    private long duration;

    private int notes_range_count;

    public NoteMap() {
    }

    public NoteMap(Sequence seq) {
        this.sequence = seq;
        duration = seq.getTickLength();
        ConcurrentSkipListSet<NoteEvent> note_events = new ConcurrentSkipListSet<>(NoteMap::NoteEvent_compare);
        int t = 0;
        for (Track track : seq.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage msg = event.getMessage();
                if (!(msg instanceof ShortMessage))
                    continue;
                ShortMessage smsg = (ShortMessage) msg;
                switch (smsg.getCommand()) {
                    case ShortMessage.NOTE_ON:
                    case ShortMessage.NOTE_OFF:
                        note_events
                                .add(new NoteEvent(t, event.getTick(), smsg,
                                        smsg.getCommand() == ShortMessage.NOTE_ON));
                }
            }
            t++;
        }
        // System.out.println(note_events);
        Note[] note_indexes = new Note[127];
        ConcurrentSkipListSet<Note> notes = new ConcurrentSkipListSet<>(Note::compareByChannel);
        for (NoteEvent ne : note_events) {
            Note recent_note = note_indexes[ne.note];
            if (recent_note != null && recent_note.getChannel() != ne.channel)
                recent_note = null;
            if (ne.on) {
                notes.add(note_indexes[ne.note] = ne.toNote());
                // if (recent_note != null && recent_note.getEndTime() > ne.time) {
                // // Note next = notes.higher(recent_note);
                // // while (next.getStartTime() == recent_note.getStartTime()
                // // || next.getChannel() != recent_note.getChannel())
                // // next = notes.higher(next);
                // recent_note.setDuration(ne.time - recent_note.getStartTime());
                // }
            }
            if (recent_note != null && recent_note.getEndTime() > ne.time) {
                recent_note.setDuration(ne.time - recent_note.getStartTime());
            }
        }
        notes_range_count = 0;
        for (int i = 0; i < 127; i++) {
            Note recent_note = note_indexes[i];
            if (recent_note != null) {
                if (recent_note.getEndTime() > duration)
                    recent_note.setDuration(duration - recent_note.getStartTime());
                notes_range_count++;
            }
        }
        this.notes.addAll(notes);
    }

    public long getDuration() {
        return duration;
    }

    public void notesAt(int[] notes, long time) {
        Note currentNote = this.notes.floor(new Note(-1, time, -1));
        boolean[] ns = new boolean[127];
        int count = 0;
        while (currentNote != null && count < notes_range_count) {
            if (currentNote.inside(time))
                notes[currentNote.getNote()] = 1;
            if (!ns[currentNote.getNote()]) {
                count++;
                ns[currentNote.getNote()] = true;
            }

            currentNote = this.notes.lower(currentNote);
        }
    }

    // public Iterator<Note> notesIn(long start, long end) {
    // NavigableSet<Note> ns = notes.headSet(new Note(-1, end, -1));
    // return new Iterator<Note>() {
    // Note currentNote = ns.last();

    // @Override
    // public boolean hasNext() {
    // if (currentNote == null)
    // return false;
    // return currentNote.getEndTime() > start;
    // }

    // @Override
    // public Note next() {
    // if (!hasNext())
    // throw new NoSuchElementException();
    // Note res = currentNote;
    // notes.remove(currentNote);
    // currentNote = notes.last();
    // return res;
    // }
    // };
    // }

    public NavigableSet<Note> noteBefore(long end) {
        return notes.headSet(new Note(-1, end, -1));
    }

    private class NoteEvent {
        int track;
        long time;
        int channel;
        int note;
        int velocity;
        boolean on;

        public NoteEvent(int track, long start, int channel, int note, int velocity, boolean on) {
            this.track = track;
            this.time = start;
            this.channel = channel + 16 * track;
            this.note = note;
            this.velocity = velocity * NoteMap.this.sequence.getResolution() / 100;
            this.on = on;
        }

        public NoteEvent(int track, long start, ShortMessage msg, boolean on) {
            this(track, start, msg.getChannel(), msg.getData1(), msg.getData2(), on);
        }

        public Note toNote() {
            return new Note(note, time, velocity, channel);
        }

        public Note toNote(long endTime) {
            return new Note(note, time, endTime - time, channel);
        }

        @Override
        public String toString() {
            return String.format("%d %d %d %s", time, channel, note, on ? "on" : "off");
        }
    }

    public void printImage() throws IOException {
        BufferedImage image = new BufferedImage(127, (int) duration, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = image.getGraphics();
        g.setColor(Color.GRAY);
        g.clearRect(0, 0, 127, image.getHeight());
        Color[] colors = new Color[] { Color.GREEN, Color.BLUE, Color.ORANGE };
        for (Note n : notes) {
            int c = n.getChannel();
            g.setColor(colors[(c & 0xf) + (c >>> 4)]);
            g.fillRect(n.getNote(), (int) n.getStartTime(), 1, (int) n.getDuration());
        }
        g.dispose();
        ImageIO.write(image, "png", new File("image.png"));
    }

    public static int NoteEvent_compare(NoteEvent n2, NoteEvent n1) {
        long res = n1.channel - n2.channel;
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        res = n2.time - n1.time;
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        return n2.note - n1.note;
    }
}
