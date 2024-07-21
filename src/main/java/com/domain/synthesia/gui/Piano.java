package com.domain.synthesia.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import com.domain.synthesia.midi.MidiPlayer;

public class Piano extends Canvas {
    public MidiPlayer player;

    public final Key[] keys = new Key[127];
    public int[] status = new int[127];

    public Piano() {
        boolean[] k = { true, false, true, false, true, true, false, true, false, true, false, true };

        for (int i = 0; i < 127; i++) {
            keys[i] = k[i % 12] ? Key.WhiteKey : Key.BlackKey;
        }

        setSize(750, 50);
    }

    @Override
    public void paint(Graphics g) {
        reset();
        if (player != null)
            player.fallingNotes.notesAt(status, player.getSequencer().getTickPosition());
        int x = 0;
        for (int i = 0; i < 127; i++) {
            Key key = keys[i];
            if (key == Key.WhiteKey)
                x += key.render(g, x, status[i]);
        }
        x = 0;
        for (int i = 0; i < 127; i++) {
            Key key = keys[i];
            if (key == Key.WhiteKey)
                x += 10;
            else
                key.render(g, x, status[i]);
        }
        g.dispose();
    }

    protected enum Key {
        WhiteKey,
        BlackKey;

        public int render(Graphics g, int x, int status) {
            if (this == WhiteKey) {
                g.setColor(Color.BLACK);
                g.drawLine(x, 0, x + 10, 0);
                g.drawLine(x + 10, 0, x + 10, 50);
                g.drawLine(x + 10, 50, x, 50);
                g.drawLine(x, 50, x, 0);

                if (status > 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x + 1, 1, 8, 48);
                }

                return 10;
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(x - 2, 0, 5, 25);

                if (status > 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x - 1, 1, 3, 23);
                }

                return 0;
            }
        }
    }

    public void reset() {
        for (int i = 0; i < 127; i++) {
            status[i] = 0;
        }
    }
}
