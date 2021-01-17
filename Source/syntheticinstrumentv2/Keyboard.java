package syntheticinstrumentv2;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.*;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;

public class Keyboard{
    private JFrame frame = new JFrame();
    private JButton next = new JButton("Next");
    private JButton prev = new JButton("Prev");
    private JPanel panel = new JPanel();
    private JLabel instr = new JLabel("Loading...");
    private GridBagConstraints gbc = new GridBagConstraints();
    private Canvas canvas = new Canvas();
    private Synthesizer synthesizer;
    private MidiChannel[] midiChannels;
    private Instrument[] instruments;
    private int instrumentIndex = 0;
    private int dynamics = 1;
    private HashMap<Integer, Integer> notes = new HashMap();
    
    public Keyboard(){
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas);
        frame.add(panel);
        frame.setPreferredSize(new Dimension(250, 100));
        canvas.addKeyListener(new KeyboardListener());
        canvas.setFocusable(true);
        canvas.requestFocus();
        
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        panel.setLayout(new GridBagLayout());
        panel.add(instr, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(prev, gbc);
        
        gbc.gridx = 1;
        panel.add(next, gbc);
        
        prev.addActionListener(new ButtonListener());
        next.addActionListener(new ButtonListener());
        
        frame.pack();
        frame.setVisible(true);
        
        try{
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
        }
        catch(MidiUnavailableException e){
            System.exit(1);
        }
        
        midiChannels = synthesizer.getChannels();
        
        Soundbank bank = synthesizer.getDefaultSoundbank();
        
        synthesizer.loadAllInstruments(bank);
        
        instruments = synthesizer.getAvailableInstruments();
        synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());
        synthesizer.getChannels()[0].programChange(instrumentIndex);
        
        instr.setText(instruments[instrumentIndex].getName());
        
        createHashMap();
    }
    public void createHashMap(){
        notes.put(KeyEvent.VK_Q, 72);
        notes.put(KeyEvent.VK_2, 73);
        notes.put(KeyEvent.VK_W, 74);
        notes.put(KeyEvent.VK_3, 75);
        notes.put(KeyEvent.VK_E, 76);
        notes.put(KeyEvent.VK_R, 77);
        notes.put(KeyEvent.VK_5, 78);
        notes.put(KeyEvent.VK_T, 79);
        notes.put(KeyEvent.VK_6, 80);
        notes.put(KeyEvent.VK_Y, 81);
        notes.put(KeyEvent.VK_7, 82);
        notes.put(KeyEvent.VK_U, 83);
        notes.put(KeyEvent.VK_I, 84);
        
        notes.put(KeyEvent.VK_Z, 60);
        notes.put(KeyEvent.VK_S, 61);
        notes.put(KeyEvent.VK_X, 62);
        notes.put(KeyEvent.VK_D, 63);
        notes.put(KeyEvent.VK_C, 64);
        notes.put(KeyEvent.VK_V, 65);
        notes.put(KeyEvent.VK_G, 66);
        notes.put(KeyEvent.VK_B, 67);
        notes.put(KeyEvent.VK_H, 68);
        notes.put(KeyEvent.VK_N, 69);
        notes.put(KeyEvent.VK_J, 70);
        notes.put(KeyEvent.VK_M, 71);
        notes.put(KeyEvent.VK_COMMA, 72);
    }
    private final class ButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            Object buttonPressed = event.getSource();
            if(buttonPressed == prev){
                if (instrumentIndex == 0) {
                    instrumentIndex = instruments.length - 1;
                } 
                else {
                    instrumentIndex--;
                }
            
            }
            else if(buttonPressed == next){
                if (instrumentIndex == instruments.length - 1) {
                    instrumentIndex = 0;
                } 
                else {
                    instrumentIndex++;
                }
            }
            instr.setText(instruments[instrumentIndex].getName());
            synthesizer.getChannels()[0].programChange(instrumentIndex);
            canvas.requestFocus();
        }
    }
    
    private final class KeyboardListener implements KeyListener{
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getExtendedKeyCode();
            if(notes.containsKey(keyCode)){
                midiChannels[0].noteOn(notes.get((Integer)keyCode), 600);
            }
            if(keyCode == KeyEvent.VK_UP){
                if(dynamics == 0){
                    synthesizer.getChannels()[0].controlChange(7, (int)(0.7 * 127));
                    dynamics = 1;
                    instr.setText(instruments[instrumentIndex].getName());
                }
                else{
                    synthesizer.getChannels()[0].controlChange(7, (int)(1 * 127));
                    dynamics = 2;
                    instr.setText(instruments[instrumentIndex].getName() + " (forte)");
                }
            }
            if(keyCode == KeyEvent.VK_DOWN){
                if(dynamics == 1){
                    synthesizer.getChannels()[0].controlChange(7, (int)(0.5 * 127));
                    dynamics = 0;
                    instr.setText(instruments[instrumentIndex].getName() + " (piano)");
                }
                else{
                    synthesizer.getChannels()[0].controlChange(7, (int)(0.7 * 127));
                    dynamics = 1;
                    instr.setText(instruments[instrumentIndex].getName());
                }
            }
        }
        public void keyReleased(KeyEvent event){
            int keyCode = event.getExtendedKeyCode();
            if(notes.containsKey(keyCode)){
                midiChannels[0].noteOff(notes.get((Integer)keyCode), 600);
            }
        }
        public void keyTyped(KeyEvent event){
        
        }
    }
    
}
