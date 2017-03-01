
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import java.util.*;
/**
 * Created by noraharhen on 2/20/17.
 */
public class Table extends HashMap {

    /** keeps track of user defined order of columns **/
    private Character[] columnnames;
    /** keeps track of type of each column as defined by user **/
    private String[] columntypes;
    /** number of rows in table **/
    public int numRows;
    /** number of columns in table, this cannot change **/
    public int numColumns;


    public Table(Character[] columnnames, String[] columntypes) {
        this.numRows = 0;
        this.numColumns = columnnames.length;
        this.columnnames = columnnames;
        this.columntypes = columntypes;
        for(int i = 0; i <columnnames.length; i++) {
            this.put(columnnames[i], new column<Integer>());
        }
    }

    /** add a row by inserting in order a value into each column individually **/
    public void addRow(int[] x) {
        for(int i = 0; i < numColumns; i++) {
            column temp = (column) get(columnnames[i]);
            temp.add(x[i]);
        }
        numRows += 1;
    }

    /** print items in Table **/
    public void printTable() {
        printHeader();
        System.out.println(" ");
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numColumns; j++) {
                if (j == numColumns-1) {
                    column temp = (column) get(columnnames[j]);
                    System.out.print(temp.get(i));
                } else {
                    column temp = (column) get(columnnames[j]);
                    System.out.print(temp.get(i));
                    System.out.print(",");
                }
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }

    /** helper method for printTable **/
    private void printHeader() {
        for( int i = 0; i < numColumns-1; i++) {
            System.out.print(columnnames[i] + " " + columntypes[i] + ",");
        }
        System.out.print(columnnames[numColumns-1] + " " +columntypes[numColumns-1] + "");
    }

    /** joins two tables together **/
    public static Table join(Table x, Table y) {
       Table joinedTable = new Table(jointColumnNames(x,y), jointColumnTypes(x,y));
       ArrayList joinIndices = findSimItemsinColumn(x,y);
       LinkedList simColumn = findSimColumnNames(x,y);
       if (simColumn.size() == 0) {
           return cartesianJoin(x,y);
       }
       ArrayList temp = (ArrayList) joinIndices.get(0);
        int columnLen = temp.size(); //gets the length of the column
       for (Object k: joinedTable.keySet()) {
           //handles the shared key set
           column newColumn = new column();
           if (simColumn.contains(k)) {
               for(int i = 0; i < columnLen; i+= 1) {
                   ArrayList wheretoJoin = (ArrayList) joinIndices.get(0);
                   int itemtoAdd = (int) wheretoJoin.get(i);
                   column compList = (column) x.get(k);
                   newColumn.add(compList.get(itemtoAdd));
               }
               joinedTable.put(k,newColumn);
           } else if (x.containsKey(k)) {
               for(int i = 0; i < columnLen; i+= 1) {
                   ArrayList wheretoJoin = (ArrayList) joinIndices.get(0);
                   int itemtoAdd = (int) wheretoJoin.get(i);
                   column compList = (column) x.get(k);
                   newColumn.add(compList.get(itemtoAdd));
               }
               joinedTable.put(k,newColumn);
           } else {
               for(int i = 0; i < columnLen; i+= 1) {
                   ArrayList wheretoJoin = (ArrayList) joinIndices.get(1);
                   int itemtoAdd = (int) wheretoJoin.get(i);
                   column compList = (column) y.get(k);
                   newColumn.add(compList.get(itemtoAdd));
               }
               joinedTable.put(k,newColumn);
           }
       }
        joinedTable.numRows = columnLen;
        return joinedTable;
    }

    /**helper method for cartesian join **/
    private static Table cartesianJoin(Table x, Table y) {
        int xLen = x.columnnames.length;
        int yLen = y.columnnames.length;
        Character[] jointcolumnNames = new Character[xLen+yLen];
        System.arraycopy(x.columnnames, 0, jointcolumnNames, 0, xLen);
        System.arraycopy(y.columnnames, 0, jointcolumnNames, xLen, yLen);

        String[] jointcolumnTypes = new String[xLen+yLen];
        System.arraycopy(x.columntypes, 0, jointcolumnTypes, 0, xLen);
        System.arraycopy(y.columntypes, 0, jointcolumnTypes, xLen, yLen);

        Table joinedTable = new Table(jointcolumnNames, jointcolumnTypes);
        int columnLen = x.numRows + y.numRows;

        for(Object k: joinedTable.keySet()) {
            column newColumn = new column();
            if (x.containsKey(k)) {
                column compList = (column) x.get(k);
                for (int i = 0; i < x.numRows; i += 1) {
                    int itemtoAdd = (int) compList.get(i);
                    for (int j =0; j < columnLen/x.numRows;j++)
                    newColumn.add(itemtoAdd);
                }
            } else {
                column compList = (column) y.get(k);
                for (int i = 0; i < columnLen; i += 1) {
                    int itemtoAdd = (int) compList.get(i % x.numRows);
                    newColumn.add(itemtoAdd);
                }
            }
            joinedTable.put(k,newColumn);
        }
        joinedTable.numRows = columnLen;
        return joinedTable;
    }

    /** helper method to find similar columns **/
    private static LinkedList<Character> findSimColumnNames(Table x, Table y) {
        /** solution borrowed from http://stackoverflow.com/questions/23562308/java-find-matching-keys-of-two-hashmaps
         * Sets remove duplicates automatically which is why it's used here*
         * Orders stack so that the last similar column is on the bottom (the one that's supposed to be first is last)**/
        LinkedList<Character> simColumnNameStack = new LinkedList<Character>();
        for (int i = 0; i < y.numColumns; i++) {
            for (int j = 0; j < x.numColumns; j++) {
                if (y.columnnames[i].equals(x.columnnames[j])) {
                    simColumnNameStack.addLast(x.columnnames[j]);
                }
            }
        }
        return simColumnNameStack;
    }

    /** helper method, this will become the column names for joined table**/
    private static Character[] jointColumnNames(Table x, Table y) {
        LinkedList<Character>  simColumnNames = findSimColumnNames (x,y);
        Character[] returnArray = new Character[x.columnnames.length+y.columnnames.length-simColumnNames.size()];
        for (int i = 0; i < x.columntypes.length; i++) {
            returnArray[i] = x.columnnames[i];
        }
        for (int j = 0; j < y.columnnames.length; j++) {
            if (!simColumnNames.contains(y.columnnames[j])) {
                returnArray[j + x.columnnames.length-1] = y.columnnames[j];
            }
        }
        for (int i = 0; i < simColumnNames.size(); i++) {
            for (int j = 0; j < returnArray.length; j++) {
                if (simColumnNames.peek().equals(returnArray[j])) {
                    Character[] arraycopy = new Character[returnArray.length];
                    /*copies items after matching column name*/
                    System.arraycopy(returnArray, j + 1, arraycopy, j + 1, arraycopy.length - j - 1);
                    /*copies items before matching column name*/
                    System.arraycopy(returnArray, 0, arraycopy, 1, j);
                    arraycopy[0] = returnArray[j];
                    returnArray = arraycopy;
                }
            }
        }
        return returnArray;
    }

    /** this will be put into the Table constructor in the join method**/
    private static String[] jointColumnTypes(Table x, Table y) {
        Character[] jointColumnNames = jointColumnNames(x,y);
        LinkedList  simColumnNames = findSimColumnNames (x,y);
        String[] returnArray = new String[jointColumnNames.length];
        for (int i = 0; i < x.columntypes.length; i++) {
            returnArray[i] = x.columntypes[i];
        }
        for (int j = 0; j < y.columntypes.length; j++) {
            if (!simColumnNames.contains(y.columnnames[j])) {
                returnArray[j+x.columntypes.length-1] = y.columntypes[j];
            }
        }
        return returnArray;
    }

    /** another helper method, finds the indexes at which we must merge in our new joint table **/
    private static ArrayList findSimItemsinColumn(Table x, Table y) {
        LinkedList commonKeys = findSimColumnNames(x,y);
        ArrayList xindex = new ArrayList();
        ArrayList yindex = new ArrayList();
        for (Object key: commonKeys) {
            column xval = (column) x.get(key);
            column yval = (column) y.get(key);
            for(int i = 0; i < xval.size(); i++) {
                for (int j = 0; j <yval.size(); j++) {
                    if(xval.get(i).equals(yval.get(j))) {
                        xindex.add(i);
                        yindex.add(j);
                    }
                }
            }
        }
        ArrayList returnArray = new ArrayList();
        returnArray.add(xindex);
        returnArray.add(yindex);
        return returnArray;
    }


    public static Table select(ArrayList<Character> columns, ArrayList<String> tables) {
        if (columns.size() <= 0) {
            /* Throw exception???*/
        }
        if (tables.size() <= 0) {
            /* Throw exception???*/
        }
        /*Identify types and store into newcolumntypes*/
        if (tables.size() > 1) {
            joinMultipleTables(tables);
            tables.add(0, "joinedtemp");
        }
        Table newTable = (Table) allTables.get(tables.get(0));
        for (int i = 0; i < newTable.size(); i++) {
            if (!Arrays.asList(newTable.columnnames).contains(columns.get(i))) {
                newTable.remove(columns.get(i));
            }
        }
        return newTable;
    }

    /*columns is an arraylist where the last value is the new column name*/
    /*String str = op.replace(" ", "");
        if(str.equals("*")){
           retVal = a*b;
        } else if(str.equals("+")){
           retVal = a+b;
        }//etc
        */
    public static Table Binaryselect(ArrayList<Character> columns, ArrayList<String> tables, String operator) {
        if (tables.size() > 1) {
            joinMultipleTables(tables);
            tables.add(0, "joinedtemp");
        }
        Table newTable = (Table) allTables.get(tables.get(0));
        if (operator.equals("*")){

        } else if (operator.equals("-")) {

        } else if (operator.equals("+")) {

        } else if (operator.equals("/")) {

        }
    }

    public static column addColumns(column column1, column column2) {
        column newColumn = new column();
        if (column1.get(0) instanceof Integer) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Integer) column1.get(i) + (Integer) column2.get(i));
            }
        } else if (column1.get(0) instanceof Float) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Float) column1.get(i) + (Float) column2.get(i));
            }
        } else {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((String) column1.get(i) + (String) column2.get(i));
            }
        }
        return newColumn;
    }

    public static column minusColumns(column column1, column column2) {
        column newColumn = new column();
        if (column1.get(0) instanceof Integer) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Integer) column1.get(i) - (Integer) column2.get(i));
            }
        } else if (column1.get(0) instanceof Float) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Float) column1.get(i) - (Float) column2.get(i));
            }
        } else {
            /*throw exceptionerror*/
        }
        return newColumn;
    }

    public static column multiplyColumns(column column1, column column2) {
        column newColumn = new column();
        if (column1.get(0) instanceof Integer) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Integer) column1.get(i) * (Integer) column2.get(i));
            }
        } else if (column1.get(0) instanceof Float) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Float) column1.get(i) * (Float) column2.get(i));
            }
        } else {
            /*throw exceptionerror*/
        }
        return newColumn;
    }

    public static column divideColumns(column column1, column column2) {
        column newColumn = new column();
        if (column1.get(0) instanceof Integer) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Integer) column1.get(i) / (Integer) column2.get(i));
            }
        } else if (column1.get(0) instanceof Float) {
            for (int i = 0; i < column2.size(); i++) {
                newColumn.add((Float) column1.get(i) / (Float) column2.get(i));
            }
        } else {
            /*throw exceptionerror*/
        }
        return newColumn;
    }


    public static Table joinMultipleTables(ArrayList<String> tables) {
        int i = tables.size();
        while (i > 1) {
            allTables.put("joinedtemp", join(allTables.get(tables.get(0)), allTables.get(tables.get(1))));
            tables.add(0, allTables.get("joinedtemp"));
            tables.remove(1);
            tables.remove(2);
            i--;
        }
        return allTables.get("joinedtemp");
    }

    public static void main(String[] args) {
        Value v0 = new Value(0);
        Value v1 = new Value("Good morning");
        Value v2 = new Value();
        /*
        Character[] x = {'x','y', 'z'};
        String[] n = {"int", "int", "int"};
        Table T1 = new Table(x,n);
        int [] firstrow = {2,5,4};
        T1.addRow(firstrow);
        int[] secondrow = {8,3,9};
        T1.addRow(secondrow);
        T1.printTable();

        Character[] l = {'x','b'};
        String[] m = {"int", "int"};
        Table T2 = new Table(l,m);
        int[] frow = {7,0};
        T2.addRow(frow);
        int[] srow = {2,8};
        T2.addRow(srow);
        T2.printTable();
        Table T3 = join(T1,T2);
        T3.printTable();
        */
    }
}
