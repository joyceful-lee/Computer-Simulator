package computer.hardware.cpu;

import computer.hardware.panel.Panel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IOBuff {
    private String buff;
    private String in;

    public IOBuff(String in){
        this.in = in;
        this.buff = "";
    }

    public void setBuff(String input){
        this.buff = input;
    }

    public void flush(){
        buff = "";
    }

    public char getOneDigit() {
        char ret = buff.charAt(0);
        buff = buff.substring(1);
        return ret;
    }

    public int getLength() {
        return buff.length();
    }

    public boolean isEmpty() {
        return buff.length() == 0;
    }

    public void panelText(){
        String text = Panel.keyText.getText();
        setBuff(text);
    }

    public void setFile() {
        JFileChooser file = new JFileChooser();
        file.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                final String name = pathname.getName();
                return name.endsWith(".txt");
            }
            public String getDescription(){
                return "*.txt";
            }
        });
        List<String> data = new ArrayList<>();
        int select = file.showOpenDialog(null);
        if (select == file.APPROVE_OPTION) {
            // We get the file!
            File IOFile = file.getSelectedFile();
            System.out.println("Reading file " + IOFile.getName() + "...");
            // Read the file into the buffer.
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(IOFile));
                BufferedReader buffRead = new BufferedReader(reader);
                String line;
                while((line=buffRead.readLine())!= null) {
                   data.add(line);
                }
                setBuff(String.join(",", data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
