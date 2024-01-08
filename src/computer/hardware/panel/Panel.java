package computer.hardware.panel;

import computer.TimeToRock;
import computer.hardware.cpu.Registers;
import computer.hardware.memory.Memory;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * The panel that we are using to operate this machine.
 *
 * @version v0.3.0
 **/

public class Panel extends JFrame {
    private Registers regs = TimeToRock.regs;
    private Memory mem = TimeToRock.mem;
    public static Logger logger = TimeToRock.logger;
    private HashMap<String, JTextField> allRegisterValuesOnPanel;
    private HashMap<String, JButton> allButtons;
    private JPanel North;
    private JPanel West;
    private JPanel Center;
    private JPanel South;
    public JScrollPane console;
    private JTextArea consoleOutput;
    public static JTextArea keyText;
    public static JTextArea printText;
    public static JTextArea pipelineStatus = new JTextArea("");

    /**
     * Initialize the gui panel
     */
    public Panel(){
        allRegisterValuesOnPanel = new HashMap<>();
        allButtons = new HashMap<>();
        JFrame jFrame = new JFrame("CSCI 6461 Team 3 P4A");
        jFrame.setLayout(new BorderLayout());

        //Add those buttons to the top
        this.North = new JPanel();
        this.addNorth();
        jFrame.add(this.North, BorderLayout.NORTH);

        //Add those registers to the left
        this.West = new JPanel();
        this.addWest(regs);
        jFrame.add(this.West, BorderLayout.WEST);

        Center = new JPanel();
//        Center.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.addCenter();
        jFrame.add(this.Center, BorderLayout.CENTER);

        PanelActions panelActions = new PanelActions();
        panelActions.addButtonActions(
                this.allButtons,
                this.console,
                this.consoleOutput,
                this.allRegisterValuesOnPanel
        );
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

//        pipelineStatus = new JTextArea("");

        TimeToRock.logger.info("Panel initialized.");
    }

    /**
     * Add those buttons to the top
     */
    private void addNorth(){
        JButton writeRegsButton = new JButton("Write Registers");
        this.North.add(writeRegsButton);
        this.allButtons.put("writeRegs", writeRegsButton);
        JButton iplButton = new JButton("IPL");
        this.North.add(iplButton);
        this.allButtons.put("IPL", iplButton);
        // load memory from file
        JButton loadButton = new JButton("Load Memory");
        this.North.add(loadButton);
        this.allButtons.put("loadMem", loadButton);
        JButton ssButton = new JButton("Single Cycle");
        this.North.add(ssButton);
        this.allButtons.put("SS", ssButton);
        JButton runButton = new JButton("Run");
        this.North.add(runButton);
        this.allButtons.put("Run", runButton);
        JButton clrButton = new JButton("Clear Output");
        this.North.add(clrButton);
        this.allButtons.put("Clear", clrButton);
        clrButton.setVisible(false);
    }

    /**
     * Add those registers to the left
     *
     * @param regs the registers that we want to add
     */
    private void addWest(Registers regs){
        this.West.setLayout(new GridBagLayout());
        this.West.setToolTipText("After editing the value of registers on the panel, " +
                "press Write Registers button to write them.");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        JTextField value;
        String [] regNames = {"PC", "PRE-IR", "IR", "MAR", "MBR", "X1", "X2", "X3", "GPR0", "GPR1", "GPR2", "GPR3",
                "MFR", "CC", "FR0", "FR1"};
        int j = 0;
        for (String name: regNames){
            gbc.gridy = j;
            gbc.gridx = 0;
            this.West.add(new JLabel("   " + name), gbc);
            gbc.gridx = 1;
            this.West.add(new JLabel("->"), gbc);
            gbc.gridx = 2;
            value = new JTextField();
            value.setText(regs.get(name).getValueString());
            this.West.add(value, gbc);
            this.allRegisterValuesOnPanel.put(name, value);
            j++;
        }
        allRegisterValuesOnPanel.get("CC").setEditable(false);
        allRegisterValuesOnPanel.get("MFR").setEditable(false);

//        this.West.add(new JLabel("Pipeline Status:"));
//        pipelineStatus.setEditable(false);
//        this.West.add(pipelineStatus);
    }

    /**
     * Add the console output to the center and the console input to the bottom
     */
    private void addCenter(){
        this.consoleOutput = new JTextArea("",19, 40);
        this.consoleOutput.setEditable(false);
        this.consoleOutput.setLineWrap(true);
        this.consoleOutput.setWrapStyleWord(true);

        //Redirect system output to JTextField
        TextAreaOutputStream taos = new TextAreaOutputStream(this.consoleOutput, 60 );
        PrintStream printStream = new PrintStream(taos);
        System.setOut(printStream);
        System.setErr(printStream);
        //If I want to redirect logging in the future
        //https://java-swing-tips.blogspot.com/2015/02/logging-into-jtextarea.html

        //Add some control buttons
        JPanel control = new JPanel();

        JButton program1Button = new JButton("Run Program");
        control.add(program1Button);
        this.allButtons.put("Program", program1Button);
        control.add(new JLabel("Debug functions:"));
        JButton showMemButton = new JButton("Memory Operation");
        control.add(showMemButton);
        this.allButtons.put("showMem", showMemButton);
        JButton scrollToBottom = new JButton("Scroll ⬇");
        control.add(scrollToBottom);
        this.allButtons.put("toBottom", scrollToBottom);
        JButton testButton = new JButton("test");
        control.add(testButton);
        this.allButtons.put("test", testButton);
        testButton.setVisible(false);

        JPanel keyPanel = new JPanel(new GridLayout(1,1));
        keyText = new JTextArea(19,40);
        JScrollPane scrollP = new JScrollPane(keyText);
        keyText.setBackground(Color.WHITE);
        keyText.setLineWrap(true);
        keyText.setWrapStyleWord(true);
        keyPanel.add(scrollP);

        JPanel printPanel = new JPanel(new GridLayout(1,1));
        printText = new JTextArea(19,40);
        JScrollPane scrollPane = new JScrollPane(printText);
        printText.setEditable(false);
        printText.setBackground(Color.WHITE);
        printText.setLineWrap(true);
        printText.setWrapStyleWord(true);
        printPanel.add(scrollPane, BorderLayout.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel combined = new JPanel(new BorderLayout());
        console = new JScrollPane(this.consoleOutput);
        tabbedPane.addTab("Console", console);
        tabbedPane.addTab("Keyboard", keyPanel);
        tabbedPane.addTab("Printer", printPanel);
        combined.add(tabbedPane, BorderLayout.NORTH);
        combined.add(control, BorderLayout.SOUTH);
        this.Center.add(combined);
    }

    /**
     * Update all registers' value displayed on the panel
     */
    public void updateRegsValueOnPanel(){
        for(String name: this.allRegisterValuesOnPanel.keySet()){
            String value = regs.get(name).getValueString();
            String oldValue = this.allRegisterValuesOnPanel.get(name).getText();
            if (!value.equals(oldValue)){
                this.allRegisterValuesOnPanel.get(name).setText(value);
                System.out.println("Update displayed register " + name + " value from " + oldValue + " to " + value + ".");
            }
        }
    }
}
