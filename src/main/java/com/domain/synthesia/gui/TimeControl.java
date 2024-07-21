package com.domain.synthesia.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.sound.midi.Sequencer;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.domain.synthesia.Synthesia;
import com.domain.synthesia.midi.MidiPlayer;

public class TimeControl extends JPanel {
    private final Synthesia app;
    private final JSlider progressBar = new JSlider(0, 0, 0);
    private Sequencer sequencer;
    private Button btn_pp;

    public TimeControl(Synthesia app) {
        this.app = app;
        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.CENTER);
        btn_pp = new Button("Play");
        add(btn_pp, BorderLayout.SOUTH);
        btn_pp.addActionListener(a -> {
            if (sequencer.isRunning()) {
                sequencer.stop();
            } else {
                sequencer.start();
            }
            update();
        });
        {
            final boolean[] isUserChange = { false };
            progressBar.addChangeListener(e -> {
                if (!isUserChange[0])
                    return;
                if (sequencer == null)
                    return;
                sequencer.setTickPosition(progressBar.getValue() * sequencer.getTickLength() / 65535);
            });

            progressBar.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isUserChange[0] = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isUserChange[0] = false;
                }
            });
        }
        update();
    }

    public void update() {
        MidiPlayer player = app.getPlayer();
        if (player == null) {
            sequencer = null;
            btn_pp.setEnabled(false);
            progressBar.setValue(0);
            progressBar.setMaximum(0);
            if (app.getWindow() != null)
                app.getWindow().setAlwaysOnTop(false);
            return;
        }
        sequencer = player.getSequencer();
        btn_pp.setLabel(sequencer.isRunning() ? "Pause" : "Play");
        app.getWindow().setAlwaysOnTop(sequencer.isRunning() && MidiPlayer.rgtAutoPlayEnabled);
        btn_pp.setEnabled(true);
        progressBar.setMaximum(65535);
        progressBar.setValue((int) ((double) sequencer.getTickPosition() * 65535 / sequencer.getTickLength()));
    }

    public Synthesia getApp() {
        return app;
    }
}
