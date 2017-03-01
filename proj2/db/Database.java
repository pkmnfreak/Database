package db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringJoiner;


public class Database {
    public Database() {
        Map allTables = new HashMap();
    }

    //parses input
    public String transact(String query) {
<<<<<<< HEAD
        return "YOUR CODE HERE";
        /*make an arraylist of columns and tables inputted*/
        /*Get rid of whitespace String str = op.replace(" ", "");*/
=======
        String[] input = query.split(" ");
        for(int i = 0; i < input.length; i++) {
            System.out.println(input[i]);
        }
       /* if (input[0].equals("load"))
            return load(input[1]);
        else if (input[0].equals("print")) {
            return print(query);
        }
        else if (input[0].equals("join"))
            return select();
        else
            return "Please enter a valid input";
            */
       return "hi";
    }

    // creates new key which is the name of table and the value is the table
    /*public String createTable(String name) {
        //Table newTable =  new Table(columnnames, columntypes);
        //allTables.put(input[2].toString, newTable);
    }

    //load file
    /*public String load(String x) {
        FileReader tableReader = new FileReader(x + ".java");
        BufferedReader tableBuffRead = new BufferedReader(tableReader);
        //create a table here
        String firstLine = tableBuffRead.readLine();
        Character[] columnnames = new Character[firstLine.length()/2];
        String[] columntypes = new String[firstLine.length()/2];
        for(int i = 0; i < columnnames.length; i++) {
            if (i % 2 == 0) {
                columnnames[i] = firstLine[i];
            } else {
                columntypes[i] = firstLine[i];
            }
        }
        Table newTable = new Table(columnnames, columntypes;)
        //put table into database
        allTables.put(key,newTable);
            return " ";
    }


    //create a tbl file from existing file
    public String store() {
        FileWriter tableWriter = new FileWriter();
        BufferedWriter tableBuffWrite = new BufferedWriter(tableWriter);
    }

    // insert values into existing table
    public String insert(array values) {
        if (allTables.contains(key)) {
            Table temp = allTables.get(key);
            temp.addRow(values);
        }

    }

    // take in key of table and then calls printTable method on value associated with key
    public String print(key) {
        Table temp = allTables.get(key);
        return temp.printTable();
    }

    public String select() {

>>>>>>> 758f5e2a16dfa2f743ca2e59de78a6db42b65bad
    }
*/
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected a single query argument");
            return;
        }
        //String input = args.split(" ");

        //eval(args[0]);
    }



}
