package db;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringJoiner;


public class Database {
    private static Map allTables;

    public Database() {
        this.allTables = new HashMap<String, Table>();
    }

    public static Table select(String[] columns, String[] tables) {
        if (columns.length <= 0) {
            /* Throw exception???*/
        }
        if (tables.length <= 0) {
            /* Throw exception???*/
        }
        /*Identify types and store into newcolumntypes*/
        if (tables.length > 1) {
            joinMultipleTables(tables);
            String[] copyTables = new String[tables.length + 1];
            System.arraycopy(tables, 0, copyTables, 1, tables.length);
            copyTables[0] = "joinedtemp";
            tables = copyTables;
        }
        Table newTable = (Table) allTables.get(tables[0]);
        for (int i = 0; i < newTable.size(); i++) {
            if (!(Arrays.asList(columns).contains(newTable.columnnames[i]))) {
                newTable.remove(newTable.columnnames[i]);
                String[] newColumnNames = new String[newTable.columnnames.length - 1];
                String[] newColumnTypes = new String[newTable.columntypes.length - 1];
                System.arraycopy(newTable.columnnames, 0, newColumnNames, 0, i);
                System.arraycopy(newTable.columnnames, i + 1, newColumnNames, i, newTable.columnnames.length - i - 1);
                System.arraycopy(newTable.columntypes, 0, newColumnTypes, 0, i);
                System.arraycopy(newTable.columntypes, i + 1, newColumnTypes, i, newTable.columntypes.length - i - 1);
                newTable.columnnames = newColumnNames;
                newTable.columntypes = newColumnTypes;
                newTable.numColumns--;
                i--;
            }
        }
        return newTable;
    }


    public static Table joinMultipleTables(String[] tables) {
        int i = tables.length;
        while (i > 1) {
            Table newTable = (Table) allTables.get(tables[0]);
            allTables.put("joinedtemp", newTable.join(newTable, (Table) allTables.get(tables[1])));
            tables[0] = "joinedtemp";
            String[] copyTables = new String[tables.length - 1];
            System.arraycopy(tables, 0, copyTables, 0, 1);
            System.arraycopy(tables, 2, copyTables, 1, tables.length - 2);
            tables = copyTables;
            i--;
        }
        return (Table) allTables.get("joinedtemp");
    }

    public static Table Binaryselect(String[] columns, String[] tables, String operator) {
        if (tables.length > 1) {
            joinMultipleTables(tables);
            String[] copyTables = new String[tables.length + 1];
            System.arraycopy(tables, 0, copyTables, 1, tables.length);
            copyTables[0] = "joinedtemp";
            tables = copyTables;
        }
        if (columns.length != 3) {
            /*throw error*/
        }
        Table table = (Table) allTables.get(tables[0]);
        String[] newColumn = {columns[2]};
        String[] newType = {((column) table.get(columns[0])).get(0).getClass().getName()};
        Table resultTable = new Table(newColumn, newType);
        resultTable.numRows = table.numRows;
        if (operator.equals("*")){
            resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.multiplyColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
        } else if (operator.equals("-")) {
            resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.minusColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
        } else if (operator.equals("+")) {
            resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.addColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
        } else if (operator.equals("/")) {
            resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.divideColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
        }
        return resultTable;
    }


    //parses input
    public String transact(String query) {
        return "YOUR CODE HERE";
        /*make an arraylist of columns and tables inputted*/
        /*Get rid of whitespace String str = op.replace(" ", "");*/
        /*
        String[] input = query.split(" ");

        for(int i = 0; i < input.length; i++) {
            System.out.println(input[i]);
        }
        */
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

    }
*/
    public static void main(String[] args) {
        /*if (args.length != 1) {
            System.err.println("Expected a single query argument");
            return;
        } */
        Database db = new Database();
        String[] x = {"x", "y", "z"};
        String[] y = {"x", "y", "z"};
        String[] n = {"int", "int", "int"};
        Table T1 = new Table(x, n);
        Object[] firstrow = {2, 5, 4};
        T1.addRow(firstrow);
        Object[] secondrow = {8, 3, 9};
        T1.addRow(secondrow);
        String[] l = {"x","b"};
        String[] m = {"int", "int"};
        Table T2 = new Table(l,m);
        Object[] frow = {7,0};
        T2.addRow(frow);
        Object[] srow = {2,8};
        T2.addRow(srow);
        db.allTables.put("T1", T1);
        db.allTables.put("T2", T2);
        String[] columns = {"x", "z", "a"};
        String[] tables = {"T1", "T2"};
        Binaryselect(columns, tables, "*").printTable();
        /*
        T2.printTable();
        Table T3 = join(T1,T2);
        T3.printTable();
        //String input = args.split(" ");

        //eval(args[0]); */
    }



}
