import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Interface extends JFrame {

    private JPanel pan, val, ctrl, main, nota, info;
    private JButton play, stop, pause, rand;
    private JTextField n1, n2;
    private JLabel note, st, oct, imp;
    private JTextArea prev;
    private JScrollPane scr;

    private Thread player;

    private boolean playing = false;

    @SuppressWarnings("deprecation")
    public Interface() {
        setTitle("The 2nd Order Composer");
        setSize(new Dimension(550, 300));

        // get a random number that it is range from 1 to 127
        int random;
        do {
           random  = ThreadLocalRandom.current().nextInt() % 127;
        } while(random < 0);

        pan = new JPanel();


        // Everthing up until close to the bottom is boring
        // GUI stuff. The bottom is where we call the train method on
        // the midi files given by user
        pan.setLayout(new GridLayout(1, 2));

        val = new JPanel();
        ctrl = new JPanel();

        n1 = new JTextField();
        n2 = new JTextField();
        st = new JLabel("Starting notes: ");
        n1.setPreferredSize(new Dimension(40, 20));
        n2.setPreferredSize(new Dimension(40, 20));

        n1.setText(random + "");
        n2.setText(random + "");

        int temp = random;

        rand = new JButton("Randomize first two notes (2nd order markov chain)");
        rand.addActionListener(e -> {
            n1.setText(temp + "");
            n2.setText(temp + "");
        });

        val.add(st);
        val.add(n1);
        val.add(n2);
        val.add(rand);

        play = new JButton("Play the generated music");
        play.addActionListener(e -> {
            // get the seed notes from the text box
            player = new Player(Integer.parseInt(n1.getText()), Integer.parseInt(n2.getText()), note, prev, oct);

            player.start();

            playing = true;

            pause.setText("Pause");
            pause.setEnabled(true);

            stop.setEnabled(true);

            rand.setEnabled(false);
            n1.setEditable(false);
            n2.setEditable(false);
        });
        stop = new JButton("Stop");
        stop.addActionListener(e -> {
            if (!playing)
                player.resume();

            // interrupt the thread running the sound
            // stops the run method 
            player.interrupt();
            try {
                // waits for the thread to die and join with the main application thread 
                player.join();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            playing = false;

            pause.setText("Pause");
            pause.setEnabled(false);

            stop.setEnabled(false);

            rand.setEnabled(true);
            n1.setEditable(true);
            n2.setEditable(true);
        });
        pause = new JButton("Pause");
        pause.addActionListener(e -> {
            if (playing) {
                player.suspend();
                pause.setText("Resume");
                playing = false;
            } else {
                player.resume();
                pause.setText("Pause");
                playing = true;
            }
        });

        pause.setEnabled(false);
        stop.setEnabled(false);
        play.setEnabled(false);

        ctrl.add(play);
        ctrl.add(pause);
        ctrl.add(stop);

        pan.add(val);
        pan.add(ctrl);

        note = new JLabel();

        prev = new JTextArea();
        prev.setEditable(false);

        main = new JPanel();

        main.setLayout(new GridLayout(1, 2, 10, 10));

        scr = new JScrollPane(prev);

        main.add(scr);

        nota = new JPanel();
        nota.setLayout(new BorderLayout());

        info = new JPanel();
        info.setLayout(new GridLayout(1, 2));

        oct = new JLabel("Octave: ");
        imp = new JLabel("Learning...");

        info.add(oct);
        info.add(imp);

        nota.add(note);
        nota.add(info, BorderLayout.SOUTH);

        main.add(nota);

        add(main);
        add(pan, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

        JFileChooser dir = new JFileChooser();
        dir.setCurrentDirectory(new File(System.getProperty("user.home") + "/midi"));
        dir.setDialogTitle("Choose a directory containing the midi files to teach the 2nd Order Composer with");
        dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        dir.setAcceptAllFileFilterUsed(false);

        File learningDir = null;

        if (dir.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            learningDir = dir.getSelectedFile();
        else {
            JOptionPane.showMessageDialog(this, "You need to choose a learning directory!", "Learning error!", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        int numberOfFiles = 0;
        if (learningDir == null) {
            JOptionPane.showMessageDialog(this, "No files given! So I can't learn anything :(", "Error!", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        }
        else {
            File[] files = learningDir.listFiles();
            try {
                for (File file : files) {
                    // count number of compositions given 
                    // to check if the number of is 0 (means we can't learn anything)
                    numberOfFiles = 0;
                    if (!file.isDirectory()) {
                        try {
                            // check that the file is indeed a midi file
                            if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("mid")) {
                                new Train(file.getCanonicalPath());
                                numberOfFiles++;
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (NullPointerException e) { // in case files is null 
                e.printStackTrace();
            }
        }

        if (numberOfFiles == 0) {
            JOptionPane.showMessageDialog(this, "No midi files found in the learning directory!", "Error!", JOptionPane.ERROR_MESSAGE);
            dispose();
            System.exit(3);
        }

        // finish making the probability matrix
        MarkovChain.setAsProbabilityMatrix();

        imp.setText("Learned compositions: " + Train.getCount());
        play.setEnabled(true);
    }

}
