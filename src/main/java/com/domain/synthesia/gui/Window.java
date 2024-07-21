package com.domain.synthesia.gui;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.Timer;

import com.domain.synthesia.Synthesia;
import com.domain.synthesia.midi.MidiPlayer;

public class Window extends Frame {
    private final Synthesia app;
    private final MenuBar menuBar;

    private final TimeControl timeControl;
    private final Piano piano;
    private final FallingNotes fallingNotes;

    private Timer t;

    public Window(Synthesia app) {
        super("Synthesia (free)");
        this.app = app;

        setLayout(new GridBagLayout());
        menuBar = new MenuBar();
        setMenuBar(menuBar);
        {
            Menu menu = new Menu("File");
            FileDialog f = new FileDialog(this);
            f.setMultipleMode(false);
            f.setMode(FileDialog.LOAD);
            f.setFilenameFilter((dir, name) -> name.endsWith(".mid") | name.endsWith(".midi"));
            // f.setFileFilter(new FileNameExtensionFilter("Midi Files", "mid", "midi"));
            MenuItem mi_o = new MenuItem("Open");
            menu.add(mi_o);
            mi_o.addActionListener(a -> {
                f.setVisible(true);
                // if (f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (f.getFile() == null)
                    return;
                try {
                    app.openFile(new File(f.getDirectory(), f.getFile()));
                } catch (IOException | MidiUnavailableException | InvalidMidiDataException e) {
                    app.reportError(e);
                }
            });
            MenuItem mi_u = new MenuItem("Unload");
            menu.add(mi_u);
            mi_u.addActionListener(a -> {
                app.unloadMidi();
            });
            MenuItem mi_e = new MenuItem("Exit");
            menu.add(mi_e);
            mi_e.addActionListener(a -> {
                app.close();
            });
            menuBar.add(menu);
        }
        {
            Menu menu = new Menu("Midi");
            {
                Menu sb_menu = new Menu("Soundbank");
                menu.add(sb_menu);

                MenuItem mi_o = new MenuItem("Open soundbank file");
                sb_menu.add(mi_o);
                MenuItem mi_d = new MenuItem("Use default soundbank");
                sb_menu.add(mi_d);

                FileDialog fd = new FileDialog(this);
                fd.setMultipleMode(false);
                fd.setMode(FileDialog.LOAD);
                fd.setFilenameFilter((dir, name) -> name.endsWith(".sf2"));
                mi_o.addActionListener(a -> {
                    fd.setVisible(true);
                    // if (f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    if (fd.getFile() == null)
                        return;
                    try {
                        File f = new File(fd.getDirectory(), fd.getFile());
                        if (app.setSoundbank(MidiSystem.getSoundbank(f))) {
                            System.out.printf("Loaded soundfont: %s\n", f.getAbsoluteFile());
                        } else {
                            Window.this.reportError("Cannot succesfully load soundbank");
                        }
                    } catch (IOException | InvalidMidiDataException err) {
                        app.reportError(err);
                    }
                });
                mi_d.addActionListener(a -> {
                    app.useDefaultSoundbank();
                });
            }
            menuBar.add(menu);
        }

        Insets inset = new Insets(0, 0, 0, 0);

        timeControl = new TimeControl(app);
        add(timeControl,
                new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        inset,
                        0, 0));

        piano = new Piano();
        add(piano,
                new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, inset,
                        0, 0));

        fallingNotes = new FallingNotes(piano);
        add(fallingNotes,
                new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        inset,
                        0, 0));

        pack();
        t = new Timer(1000 / 30, a -> update());
        t.setRepeats(true);
    }

    public void start() {
        setVisible(true);
        setAlwaysOnTop(MidiPlayer.rgtAutoPlayEnabled);
        t.start();
    }

    public Synthesia getApp() {
        return app;
    }

    public TimeControl getTimeControl() {
        return timeControl;
    }

    public Piano getPiano() {
        return piano;
    }

    public void update() {
        timeControl.update();
        {
            BufferStrategy bs = piano.getBufferStrategy();
            if (bs == null) {
                piano.createBufferStrategy(3);
                bs = piano.getBufferStrategy();
            }
            piano.update(bs.getDrawGraphics());
            bs.show();
        }
        {
            BufferStrategy bs = fallingNotes.getBufferStrategy();
            if (bs == null) {
                fallingNotes.createBufferStrategy(3);
                bs = fallingNotes.getBufferStrategy();
            }
            fallingNotes.update(bs.getDrawGraphics());
            bs.show();
        }
    }

    public void reportError(String msg) {
        Dialog d = new Dialog(this, "Error", true);
        d.setLayout(new BorderLayout());
        d.add(new Label(msg), BorderLayout.CENTER);
        Button btn_ok = new Button("OK");
        btn_ok.addActionListener(e -> {
            d.dispose();
        });
        d.add(btn_ok, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        d.dispose();
    }
}
