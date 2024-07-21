package com.domain.synthesia.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import com.domain.synthesia.piano.Note;

public class FallingNotes extends Canvas {
    public final Piano piano;
    public int scale = 10;

    public FallingNotes(Piano piano) {
        this.piano = piano;

        setBackground(Color.BLACK);

        setSize(750, 300);
    }

    @Override
    public void paint(Graphics g) {
        if (piano.player == null)
            return;

        Color[] colors = new Color[] { Color.GREEN, Color.BLUE, Color.ORANGE, Color.RED, Color.YELLOW, Color.PINK,
                Color.CYAN };

        long time = piano.player.getSequencer().getTickPosition();

        boolean[] nw = { true, false, true, false, true, true, false, true, false, true, false, true };
        int[] nx = { 0, 8, 10, 18, 20, 30, 38, 40, 48, 50, 58, 60 };

        for (Note note : piano.player.fallingNotes
                .noteBefore(piano.player.getSequencer().getTickPosition() + getWidth() * scale)) {
            if (note.getEndTime() < time)
                continue;

            int n = note.getNote();
            int x = (n / 12) * 7 * 10 + nx[n % 12];

            int c = note.getChannel();
            g.setColor(colors[((c & 0xf) + (c >>> 4)) % colors.length]);

            int h = (int) note.getDuration() / scale;

            g.fillRect(x, getHeight() - (int) (note.getStartTime() - time) / scale - h, nw[n % 12] ? 10 : 5,
                    h);
        }

        g.dispose();
    }
}
