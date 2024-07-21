package com.domain.synthesia.midi;

import java.awt.Robot;

import static java.awt.event.KeyEvent.*;

import java.awt.AWTException;

public class RgtAutoPlay {
    public static final int[] QWERTY_KEY_NOTES = new int[] {
            VK_1,
            0,
            VK_2,
            0,
            VK_3,
            VK_4,
            0,
            VK_5,
            0,
            VK_6,
            0,
            VK_7,
            VK_8,
            0,
            VK_9,
            0,
            VK_0,
            VK_Q,
            0,
            VK_W,
            0,
            VK_E,
            0,
            VK_R,
            VK_T,
            0,
            VK_Y,
            0,
            VK_U,
            VK_I,
            0,
            VK_O,
            0,
            VK_P,
            0,
            VK_A,
            VK_S,
            0,
            VK_D,
            0,
            VK_F,
            VK_G,
            0,
            VK_H,
            0,
            VK_J,
            0,
            VK_K,
            VK_L,
            0,
            VK_Z,
            0,
            VK_X,
            VK_C,
            0,
            VK_V,
            0,
            VK_B,
            0,
            VK_N,
            VK_M
    };
    public Robot r;

    public RgtAutoPlay() throws AWTException {
        r = new Robot();
    }

    public void playNote(int note) {
        note += 23 - 59;
        while (note < 0) {
            note += 12;
        }
        while (note >= QWERTY_KEY_NOTES.length) {
            note -= 12;
        }
        int key = QWERTY_KEY_NOTES[note];
        boolean shift = key == 0;
        if (shift) {
            key = QWERTY_KEY_NOTES[note - 1];
        }
        press(key, shift);
    }

    public void press(int key, boolean shift) {
        if (shift)
            r.keyPress(VK_SHIFT);
        r.keyPress(key);
        r.delay(1);
        if (shift)
            r.keyRelease(VK_SHIFT);
        r.keyRelease(key);
    }
}
