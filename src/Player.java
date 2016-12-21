import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.*;

public class Player extends Thread {
	private int n1, n2;
	private JLabel noteText, oct;
	private JTextArea prev;

	private final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	public Player(int n1, int n2, JLabel note, JTextArea prev, JLabel oct) {
		super("Thread Player");

		this.n1 = n1;
		this.n2 = n2;
		this.noteText = note;
		this.prev = prev;
		this.oct = oct;
	}
	public void run() {
		try {
			// syntesizer used to play the generated notes
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();

			final MidiChannel[] channels = synth.getChannels();

			// firstNote in sequence, secondNote in sequence
			// keyNote is generated note
			int firstNote, secondNote, keyNote;

			// set up first two notes from random generated ones in
			firstNote = n1;
			secondNote = n2;

			while (!this.isInterrupted()) {
				// Each edge represents a note
				// this is the next note to be played
				Edge note = MarkovChain.nextEdge(firstNote*127+secondNote);

				keyNote = note.getKeyNote();

				MarkovChain.updateWeight(note.getKeyNote(), note);
				int keyNoteOctave = keyNote/12 - 1;
				System.out.println(keyNote);
				String noteName = NOTE_NAMES[keyNote%12];

				noteText.setText(noteName);
				oct.setText("Octave: " + keyNoteOctave);
				prev.insert(noteName + keyNoteOctave + "\n", 0);

				// change value bigger to slow down music
				// change value smaller to speed up music
				double dannnysBias = 1.6;
				// turn on the note
				// and pause for note.getDuration time
				// my attempt at harmony/melodies is not turning any notes off
				channels[0].noteOn(keyNote, note.getVelocity());
				Thread.sleep((long)Math.ceil(note.getDuration()*dannnysBias));

				// update previous notes
				firstNote = secondNote;
				secondNote = keyNote;
			}
		} catch(Exception e) { 
			e.printStackTrace();
		}
	}
}
