import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by daniel on 12/20/16.
 *
 * @author Daniel Evans
 */
public class Train {

    private static int numberOfCompositions;

    public Train(String midiFilePath) {

        try {
            long oldPulse = 0;

            Sequence sequence = MidiSystem.getSequence(new File(midiFilePath));

            // determining the beats per minute of the audio sequence
            Path path = Paths.get(midiFilePath);
            byte[] data = Files.readAllBytes(path);
            int bpm = getBPM(data);

            // determines the number of pulses (milliseconds in the file)
            // sequence.getResolution() is in pulses per quarter note (PPQN)
            // found formula on stackoverflow
            int pulses = 60000/(bpm*sequence.getResolution());
            int id[] = {0, 0, 0};
            // stores the previous two edges to generate the next note
            Edge nArr[][] = new Edge[2][2];

            for(Track track : sequence.getTracks()) {
                for(int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if(message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;

                        if(sm.getCommand() == ShortMessage.NOTE_ON) {
                            int key = sm.getData1();
                            int velocity = sm.getData2();
                            // get the time of this midi event (event.getTick()*pulses)
                            // and subtract from previous event's time
                            int pause = (int) Math.abs(event.getTick()*pulses-oldPulse);

                            oldPulse = event.getTick()*oldPulse;

                            if(pause == 0)
                                pause = Defaults.PAUSE;

                            for(int j = 0; j < 2; j++) {
                                // when we have two edges (essentially notes)
                                // use them to make an update to the markov chain
                                if(id[j] == 2) {
                                    id[j] = 0;
                                    // the expression passed as first arg to updateWeight
                                    // is unique to the two notes in sequence
                                    // so this is what builds the transition probability matrix
                                    MarkovChain.updateWeight(nArr[j][0].getKeyNote()*127+nArr[j][1].getKeyNote(),
                                            new Edge(1.0, // add up the chances of this note sequence
                                                    (nArr[j][0].getVelocity()+nArr[j][1].getVelocity())/2,
                                                    (nArr[j][0].getDuration()+nArr[j][1].getDuration())/2,
                                                    (nArr[j][0].getOctave()+nArr[j][1].getOctave())/2,
                                                    key));
                                } else {
                                    nArr[j][id[j]++] = new Edge(1.0, velocity, pause, key,key/12-1);
                                }
                            }
                        }
                    }
                }
            }

            numberOfCompositions++;
        } catch(InvalidMidiDataException|IOException e) {
            e.printStackTrace();
        }

    }

    // https://cycling74.com/forums/topic/detect-midi-files-bpm/#.WFnAvvErJCU
    // find the bytes with sequence FF51 and return summation of next three bytes
    // in sequence or 120 (default for midi files)
    private int getBPM(byte[] data) {
        for (int i = 0; i + 1 < data.length; i++) {
            if(data[i] == 0xFF && data[i+1] == 0x51)
                return data[i+2] + data[i+3] + data[i+4];
        }
        // BPM defaults to 120 in
        return 120;
    }


    public static int getCount() {

        return numberOfCompositions;
    }
}
