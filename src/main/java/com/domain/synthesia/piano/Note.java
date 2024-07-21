package com.domain.synthesia.piano;

public class Note {
    private final int note;
    private final long startTime;
    private long duration = -1;
    private final int channel;

    public Note(int note, long startTime, int channel) {
        this.note = note;
        this.startTime = startTime;
        this.channel = channel;
    }

    public Note(int note, long startTime, long duration, int channel) {
        this(note, startTime, channel);
        this.duration = duration;
    }

    public int getNote() {
        return note;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getEndTime() {
        return startTime + duration;
    }

    public int getChannel() {
        return channel;
    }

    public boolean inside(long time) {
        return startTime <= time && time <= startTime + duration;
    }

    public static int compare(Note n1, Note n2) {
        long res = n1.getStartTime() - n2.getStartTime();
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        res = n2.getChannel() - n1.getChannel();
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        res = n2.note - n1.note;
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        return 0;
    }

    public static int compareByChannel(Note n2, Note n1) {
        long res = n1.channel - n2.channel;
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        res = n2.startTime - n1.startTime;
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        res = n2.note - n1.note;
        if (res > 0)
            return 1;
        else if (res < 0)
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Note))
            return false;
        Note o = (Note) obj;
        return o.channel == channel && o.note == note && o.startTime == startTime;
    }

    public boolean equalsByNote(Object obj) {
        if (!(obj instanceof Note))
            return false;
        Note o = (Note) obj;
        return o.channel == channel && o.note == note;
    }
}
