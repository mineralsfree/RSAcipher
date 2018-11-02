package men.brakh;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import java.text.NumberFormat;
import java.util.Arrays;

public class CipherFrame extends JFrame implements ActionListener {
    JFileChooser fileChooser;
    private JFormattedTextField  qValue;
    private JFormattedTextField  pValue;
    private JFormattedTextField  KcValue;

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
        rMod.setText("0");
        eValue = new JFormattedTextField(formatter);
        eValue.setText("157");
        KcValue = new JFormattedTextField(formatter);
        KcValue.setText("0");

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
        add(new JLabel("K cекретный"));
        container.add(eValue);
        add(new JLabel("K oткр"));
        container.add(KcValue);
        this.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        if ("openFile".equals(e.getActionCommand())) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                try {
                    txt = RSA.readfile(file);
                    System.out.println("File content: " +RSA.getDecimaltext(txt,5));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
        }
        if ("Encrypt".equals(e.getActionCommand())) {
             if (file!=null){
                 if (getFileExtension(file).equals(".coded")) {
                     key = Integer.parseInt(rMod.getText());
                     int secretKey = Integer.parseInt(KcValue.getText());
                     String path = getOutDecodePath(fileChooser.getSelectedFile().getPath());
                     try (FileOutputStream fos = new FileOutputStream(path)) {
                         fos.write(RSA.decrypt(secretKey, key, toShortArray(RSA.readfile(fileChooser.getSelectedFile()))));
                     } catch (IOException e1) {
                         e1.printStackTrace();
                     }
                 } else {
                     p = Integer.parseInt(qValue.getText());
                     q = Integer.parseInt(pValue.getText());
                     E = Integer.parseInt(eValue.getText());
                     key = Integer.parseInt(rMod.getText());
                     x = Math.EulerFunc(p, q);
                     r = p * q;
                     rMod.setText(String.valueOf(r));
                     isOkay = true;
                     if (!Math.CheckSimple(E, x)) {
                         JOptionPane.showMessageDialog(null, "eValue and EulerFunc(p, q) are not relatively prime");
                         isOkay= false;
                     }
                     if ((E < 1) || (E > x)) {
                         JOptionPane.showMessageDialog(null, "invalid e");
                         isOkay= false;
                     }
                     if (!Math.isPrime(p)||!Math.isPrime(q)) {
                         JOptionPane.showMessageDialog(null, "p, q are relatively prime");
                         isOkay= false;


                     } if (r<=256){
                         JOptionPane.showMessageDialog(null, "mod key part must be at least BYTE_MAX_VALUE +1");
                         isOkay= false;
                     }
                     if (isOkay) {
                         int[] c = Math.gcd(x, E); // euler function, public key
                         System.out.println("GCD" + Arrays.toString(c));
                         {
                             String path = getOutEncodePath(fileChooser.getSelectedFile().getPath());
                             try (FileOutputStream fos = new FileOutputStream(path)) {
                                 fos.write(toByteArray(RSA.encrypt(E, r, RSA.readfile(fileChooser.getSelectedFile()))));
                                 KcValue.setText(String.valueOf(c[2]));
                             } catch (IOException e1) {
                                 e1.printStackTrace();
                             }

                         }


                     }
                     else{
                         JOptionPane.showMessageDialog(null, "encryption aborted");
                     }
                 }
        } else{
                 JOptionPane.showMessageDialog(null, "You haven't specified file => encryption aborted");
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

