package men.brakh;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.lang.Object.*;

public class CipherFrame extends JFrame implements ActionListener {
    JFileChooser fileChooser;
    private JFormattedTextField  qValue;
    private JFormattedTextField  pValue;
    ByteArrayOutputStream baos = null;
    DataOutputStream dos = null;
    private JFormattedTextField  eValue;
    private  JFormattedTextField  rMod;
    private byte[] txt;
    File file;
    private int E, key,p,q,r,x;
    private boolean isOkay;
    CipherFrame() {


        super("Perfect LFSR encoder/decoder");
        isOkay =  false;
        this.setBounds(100, 100, 640, 320);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(10, 4,5,5));
        fileChooser = new JFileChooser(System.getProperty("user.home"));
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(workingDirectory);

        File f = new File(workingDirectory.getAbsolutePath());

        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);

        JButton openFile = new JButton("Open File");
        openFile.setActionCommand("openFile");
        openFile.addActionListener(this);

        JButton encrypt = new JButton("Encrypt");
        encrypt.setActionCommand("Encrypt");
        encrypt.addActionListener(this);

        rMod = new JFormattedTextField(formatter);
        rMod.setText("Rmod");

        eValue = new JFormattedTextField(formatter);
        eValue.setText("7");

        pValue = new JFormattedTextField(formatter);
        pValue.setText("3");
        qValue = new JFormattedTextField(formatter);
        qValue.setText("11");

        // container.add(fileChooser);
        container.add(openFile);
        add(new JLabel("rmod"));
        container.add(rMod);
        add(new JLabel("pValue"));
        container.add(pValue);
        add(new JLabel("qValue"));
        container.add(qValue);

        container.add(encrypt);
        add(new JLabel("eValue"));
        container.add(eValue);

        this.setVisible(true);

    }
public int ingetQ(){
        return q;
}
    public int ingetP(){
        return p;
    }

    public void actionPerformed(ActionEvent e) {
        if ("openFile".equals(e.getActionCommand())) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                try {
                    txt = RSA.readfile(file);
                    System.out.println(RSA.getDecimaltext(txt,5));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
        }
        if ("Encrypt".equals(e.getActionCommand())) {
            p = Integer.parseInt(qValue.getText());
            q = Integer.parseInt(pValue.getText());
            E = Integer.parseInt(eValue.getText());
            key = Integer.parseInt(rMod.getText());
            x = Math.EulerFunc(p,q);
            r = p*q;
            isOkay = true;
            if (!Math.CheckSimple(p,q)||(!Math.CheckSimple(E, x))||(E<1)||(E>x)){
                JOptionPane.showMessageDialog(null, "Number are not relatively prime");

            } else{
              //  JOptionPane.showMessageDialog(null, "Number are relatively prime");
                int[] c =Math.gcd(x,E);
                int d = c[2];
                //JOptionPane.showMessageDialog(null, Integer.toString(d));
                baos = new ByteArrayOutputStream();
                dos = new DataOutputStream(baos);

                if (getFileExtension(file).equals(".coded")){
                    d = Integer.parseInt(qValue.getText());
                    String path = getOutDecodePath(fileChooser.getSelectedFile().getPath());
                    try (FileOutputStream fos = new FileOutputStream(path)) {
                        fos.write(RSA.decrypt(d,key,toShortArray(RSA.readfile(fileChooser.getSelectedFile()))));
                        //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                } else{
                    String path = getOutEncodePath(fileChooser.getSelectedFile().getPath());
                    try (FileOutputStream fos = new FileOutputStream(path)) {
                        fos.write( toByteArray(RSA.encrypt(E,r,RSA.readfile(fileChooser.getSelectedFile()))));
                        //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }


            }
        }
    }
    private short[] toShortArray(byte[] lol){
        short kek[] = new short[lol.length];
        for (int i = 0; i < lol.length; i++) {
            kek[i] = (short)lol[i];

        }
        return kek;
    }
    private byte[] toByteArray(short[] lol){
        byte kek[] = new byte[lol.length*2];
        for (int i=0;i<lol.length;i++){
            kek[i] = (byte)(lol[i] & 0xff);
            kek[i+1] = (byte)((lol[i] >> 8) & 0xff);
        }
        return kek;    }
    private String getOutEncodePath(String filePath) {
        return filePath + ".coded";
    }
    private String getOutDecodePath(String filePath) {
        return filePath + ".decoded";
    }
    private static String getFileExtension(File file) {
        String extension = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }

        return extension;

    }


    public static void main(String[] args) {
        CipherFrame cf = new CipherFrame();



        JOptionPane.showMessageDialog(null, Arrays.toString(Math.gcd(32,150)));


    }
}

