package men.brakh;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class RSA {
    private File file;

    public  static   byte[] readfile(File file) throws IOException {
        Path filePath = Paths.get(file.getAbsolutePath());
        return Files.readAllBytes(filePath);


    }
    public static String getDecimaltext(byte[] c, int size ){

        if (c.length<size){
               size = c.length;
        }
        StringBuilder sb = new StringBuilder();

        for (int i=0;i<size;i++){
            sb.append(c[i]);
            sb.append(" ");

        }
        return sb.toString();
    }
    public static String getDecimaltext(short[] c, int size ){

        if (c.length<size){
            size = c.length;
        }
        StringBuilder sb = new StringBuilder();

        for (int i=0;i<size;i++){
            sb.append(c[i]);
            sb.append(" ");

        }
        return sb.toString();
    }
    public static short[] encrypt(int e, int r,byte[] plaintext){
        short[] d = new short[plaintext.length];
        for(int i =0;i<plaintext.length;i++){
            d[i] = (short)Math.pow(plaintext[i],e,r);
        }
        System.out.println( "Coded: " + getDecimaltext(d,50));
        return d;
    }
    static byte[] decrypt(int d, int r,short[] cipherText){

        byte[] m = new byte[cipherText.length];
        for(int i =0;i<cipherText.length;i++){
            m[i] = (byte)Math.pow(cipherText[i],d,r);
        }
        System.out.println("Decoded "+  getDecimaltext(m,50));
        System.out.println("Ciphered " + getDecimaltext(cipherText,50));
        return m;

    }
}
