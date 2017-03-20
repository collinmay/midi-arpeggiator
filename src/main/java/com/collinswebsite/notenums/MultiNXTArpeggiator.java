package com.collinswebsite.notenums;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by misson20000 on 3/18/17.
 */
public class MultiNXTArpeggiator implements Receiver {
    private NXTState[] nxtStates;

    public MultiNXTArpeggiator(int numNXTs) throws IOException {
        nxtStates = new NXTState[numNXTs];
        for(int i = 0; i < numNXTs; i++) {
            nxtStates[i] = new NXTState(new PrintStream(new Socket("localhost", 40560 + i).getOutputStream()));
        }
    }

    private class NXTState {
        private int currentNote;
        private int currentVelocity;
        private long startTime;
        private PrintStream stream;

        public NXTState(PrintStream stream) {
            this.stream = stream;
        }

        public void setNote(int note, int velocity) {
            if(currentNote != note) {
                stream.println(note);
                stream.flush();
            }
            this.currentNote = note;
            this.currentVelocity = velocity;
            this.startTime = System.currentTimeMillis();
        }
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
                try {
                    noteOn(shortMessage.getData1(), shortMessage.getData2());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private synchronized void noteOn(int note, int velocity) throws Exception {
        for(NXTState nxt : nxtStates) {
            if(nxt.currentNote == 0) {
                nxt.setNote(note, velocity);
                return;
            }
        }

        Arrays.stream(nxtStates).min(Comparator.comparingLong(a -> a.currentVelocity* 20 + a.startTime)).orElseThrow(() -> new Exception("no nxt states")).setNote(note, velocity);
    }

    private synchronized void noteOff(int note) {
        for (NXTState nxt : nxtStates) {
            if (nxt.currentNote == note) {
                nxt.setNote(0, 0);
                break;
            }
        }
    }


    /*public void begin() {
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
    }*/

    @Override
    public void close() {

    }
}
