package com.collinswebsite.notenums;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by misson20000 on 3/18/17.
 */
public class SingleNXTArpeggiator implements Receiver {
    private final PrintStream stream;
    private List<Integer> notes;
    private int arpeggio;

    public SingleNXTArpeggiator(OutputStream stream) {
        notes = new LinkedList<>();
        this.stream = new PrintStream(stream);
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if(message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            switch(shortMessage.getCommand()) {
            case 0x80:
                noteOff(shortMessage.getData1());
                break;
            case 0x90:
                if(shortMessage.getData2() > 80) {
                    noteOn(shortMessage.getData1());
                }
                break;
            }
        }
    }

    private synchronized void noteOn(int note) {
        notes.add(0, note);
        //arpeggio = 0;
    }

    private synchronized void noteOff(int note) {
        notes.remove(Integer.valueOf(note));
    }

    public void begin() {
        double freq = 80.0;
        int currentNote = 0;
        arpeggio = 0;
        while(true) {
            try {
                Thread.sleep((long) (1000.0/freq));
            } catch (InterruptedException e) {
            }

            synchronized(this) {
                if(notes.size() > 0) {
                    if(currentNote != notes.get(arpeggio % notes.size())) {
                        stream.println(notes.get(arpeggio % notes.size()));
                        currentNote = notes.get(arpeggio % notes.size());
                    }
                } else {
                    currentNote = 0;
                    stream.println(0);
                }
            }

            stream.flush();
            arpeggio++;
            if(notes.size() > 0) {
                arpeggio %= notes.size();
            }
        }
    }

    @Override
    public void close() {

    }
}
