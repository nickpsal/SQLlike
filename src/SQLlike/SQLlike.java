package SQLlike;

/*
Author : nickpsal
*/

import java.util.ArrayList;
import java.util.HashMap;


import java.io.*; 

public class SQLlike 
{  
   
    public static void main(String[] args)
    {
//        The program starts by reading the code.txt to run the SQLlike code
//        that is inside that file
        System.out.println("Program Starts.");
        System.out.println("Checking SQLlike syntax code for Select and Join");
        String file = "src/SQLlike/ErotimaD.txt";
        Parser p = new Parser(file);
        System.out.println("The SQLlike code has been checked with 0 errors");
        file = "src/SQLlike/ErotimaDjoin.txt";
        p = new Parser(file);
        System.out.println("The SQLlike code has been checked with 0 errors");
    }
}
