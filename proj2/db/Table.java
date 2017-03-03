package db;

import java.util.*;
import java.io.*;
/**
 * Created by noraharhen on 2/20/17.
 */
public class Table extends HashMap {

    /** keeps track of user defined order of columns **/
    public String[] columnnames;
    /** keeps track of type of each db.column as defined by user **/
    public String[] columntypes;
    /** number of rows in table **/
    public int numRows;
    /** number of columns in table, this cannot change **/
    public int numColumns;


    public Table(String[] columnnames, String[] columntypes) {
        this.numRows = 0;
        this.numColumns = columnnames.length;
        this.columnnames = columnnames;
        this.columntypes = columntypes;
        for(int i = 0; i < columnnames.length; i++) {
            this.put(columnnames[i], new column());
        }
    }

    /** add a row by inserting in order a value into each db.column individually **/
    public void addRow(Value[] x) {
        for(int i = 0; i < numColumns; i++) {
            column temp = (column) get(columnnames[i]);
            Value valAdd = new Value(x[i]);
            temp.add(valAdd);
        }
        numRows += 1;
    }

    public row selectRow(int index) {
        //create an array of values which will be put in to the row constructor
        Value[] rowArray = new Value[numColumns];
        for(int i =0; i < numColumns; i++) { // iterate through all columns
            column temp = (column) get(columnnames[i]);
            rowArray[i] = new Value(temp.get(index));
        }
        return new row(rowArray);
    }

    /** print items in db.Table **/
    public String printTable() {
        System.out.println(this.toString());
        return this.toString();
    }


    /** joins two tables together **/
    public static Table join(Table x, Table y) {
       Table joinedTable = new Table(jointColumnNames(x,y), jointColumnTypes(x,y));
       ArrayList joinIndices = findSimItemsinColumn(x,y);
       LinkedList simColumn = findSimColumnNames(x,y);
       if (simColumn.size() == 0) {
           return cartesianJoin(x,y);
       }
       if (joinIndices.size() == 0) {
           return joinedTable;
       }
       ArrayList temp = (ArrayList) joinIndices.get(0);
       int columnLen = temp.size(); //gets the length of the db.column
       for (Object k: joinedTable.keySet()) {
           //handles the shared key set
           column newColumn = new column();
           if (simColumn.contains(k)) {
               for(int i = 0; i < columnLen; i+= 1) {
                   ArrayList wheretoJoin = (ArrayList) joinIndices.get(0);
                   int itemtoAdd = (int) wheretoJoin.get(i);
                   column compList = (column) x.get(k);
                   Value valAdd = new Value(compList.get(itemtoAdd));
                   newColumn.add(valAdd);
               }
               joinedTable.put(k,newColumn);
           } else if (x.containsKey(k)) {
               for(int i = 0; i < columnLen; i+= 1) {
                   ArrayList wheretoJoin = (ArrayList) joinIndices.get(0);
                   int itemtoAdd = (int) wheretoJoin.get(i);
                   column compList = (column) x.get(k);
                   Value valAdd = new Value(compList.get(itemtoAdd));
                   newColumn.add(valAdd);
               }
               joinedTable.put(k,newColumn);
           } else {
               for(int i = 0; i < columnLen; i+= 1) {
                   ArrayList wheretoJoin = (ArrayList) joinIndices.get(1);
                   int itemtoAdd = (int) wheretoJoin.get(i);
                   column compList = (column) y.get(k);
                   Value valAdd = new Value(compList.get(itemtoAdd));
                   newColumn.add(valAdd);
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
        String[] jointcolumnNames = new String[xLen+yLen];
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
                    Value valAdd = new Value(itemtoAdd);
                    for (int j =0; j < columnLen/x.numRows;j++)
                    newColumn.add(valAdd);
                }
            } else {
                column compList = (column) y.get(k);
                for (int i = 0; i < columnLen; i += 1) {
                    int itemtoAdd = (int) compList.get(i % x.numRows);
                    Value valAdd = new Value(itemtoAdd);
                    newColumn.add(valAdd);
                }
            }
            joinedTable.put(k,newColumn);
        }
        joinedTable.numRows = columnLen;
        return joinedTable;
    }

    /** helper method to find similar columns **/
    private static LinkedList<String> findSimColumnNames(Table x, Table y) {
        /** solution borrowed from http://stackoverflow.com/questions/23562308/java-find-matching-keys-of-two-hashmaps
         * Sets remove duplicates automatically which is why it's used here*
         * Orders stack so that the last similar db.column is on the bottom (the one that's supposed to be first is last)**/
        LinkedList<String> simColumnNameStack = new LinkedList<>();
        for (int i = 0; i < y.numColumns; i++) {
            for (int j = 0; j < x.numColumns; j++) {
                if (y.columnnames[i].equals(x.columnnames[j])) {
                    simColumnNameStack.addLast(x.columnnames[j]);
                }
            }
        }
        return simColumnNameStack;
    }

    /** helper method, this will become the db.column names for joined table**/
    private static String[] jointColumnNames(Table x, Table y) {
        LinkedList<String>  simColumnNames = findSimColumnNames (x,y);
        String[] returnArray = new String[x.columnnames.length + y.columnnames.length - simColumnNames.size()];
        for(int i = 0; i < simColumnNames.size(); i++) {
            returnArray[i] = simColumnNames.get(i);
        }

        int xnotSim = 0; //keep track of number of names in x but not sim

        for (int i = 0; i < x.columnnames.length; i++) {
            if (!simColumnNames.contains(x.columnnames[i])) {
                xnotSim += 1;
                returnArray[i + simColumnNames.size()] = x.columnnames[i];
            }
        }
        for (int j = 0; j < y.columnnames.length; j++) {
            if (!simColumnNames.contains(y.columnnames[j])) {
                returnArray[j + simColumnNames.size() + xnotSim] = y.columnnames[j];
            }
        }
        return returnArray;
    }

    /** this will be put into the db.Table constructor in the join method**/
    private static String[] jointColumnTypes(Table x, Table y) {
        String[] jointColumnNames = jointColumnNames(x,y);
        LinkedList  simColumnNames = findSimColumnNames (x,y);
        String[] returnArray = new String[jointColumnNames.length];
        for(int i = 0; i < simColumnNames.size(); i++) {
            //get index of simColumnNames
            String temp = (String) simColumnNames.get(i);
            int index = Arrays.binarySearch(x.columnnames,temp);
            returnArray[i] = (String) x.columntypes[index];
        }

        int xnotSim = 0; //keep track of number of names in x but not sim

        for (int i = 0; i < x.columnnames.length; i++) {
            if (!simColumnNames.contains(x.columnnames[i])) {
                xnotSim += 1;
                returnArray[i + simColumnNames.size()] = x.columntypes[i];
            }
        }
        for (int j = 0; j < y.columnnames.length; j++) {
            if (!simColumnNames.contains(y.columnnames[j])) {
                returnArray[j + simColumnNames.size() + xnotSim] = y.columntypes[j];
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

    public String toString() {
        String returnString = "";
        for (int i = 0; i < columnnames.length-1; i++) {
            returnString = returnString + columnnames[i].toString() + " " + columntypes[i].toString() +",";

        }
        returnString = returnString + columnnames[columnnames.length-1].toString() + " " + columntypes[columnnames.length-1].toString();
        returnString = returnString + System.lineSeparator();
        //System.out.println(returnString);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (j == numColumns - 1) {
                    column temp = (column) get(columnnames[j]);
                    returnString = returnString + temp.get(i).toString();
                } else {
                    column temp = (column) get(columnnames[j]);
                    returnString = returnString + temp.get(i).toString() + ",";
                }
            }
            returnString = returnString + System.lineSeparator();
        }
        return returnString;
    }

    // stores the string representation of file in a .tbl file
    public String Store(String name) {
        try (PrintWriter out = new PrintWriter( name + ".tbl" ) ) {
            out.println(this.toString());
            out.close();
        } catch (Exception e) {
                System.out.println("error: couldn't make file");}
        return " ";
    }

    public static Table Load(String name) throws IOException {
        //load file
        BufferedReader in = new BufferedReader(new FileReader(name));
        String line = in.readLine();
        String delims = "[ ,]";
        String[] header = line.split(delims);
        String[] columnnames = new String[header.length/2];
        String[] columntypes = new String[header.length/2];
        for(int i = 0; i < header.length; i++) {
            if(i % 2 == 0) {
                columnnames[i/2] = header[i];
            } else {
                columntypes[i/2] = header[i];
            }
        }
        //create new table associated with this
        Table newTable = new Table(columnnames, columntypes);
        String nextLine;
        //populate table with values, do this several time per row
        while ((nextLine = in.readLine()) != null) {
            String[] row = nextLine.split(delims);
            Value[] returnRow = new Value[row.length];
            for (int i = 0; i < row.length; i++) {
                returnRow[i] = new Value(convertType(row[i], newTable.columntypes[i]));
            }
            newTable.addRow(returnRow);
        }
        return newTable;
    }

    //helper method to convertTypes
    private static Object convertType(String item, String type) {
        if (type.equals("int")) {
            return Integer.parseInt(item);
        } else if (type.equals("int")) {
            return Float.parseFloat(item);
        } else if (type.equals("int")) {
            return item;
        } else {
            throw new Error();
        }
    }


    /*columns is an arraylist where the last value is the new column name*/
    /*String str = op.replace(" ", "");
        if(str.equals("*")){
           retVal = a*b;
        } else if(str.equals("+")){
           retVal = a+b;
        }//etc
        */
    public static column add(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            Value newValue = column1.addValue(((Value) column1.get(i)), value1);
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static column minus(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            Value newValue = column1.minusValue(((Value) column1.get(i)), value1);
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static column multiply(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            Value newValue = column1.multiplyValue(((Value) column1.get(i)), value1);
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static column divide(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            Value newValue = column1.divideValue(((Value) column1.get(i)), value1);
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static column addColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            Value newValue = column1.addValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
            }
        return newColumn;
    }

    public static column minusColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            Value newValue = column1.minusValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static column multiplyColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            Value newValue = column1.multiplyValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static column divideColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            Value newValue = column1.divideValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public column greaterThan(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.compare((Value) column1.get(i), value1) > 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column lessThan(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.compare((Value) column1.get(i), value1) < 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column equalTo(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.compare((Value) column1.get(i), value1) == 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column notEqualTo(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.compare((Value) column1.get(i), value1) != 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column greaterThanOrEqualTo(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.compare((Value) column1.get(i), value1) >= 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column lessThanOrEqualTo(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.compare((Value) column1.get(i), value1) <= 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column greaterThanColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.compare((Value) column1.get(i), (Value) column2.get(i)) > 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column lessThanColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.compare((Value) column1.get(i), (Value) column2.get(i)) < 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column equalToColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.compare((Value) column1.get(i), (Value) column2.get(i)) == 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column notEqualToColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.compare((Value) column1.get(i), (Value) column2.get(i)) != 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column greaterThanOrEqualToColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.compare((Value) column1.get(i), (Value) column2.get(i)) >= 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public column lessThanOrEqualToColumns(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.compare((Value) column1.get(i), (Value) column2.get(i)) <= 0) {
                Value newValue = new Value(((Value) column1.get(i)).value);
                newColumn.add(newValue);
            }
        }
        return newColumn;
    }

    public static void main(String[] args) {
        Value v0 = new Value(0);
        Value v1 = new Value("Good morning");
        Value v4 = new Value("hi");
        Value v2 = new Value(6);
        Value v3 = new Value(6.0);
        column c = new column();
        System.out.print(c.compare(v1, v4));


    }

}