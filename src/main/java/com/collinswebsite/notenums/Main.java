package com.collinswebsite.notenums;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.midi.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by misson20000 on 3/18/17.
 */
public class Main extends Application {
    public static void main(String[] args) throws IOException, MidiUnavailableException, InvalidMidiDataException {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException, MidiUnavailableException, InvalidMidiDataException {
        FileChooser choose = new FileChooser();
        choose.getExtensionFilters().add(new FileChooser.ExtensionFilter("MIDI files (*.mid, *.midi)", "*.mid", "*.midi", "*.MID", "*.MIDI"));
        FileInputStream is = new FileInputStream(choose.showOpenDialog(stage));
        Sequencer sqr = MidiSystem.getSequencer();
        Sequence sq = MidiSystem.getSequence(is);
        sqr.open();
        sqr.setSequence(sq);

        //final SingleNXTArpeggiator arpeggiator = new SingleNXTArpeggiator(new Socket("localhost", 40560).getOutputStream());
        final Receiver arpeggiator = new MultiNXTArpeggiator(2);

        //new Thread(arpeggiator::begin, "arpeggiator thread").start();

        sqr.getTransmitters().forEach((t) -> t.setReceiver(arpeggiator));
        sqr.start();
    }
}
