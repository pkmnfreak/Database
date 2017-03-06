package db;

import java.util.*;
import java.io.*;
/**
 * Created by noraharhen on 2/20/17.
 */
public class Table extends HashMap {

    /** keeps track of user defined order of columns **/
    String[] columnnames;
    /** keeps track of type of each db.column as defined by user **/
    String[] columntypes;
    /** number of rows in table **/
    int numRows;
    /** number of columns in table, this cannot change **/
    int numColumns;


    public Table(String[] columnnames, String[] columntypes) {
        this.numRows = 0;
        this.numColumns = columnnames.length;
        this.columnnames = columnnames;
        this.columntypes = columntypes;
        for (int i = 0; i < columnnames.length; i++) {
            this.put(columnnames[i], new column());
        }
    }

    public String[] getColNames() {
        return this.columnnames;
    }

    public String[] getColTypes() {
        return this.columntypes;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCol() {
        return this.numColumns;
    }
    /** add a row by inserting in order a value into each db.column individually **/
    public void addRow(Value[] x) {
        for (int i = 0; i < numColumns; i++) {
            column temp = (column) get(columnnames[i]);
            Value valAdd = x[i];
            temp.add(valAdd);
        }
        numRows += 1;
    }

    public row selectRow(int index) {
        //create an array of values which will be put in to the row constructor
        Value[] rowArray = new Value[numColumns];
        for (int i = 0; i < numColumns; i++) { // iterate through all columns
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
        Table joinedTable = new Table(jointColumnNames(x, y), jointColumnTypes(x, y));
        ArrayList joinIndices = findSimItemsinColumn(x, y);
        LinkedList simColumn = findSimColumnNames(x, y);
        if (simColumn.size() == 0) {
            return cartesianJoin(x, y);
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
                for (int i = 0; i < columnLen; i += 1) {
                    ArrayList wheretoJoin = (ArrayList) joinIndices.get(0);
                    int itemtoAdd = (int) wheretoJoin.get(i);
                    column compList = (column) x.get(k);
                    newColumn.add(compList.get(itemtoAdd));
                }
                joinedTable.put(k, newColumn);
            } else if (x.containsKey(k)) {
                for (int i = 0; i < columnLen; i += 1) {
                    ArrayList wheretoJoin = (ArrayList) joinIndices.get(0);
                    int itemtoAdd = (int) wheretoJoin.get(i);
                    column compList = (column) x.get(k);
                    newColumn.add(compList.get(itemtoAdd));
                }
                joinedTable.put(k, newColumn);
            } else {
                for (int i = 0; i < columnLen; i += 1) {
                    ArrayList wheretoJoin = (ArrayList) joinIndices.get(1);
                    int itemtoAdd = (int) wheretoJoin.get(i);
                    column compList = (column) y.get(k);
                    newColumn.add(compList.get(itemtoAdd));
                }
                joinedTable.put(k, newColumn);
            }
        }
        joinedTable.numRows = columnLen;
        joinedTable.numColumns = joinedTable.size();
        return joinedTable;
    }

    /**helper method for cartesian join **/
    private static Table cartesianJoin(Table x, Table y) {
        int xLen = x.columnnames.length;
        int yLen = y.columnnames.length;
        String[] jointcolumnNames = new String[xLen + yLen];
        System.arraycopy(x.columnnames, 0, jointcolumnNames, 0, xLen);
        System.arraycopy(y.columnnames, 0, jointcolumnNames, xLen, yLen);

        String[] jointcolumnTypes = new String[xLen + yLen];
        System.arraycopy(x.columntypes, 0, jointcolumnTypes, 0, xLen);
        System.arraycopy(y.columntypes, 0, jointcolumnTypes, xLen, yLen);

        Table joinedTable = new Table(jointcolumnNames, jointcolumnTypes);
        int columnLen;
        if (x.numRows == y.numRows) {
            columnLen = x.numRows;
        } else {
            columnLen = x.numRows + y.numRows;
        }
        for (Object k: joinedTable.keySet()) {
            column newColumn = new column();
            if (x.containsKey(k)) {
                column compList = (column) x.get(k); //grab column
                for (int i = 0; i < columnLen; i += 1) {
                    Value itemtoAdd = (Value) compList.get(i % x.numRows);
                    //for (int j =0; j < columnLen/x.numRows;j++)
                    newColumn.add(itemtoAdd); // add to new column
                }
            } else {
                column compList = (column) y.get(k);
                for (int i = 0; i < columnLen; i += 1) {
                    Value itemtoAdd = (Value) compList.get(i % y.numRows);
                    newColumn.add(itemtoAdd);
                }
            }
            joinedTable.put(k, newColumn);
        }
        joinedTable.numRows = columnLen;
        return joinedTable;
    }

    /** helper method to find similar columns **/
    private static LinkedList<String> findSimColumnNames(Table x, Table y) {
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
        LinkedList<String>  simColumnNames = findSimColumnNames(x, y);
        String[] returnArray = new String[x.columnnames.length
                + y.columnnames.length - simColumnNames.size()];
        if (simColumnNames.size() == 0) {
            for (int i = 0; i < x.numColumns; i++) {
                returnArray[i] = x.columnnames[i];
            }
            for (int j = 0; j < y.numColumns; j++) {
                returnArray[j + x.numColumns] = y.columnnames[j];
            }
        } else {

            for (int i = 0; i < simColumnNames.size(); i++) {
                returnArray[i] = simColumnNames.get(i);
            }

            int xnotSim = 0; //keep track of number of names in x but not sim

            for (int i = 0; i < x.columnnames.length; i++) {
                if (!simColumnNames.contains(x.columnnames[i])) {
                    returnArray[xnotSim + simColumnNames.size()] = x.columnnames[i];
                    xnotSim += 1;
                }
            }
            int indextracker = 0;
            for (int j = 0; j < y.columnnames.length; j++) {
                if (!simColumnNames.contains(y.columnnames[j])) {
                    returnArray[indextracker + simColumnNames.size() + xnotSim] = y.columnnames[j];
                    indextracker += 1;
                }
            }
        }
        return returnArray;
    }

    /** this will be put into the db.Table constructor in the join method**/
    private static String[] jointColumnTypes(Table x, Table y) {
        String[] jointColumnNames = jointColumnNames(x, y);
        LinkedList  simColumnNames = findSimColumnNames(x, y);
        String[] returnArray = new String[jointColumnNames.length];
        for (int i = 0; i < simColumnNames.size(); i++) {
            //get index of simColumnNames
            String temp = (String) simColumnNames.get(i);
            int index = Arrays.asList(x.columnnames).indexOf(temp);
            returnArray[i] = (String) x.columntypes[index];
        }
        if (simColumnNames.size() == 0) {
            for (int i = 0; i < x.numColumns; i++) {
                returnArray[i] = x.columntypes[i];
            }
            for (int j = 0; j < y.numColumns; j++) {
                returnArray[j + x.numColumns] = y.columntypes[j];
            }
        } else {

            int xnotSim = 0; //keep track of number of names in x but not sim

            for (int i = 0; i < x.columnnames.length; i++) {
                if (!simColumnNames.contains(x.columnnames[i])) {
                    returnArray[xnotSim + simColumnNames.size()] = x.columntypes[i];
                    xnotSim += 1;
                }
            }
            int indextracker = 0;
            for (int j = 0; j < y.columnnames.length; j++) {
                if (!simColumnNames.contains(y.columnnames[j])) {
                    returnArray[indextracker + simColumnNames.size() + xnotSim] = y.columntypes[j];
                    indextracker += 1;
                }
            }
        }
        return returnArray;
    }

    /** another helper method, finds the indexes at which we must merge in our new joint table **/
    private static ArrayList findSimItemsinColumn(Table x, Table y) {
        LinkedList commonKeys = findSimColumnNames(x, y);
        ArrayList xindex = new ArrayList();
        ArrayList yindex = new ArrayList();
        if (commonKeys.size() > 0) {
            String key = (String) commonKeys.get(0);
            column xval = (column) x.get(key);
            column yval = (column) y.get(key);
            for (int i = 0; i < xval.size(); i++) {
                for (int j = 0; j < yval.size(); j++) {
                    Value currX = (Value) xval.get(i);
                    String xName = (String) currX.label;
                    Value currY = (Value) yval.get(j);
                    String yName = (String) currY.label;
                    if (currX.equals(currY)) {
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
        for (int i = 0; i < columnnames.length - 1; i++) {
            returnString = returnString + columnnames[i].toString()
                    + " " + columntypes[i].toString() + ",";

        }
        returnString = returnString + columnnames[columnnames.length - 1].toString()
                + " " + columntypes[columnnames.length - 1].toString();
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

    // stores the string representation of file in a multiColumnJoin2.tbl file
    public void Store(String name) {
        try {
            PrintWriter out = new PrintWriter( name + ".tbl" );
            out.println(this.toString());
            out.close();
        } catch (FileNotFoundException  e) {
                System.out.println("ERROR: couldn't make file" + e);
        } catch (IOException e) {
                System.out.println("ERROR: couldn't make file" + e);
       }
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


    public static Object add(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.addValue(((Value) column1.get(i)), value1) instanceof String) {
                return "ERROR: Mixing unmatching types";
            }
            Value newValue = (Value) column1.addValue(((Value) column1.get(i)), value1);
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static Object minus(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.minusValue(((Value) column1.get(i)), value1) instanceof String) {
                return "ERROR: Invalid Operation";
            }
            Value newValue = (Value) column1.minusValue(((Value) column1.get(i)), value1);
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static Object multiply(column column1, Value value1) {
        column newColumn = new column();
        try {
            for (int i = 0; i < column1.size(); i++) {
                Value newValue = (Value) column1.multiplyValue(((Value) column1.get(i)), value1);
                newColumn.add(newValue);
            }
        } catch(ClassCastException e) {
            return "ERROR: Invalid Operation";
        }
        return newColumn;
    }

    public static Object divide(column column1, Value value1) {
        column newColumn = new column();
        for (int i = 0; i < column1.size(); i++) {
            if (column1.divideValue(((Value) column1.get(i)), value1) instanceof String) {
                return "ERROR: Invalid Operation";
            }
            Value newValue = (Value) column1.divideValue(((Value) column1.get(i)), value1);
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static Object addcol(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.addValue(((Value) column1.get(i)), ((Value) column2.get(i))) instanceof String) {
                return "ERROR: Invalid Operation";
            }
            Value newValue = (Value) column1.addValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static Object mincol(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.minusValue(((Value) column1.get(i)), ((Value) column2.get(i))) instanceof String) {
                return "ERROR: Invalid operations";
            }
            Value newValue = (Value) column1.minusValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static Object mulcol(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.multiplyValue(((Value) column1.get(i)), ((Value) column2.get(i))) instanceof String) {
                return "ERROR: Invalid operations";
            }
            Value newValue = (Value) column1.multiplyValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
        }
        return newColumn;
    }

    public static Object divcol(column column1, column column2) {
        column newColumn = new column();
        for (int i = 0; i < column2.size(); i++) {
            if (column1.divideValue(((Value) column1.get(i)), ((Value) column2.get(i))) instanceof String) {
                return "ERROR: Invalid operations";
            }
            Value newValue = (Value) column1.divideValue(((Value) column1.get(i)), ((Value) column2.get(i)));
            newColumn.add(newValue);
        }
        return newColumn;
    }




    Table selectConditional(String[] col, String[] tables, String conditional, String[] comparisons, Database db) {
        try {
            if (tables.length > 1) {
                tables = db.checkMultipleTables(tables);
            }
        } catch (NullPointerException e) {
            System.out.println("Malformed query oops");
        }
        Table table = db.allTables.get(tables[0]);
        String[] newColumn;
        String[] newType;
        if (col[0].equals("*")) {
            newColumn = table.getColNames();
            newType = table.getColTypes();
        } else {
            newColumn = col;
            newType = new String[col.length];
            for (int i = 0; i < col.length; i++) {
                newType[i] = table.getColTypes()[Arrays.asList(table.getColNames()).indexOf(col[i])];
            }
        }
        Table resultTable = new Table(newColumn, newType);
        Value[] tempForAddRow = new Value[newColumn.length];
        if (conditional.equals(">")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).greaterThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).greaterThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("<")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).lessThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).lessThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("==")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).equalTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).equalTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("!=")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).notEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).notEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals(">=")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).greaterThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).greaterThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("<=")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).lessThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).lessThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        }
        resultTable.numRows = ((column) resultTable.get(newColumn[0])).size();
        db.allTables.put("table" + tables[0] + conditional, resultTable);
        return resultTable;
    }

    Table selectConditional(String[] columns, Table table, String conditional, String[] comparisons, Database db) {
        String[] newColumn;
        String[] newType;
        if (columns[0].equals("*")) {
            newColumn = table.getColNames();
            newType = table.getColTypes();
        } else {
            newColumn = columns;
            newType = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                newType[i] = table.getColTypes()[Arrays.asList(table.getColNames()).indexOf(columns[i])];
            }
        }
        Table resultTable = new Table(newColumn, newType);
        Value[] tempForAddRow = new Value[newColumn.length];
        if (conditional.equals(">")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).greaterThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).greaterThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("<")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).lessThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).lessThan(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("==")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).equalTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).equalTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("!=")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).notEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).notEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals(">=")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).greaterThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).greaterThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        } else if (conditional.equals("<=")) {
            for (int j = 0; j < table.getNumRows(); j++) {
                if (Arrays.asList(table.getColNames()).contains(comparisons[1])) {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = (Value) (((column) table.get(comparisons[1])).get(j));
                    if (((column) table.get(comparisons[0])).lessThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                } else {
                    Value x = (Value) (((column) table.get(comparisons[0])).get(j));
                    Value y = ((column) table.get(comparisons[0])).createValue(comparisons[1]);
                    if (((column) table.get(comparisons[0])).lessThanOrEqualTo(x, y)) {
                        for (int k = 0; k < newColumn.length; k++) {
                            tempForAddRow[k] = (Value) ((column) table.get(newColumn[k])).get(j);
                        }
                        resultTable.addRow(tempForAddRow);
                    }
                }
            }
        }
        resultTable.numRows = ((column) resultTable.get(newColumn[0])).size();
        db.allTables.put("table" + conditional, resultTable);
        return resultTable;
    }



    public Object select(String[] columns, String[] tables, String operator, Database db) {
        if (tables.length > 1) {
            tables = db.checkMultipleTables(tables);
        }
        Table table = (Table) db.allTables.get(tables[0]);
        int index = Arrays.asList(table.getColNames()).indexOf(columns[0]);
        String[] newColumn = {columns[2]}; String[] newType = {table.getColTypes()[index]};
        Table resultTable = new Table(newColumn, newType); resultTable.numRows = table.getNumRows();
        if (Arrays.asList(table.getColNames()).contains(columns[1])) {
            int indexOfColumn2 = Arrays.asList(table.getColNames()).indexOf(columns[1]);
            if (table.getColTypes()[index].equals("float")
                    || table.getColTypes()[indexOfColumn2].equals("float")) {
                newType[0] = "float";
            }
        } else {
            Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
            if (table.getColTypes()[index].equals("float") || tempVal.value instanceof Float) {
                newType[0] = "float";
            }
        }
        if (operator.equals("*")) {
            if (Arrays.asList(table.getColNames()).contains(columns[1])) {
                Object result = table.mulcol((column) table.get(columns[0]),
                        (column) table.get(columns[1]));
                if (result instanceof String) {
                    return "ERROR: Invalid types";
                }
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), result);
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                if (table.multiply((column) table.get(columns[0]), tempVal) instanceof String) {
                    return "ERROR: Invalid Operation";
                }
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]),
                        table.multiply((column) table.get(columns[0]), tempVal));
            }
        } else if (operator.equals("-")) {
            if (Arrays.asList(table.getColNames()).contains(columns[1])) {
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]),
                        table.mincol((column) table.get(columns[0]),
                                (column) table.get(columns[1])));
                if (table.mincol((column) table.get(columns[0]),
                        (column) table.get(columns[1])) instanceof String) {
                    return "ERROR: Invalid Operation";
                }
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]),
                        table.minus((column) table.get(columns[0]), tempVal));
                if (table.minus((column) table.get(columns[0]), tempVal) instanceof String) {
                    return "ERROR: Invalid Operation";
                }
            }
        } else if (operator.equals("+")) {
            if (Arrays.asList(table.getColNames()).contains(columns[1])) {
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]),
                        table.addcol((column) table.get(columns[0]),
                                (column) table.get(columns[1])));
                if (table.addcol((column) table.get(columns[0]),
                        (column) table.get(columns[1])) instanceof String) {
                    return "ERROR: Invalid Operation";
                }
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]),
                        table.add((column) table.get(columns[0]), tempVal));
                if (table.add((column) table.get(columns[0]), tempVal) instanceof String) {
                    return "ERROR: Invalid Operation";
                }
            }
        } else if (operator.equals("/")) {
            if (Arrays.asList(table.getColNames()).contains(columns[1])) {
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]),
                        table.divcol((column) table.get(columns[0]),
                                (column) table.get(columns[1])));
                if (table.divcol((column) table.get(columns[0]),
                        (column) table.get(columns[1])) instanceof String) {
                    return "ERROR: Invalid Operation";
                }
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]),
                        table.divide((column) table.get(columns[0]), tempVal));
                if (table.divide((column) table.get(columns[0]), tempVal) instanceof String) {
                    return "ERROR: Invalid Operation";
                }
            }
        }
        db.allTables.put(columns[2], resultTable); return resultTable;
    }

}
