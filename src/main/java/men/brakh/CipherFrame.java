package men.brakh;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.lang.Object.*;

public class CipherFrame extends JFrame implements ActionListener {
    JFileChooser fileChooser;
    private JFormattedTextField  qValue;
    private JFormattedTextField  pValue;
    private JFormattedTextField  KcValue;
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
        container.setLayout(new GridLayout(12, 6,5,5));
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
        rMod.setText("2419");
        eValue = new JFormattedTextField(formatter);
        eValue.setText("157");
        KcValue = new JFormattedTextField(formatter);
        KcValue.setText("133");

        pValue = new JFormattedTextField(formatter);
        pValue.setText("41");
        qValue = new JFormattedTextField(formatter);
        qValue.setText("59");


        // container.add(fileChooser);
        container.add(openFile);
        add(new JLabel("rmod"));
        container.add(rMod);
        add(new JLabel("pValue"));
        container.add(pValue);
        add(new JLabel("qValue"));
        container.add(qValue);

        container.add(encrypt);
        add(new JLabel("K oткр"));
        container.add(eValue);
        add(new JLabel("K cекретный"));
        container.add(KcValue);
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
            int secretKey = Integer.parseInt(KcValue.getText());


            r = p*q;
            isOkay = true;
            if (!Math.CheckSimple(E, x)){
                JOptionPane.showMessageDialog(null, "E and X are not relatively prime");
            }
            if ((E<1)||(E>x)){
                JOptionPane.showMessageDialog(null, "invalid e");
            }
            if (!Math.CheckSimple(p,q)||(!Math.CheckSimple(E, x))||(E<1)||(E>x)){
                JOptionPane.showMessageDialog(null, "Number are not relatively prime");

            }
            else{
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
                        fos.write(RSA.decrypt(secretKey,key,toShortArray(RSA.readfile(fileChooser.getSelectedFile()))));
                        //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                } else{
                    String path = getOutEncodePath(fileChooser.getSelectedFile().getPath());
                    try (FileOutputStream fos = new FileOutputStream(path)) {
                        fos.write(toByteArray(RSA.encrypt(E,r,RSA.readfile(fileChooser.getSelectedFile()))));
                        KcValue.setText(String.valueOf(c[2]));
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
        int j=0;
        for (int i = 0; i < lol.length; i+=2) {
            int r = lol[i] & 0xFF;
            r = (r << 8) | (lol[i+1] & 0xFF);
            kek[j]=(short) r;
            j++;
        }
        return kek;

    }
    private byte [] toByteArray(short [] kek)
    {
        byte lol[] = new byte [kek.length*2];
        int j=0;
        for (int i = 0; i <kek.length*2; i+=2) { 
            lol[i+1] = (byte)(kek[j] & 0xff);
            lol[i] = (byte)((kek[j] >> 8) & 0xff);
            j++;
        }
    return lol;
    }
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



    //    JOptionPane.showMessageDialog(null, Arrays.toString(Math.gcd(32,150)));


    }
}

