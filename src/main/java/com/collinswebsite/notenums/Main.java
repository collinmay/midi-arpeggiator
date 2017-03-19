package com.collinswebsite.notenums;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by misson20000 on 3/18/17.
 */
public class Main {
    public static void main(String[] args) throws IOException, MidiUnavailableException, InvalidMidiDataException {
        FileInputStream is = new FileInputStream(args[0]);
        Sequencer sqr = MidiSystem.getSequencer();
        Sequence sq = MidiSystem.getSequence(is);
        sqr.open();
        sqr.setSequence(sq);
        sqr.setTempoFactor(0.4);

        final Arpeggiator arpeggiator = new Arpeggiator();

        new Thread(arpeggiator::begin, "arpeggiator thread").start();

        sqr.getTransmitters().forEach((t) -> t.setReceiver(arpeggiator));
        sqr.start();
    }
}
