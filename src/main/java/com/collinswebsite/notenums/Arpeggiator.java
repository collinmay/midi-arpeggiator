package com.collinswebsite.notenums;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by misson20000 on 3/18/17.
 */
public class Arpeggiator implements Receiver {
    private List<Integer> notes;

    public Arpeggiator() {
        notes = new LinkedList<>();
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
                noteOn(shortMessage.getData1());
                break;
            }
        }
    }

    private synchronized void noteOn(int note) {
        notes.add(note);
    }

    private synchronized void noteOff(int note) {
        notes.remove(Integer.valueOf(note));
    }

    public void begin() {
        double freq = 80.0;
        int currentNote = 0;
        int arpeggio = 0;
        while(true) {
            try {
                Thread.sleep((long) (1000.0/freq));
            } catch (InterruptedException e) {
            }

            synchronized(this) {
                if(notes.size() > 0) {
                    if(currentNote != notes.get(arpeggio % notes.size())) {
                        System.out.println(notes.get(arpeggio % notes.size()));
                        currentNote = notes.get(arpeggio % notes.size());
                    }
                } else {
                    currentNote = 0;
                    System.out.println(0);
                }
            }

            System.out.flush();
            arpeggio++;
        }
    }

    @Override
    public void close() {

    }
}
