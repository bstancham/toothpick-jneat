package jneat.common;

import java.util.*;
//import java.lang.*;
//import java.text.*;
import java.io.*;

public class IOseq {
   
    private String filename;
    private FileWriter fW;
    private BufferedWriter bW;
    private PrintWriter pW;
   
    private FileReader fR;
    private BufferedReader bR;
   
    public String IOseqRead() {
        String line = "?";
        StringTokenizer st;
	  
        try {
		 
            line = bR.readLine();
            if (line == null || line == "")
                line = "EOF";
            else {
            }
		 
        } 
        catch (Exception evt) {
            System.out.println("Error in jSeqRead : " + evt);
            line = "EXC";
			
        }
	  
        return line;
    }                                                   
   
    /**
     * jSeqFile constructor comment.
     */
    public IOseq() {
        filename = "Logxxxx.txt";
        fW = null;
        bW = null;
        pW = null;
	  
        fR = null;
        bR = null;
    }                                                   
   
    public IOseq(String name) {
        filename = name;
        fW = null;
        bW = null;
        pW = null;
	  
        fR = null;
        bR = null;
    }                                                   
   
    public void IOseqOpenW(boolean h) {
        try {
            fW = new FileWriter(filename);
            bW = new BufferedWriter(fW);
            pW = new PrintWriter(bW);
            if (h == true) {
                String testata = "Created in date --> " + (new Date());
                pW.println(testata);
            }
        } 
        catch (Exception evt) {
            System.out.println("Error in jSeqOpenW : " + evt);
        }
    }                                                   
   
    public void IOseqWrite(String testo) {
        pW.println(testo);
    }                                                   
   
    public void IOseqCloseR() {
        try {
            bR.close();
		 
        } 
        catch (Exception evt) {
            System.out.println("Error in jSeqCloseR : " + evt);
        }
    }                        
   
    public void IOseqCloseW() {
        try {
            pW.flush();
            bW.flush();
            fW.flush();
            fW.close();
        } 
        catch (Exception evt) {
            System.out.println("Error in jSeqCloseW : " + evt);
        }
    }                                                                        
   
    public boolean IOseqOpenR() {
        try { 
            fR = new FileReader(filename);
            bR = new BufferedReader(fR);
        } 
        catch (Exception evt) {
            //			   System.out.println("Error in jSeqOpenR : " + evt);
            return false;
        }
        return true;
    }

    public boolean fileExists() {
        return new File(filename).exists();
    }
   
}
