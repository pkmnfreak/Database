package db;

import com.sun.org.apache.bcel.internal.generic.TargetLostException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;


public class Database {
    HashMap<String, Table> allTables = new HashMap<>();

    public Database() {
        this.allTables = allTables;
    }

    private static final String REST  = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND   = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD   = Pattern.compile("load " + REST),
            STORE_CMD  = Pattern.compile("store " + REST),
            DROP_CMD   = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD  = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*"
            +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");

    //parses input
    public String transact(String query) {
        return this.eval(query);
    }

    private String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
             return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            System.err.printf("Malformed query: %s\n", query);
            return new Error("No command found").toString();
        }
    }


    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            //System.err.printf("Malformed create: %s\n", expr);
            return new Error("Malformed create").toString();
        }
    }

    private String createNewTable(String name, String[] cols) {
        if (cols.length == 0) {
            //System.err.println("No Columns");
            return new Error("No Columns").toString();
        }

        String[] colnames = new String[cols.length];
        String[] coltypes = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            String delims = "[ ,]";
            String[] splitColType = cols[i].split(delims);
            colnames[i] = splitColType[0];
            coltypes[i] = splitColType[1];
        }
        Table newTable = new Table(colnames, coltypes);
        allTables.put(name, newTable);
        return "";
    }

    private String createSelectedTable(String name, String exprs, String tables, String conds) {
        Table newTable = select(exprs, tables, conds);
        allTables.put(name, newTable);
        return "";
    }


    private String loadTable(String name) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(name + ".tbl"));
            String line = in.readLine();
            String delims = "[ ,]";
            String[] header = line.split(delims);
            String[] columnnames = new String[header.length / 2];
            String[] columntypes = new String[header.length / 2];
            for (int i = 0; i < header.length; i++) {
                if (i % 2 == 0) {
                    columnnames[i / 2] = header[i];
                } else {
                    columntypes[i / 2] = header[i];
                }
            }
            //create new table associated with this
            Table newTable = new Table(columnnames, columntypes);
            String nextLine = in.readLine();
            //populate table with values, do this several time per row
            while (nextLine != null && !nextLine.equals("")) {
                String[] row = nextLine.split(delims);
                Value[] returnRow = new Value[row.length];
                for (int i = 0; i < row.length; i++) {
                    returnRow[i] = convertValue(row[i], newTable.columntypes[i]);
                }
                newTable.addRow(returnRow);
                nextLine = in.readLine();
            }
            in.close();
            allTables.put(name, newTable);
        } catch (FileNotFoundException e) {
            System.err.println("Malformed Table" + e);
        } catch (IndexOutOfBoundsException e){
            System.err.println("Malformed Table" + e);
        } catch (IOException e) {
            System.err.println("Malformed Table" + e);
        }
        return "";
    }

        //helper method to convertTypes
    public static Value convertValue(String item, String type) {
        if (type.equals("int")) {
            return new Value(Integer.parseInt(item));
        } else if (type.equals("float")) {
            return new Value(Float.parseFloat(item));
        } else {
            return new Value(item);
        }
    }

    private String storeTable(String name) {
        allTables.get(name).Store(name);
        return "";

    }

    private String dropTable(String name) {
        allTables.remove(name);
        return "";
    }

    private String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return "";
        }
        Table selectedTable = allTables.get(m.group(1));
        String[] rowArray = m.group(2).split(",");
        for(int i = 0; i < rowArray.length; i++) {
            rowArray[i] = rowArray[i].trim();
        }
        Value[] returnArray = new Value[rowArray.length];
        for (int i = 0; i < rowArray.length; i++) {
           returnArray[i] =  convertValue(rowArray[i], selectedTable.columntypes[i]);
        }
        selectedTable.addRow(returnArray);
        return "";
    }


    public Table select(String[] columns, String[] tables) {
        try {
            String i = tables[0];
        } catch (Exception e) {
            System.out.println("No tables given" + e);
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
                System.arraycopy(newTable.columnnames, i + 1, newColumnNames, i,
                        newTable.columnnames.length - i - 1);
                System.arraycopy(newTable.columntypes, 0, newColumnTypes, 0, i);
                System.arraycopy(newTable.columntypes, i + 1, newColumnTypes, i,
                        newTable.columntypes.length - i - 1);
                newTable.columnnames = newColumnNames;
                newTable.columntypes = newColumnTypes;
                newTable.numColumns--;
                i--;
            }
        }
        return newTable;
    }

    public  Table joinMultipleTables(String[] tables) {
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

    public Table select(String[] columns, String[] tables, String operator) {
        try {
            if (tables.length > 1) {
                joinMultipleTables(tables);
                String[] copyTables = new String[tables.length + 1];
                System.arraycopy(tables, 0, copyTables, 1, tables.length);
                copyTables[0] = "joinedtemp";
                tables = copyTables;
            }

        } catch (Exception e) {
            System.out.println("Malformed query" + e);
        }
        Table table = (Table) allTables.get(tables[0]);
        String[] newColumn = {columns[2]};
        String[] newType = {((column) table.get(columns[0])).get(0).getClass().getName()};
        Table resultTable = new Table(newColumn, newType);
        resultTable.numRows = table.numRows;
        if (operator.equals("*")) {
            if (Arrays.asList(table.columnnames).contains(columns[1])) {
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.multiplyColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.multiply((column) table.get(columns[0]), tempVal));
            }
        } else if (operator.equals("-")) {
            if (Arrays.asList(table.columnnames).contains(columns[1])) {
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.minusColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.minus((column) table.get(columns[0]), tempVal));
            }
        } else if (operator.equals("+")) {
            if (Arrays.asList(table.columnnames).contains(columns[1])) {
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.addColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.add((column) table.get(columns[0]), tempVal));
            }
        } else if (operator.equals("/")) {
            if (Arrays.asList(table.columnnames).contains(columns[1])) {
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.divideColumns((column) table.get(columns[0]), (column) table.get(columns[1])));
            } else {
                Value tempVal = ((column) resultTable.get(newColumn[0])).createValue(columns[1]);
                resultTable.replace(newColumn[0], resultTable.get(newColumn[0]), table.divide((column) table.get(columns[0]), tempVal));
            }
        }
        allTables.put(columns[2], resultTable);
        return resultTable;
    }



    /*columns are the columns we want, tables are the tables we are selecting from, conditionals is an array of symbols, and comparisons is an array of
 String arrays of size 2, where the first String is the left side and the second is the right side
 */
    private Table selectConditional(String[] columns, String[] tables, String[] conditionals, String[][] comparisons) {
        try {
            if (tables.length > 1) {
                joinMultipleTables(tables);
                String[] copyTables = new String[tables.length + 1];
                System.arraycopy(tables, 0, copyTables, 1, tables.length);
                copyTables[0] = "joinedtemp";
                tables = copyTables;
            }
        } catch (Exception e) {
            System.out.println("Malformed query oops");
        }
        Table table = (Table) allTables.get(tables[0]);
        String[] newColumn = columns;
        String[] newType = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            newType[i] = ((column) table.get(columns[i])).get(0).getClass().getName();
        }
        Table resultTable = new Table(newColumn, newType);
        resultTable.numRows = table.numRows;
        for (int i = 0; i < conditionals.length; i++) {
            if (conditionals[i].equals(">")) {
                for (String j : columns) {
                    if (Arrays.asList(table.columnnames).contains(comparisons[i][1])) {
                        resultTable.replace(j, resultTable.get(j), table.greaterThanColumns((column) table.get(comparisons[i][0]), (column) table.get(comparisons[i][1])));
                    } else {
                        Value tempVal = ((column) resultTable.get(j)).createValue(comparisons[i][1]);
                        resultTable.replace(j, resultTable.get(j), table.greaterThan((column) table.get(comparisons[i][0]), tempVal));
                    }
                }
            } else if (conditionals[i].equals("<")) {
                for (String j : columns) {
                    if (Arrays.asList(table.columnnames).contains(comparisons[i][1])) {
                        resultTable.replace(j, resultTable.get(j), table.lessThanColumns((column) table.get(comparisons[i][0]), (column) table.get(comparisons[i][1])));
                    } else {
                        Value tempVal = ((column) resultTable.get(j)).createValue(comparisons[i][1]);
                        resultTable.replace(j, resultTable.get(j), table.lessThan((column) table.get(comparisons[i][0]), tempVal));
                    }
                }
            } else if (conditionals[i].equals("==")) {
                for (String j : columns) {
                    if (Arrays.asList(table.columnnames).contains(comparisons[i][1])) {
                        resultTable.replace(j, resultTable.get(j), table.equalToColumns((column) table.get(comparisons[i][0]), (column) table.get(comparisons[i][1])));
                    } else {
                        Value tempVal = ((column) resultTable.get(j)).createValue(comparisons[i][1]);
                        resultTable.replace(j, resultTable.get(j), table.equalTo((column) table.get(comparisons[i][0]), tempVal));
                    }
                }
            } else if (conditionals[i].equals("!=")) {
                for (String j : columns) {
                    if (Arrays.asList(table.columnnames).contains(comparisons[i][1])) {
                        resultTable.replace(j, resultTable.get(j), table.notEqualToColumns((column) table.get(comparisons[i][0]), (column) table.get(comparisons[i][1])));
                    } else {
                        Value tempVal = ((column) resultTable.get(j)).createValue(comparisons[i][1]);
                        resultTable.replace(j, resultTable.get(j), table.notEqualTo((column) table.get(comparisons[i][0]), tempVal));
                    }
                }
            } else if (conditionals[i].equals(">=")) {
                for (String j : columns) {
                    if (Arrays.asList(table.columnnames).contains(comparisons[i][1])) {
                        resultTable.replace(j, resultTable.get(j), table.greaterThanOrEqualToColumns((column) table.get(comparisons[i][0]), (column) table.get(comparisons[i][1])));
                    } else {
                        Value tempVal = ((column) resultTable.get(j)).createValue(comparisons[i][1]);
                        resultTable.replace(j, resultTable.get(j), table.greaterThanOrEqualTo((column) table.get(comparisons[i][0]), tempVal));
                    }
                }
            } else if (conditionals[i].equals("<=")) {
                for (String j : columns) {
                    if (Arrays.asList(table.columnnames).contains(comparisons[i][1])) {
                        resultTable.replace(j, resultTable.get(j), table.lessThanOrEqualToColumns((column) table.get(comparisons[i][0]), (column) table.get(comparisons[i][1])));
                    } else {
                        Value tempVal = ((column) resultTable.get(j)).createValue(comparisons[i][1]);
                        resultTable.replace(j, resultTable.get(j), table.lessThanOrEqualTo((column) table.get(comparisons[i][0]), tempVal));
                    }
                }
            }
        }
        allTables.put("newTableFromBinaryConditionalSelect", resultTable);
        return resultTable;
    }

    private Table select(String columns, String tables, String conditionals) {
        /*Check if select statement contains operators */
        if (columns.toCharArray()[0] == '*') {
            String[] tableNames = tables.split(", ");
            return joinMultipleTables(tableNames);
        }
        if (columns.contains("+")) {
            String[] columnNames = columns.split(" \\+ ");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            String[] tableNames = tables.split(", ");
            return select(columnNames, tableNames, "+");
        } else if (columns.contains("-")) {
            String[] columnNames = columns.split(" \\- ");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            String[] tableNames = tables.split(", ");
            return select(columnNames, tableNames, "-");
        } else if (columns.contains("*")) {
            String[] columnNames = columns.split(" \\* ");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            String[] tableNames = tables.split(", ");
            return select(columnNames, tableNames, "*");
        } else if (columns.contains("/")) {
            String[] columnNames = columns.split(" \\/ ");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            String[] tableNames = tables.split(", ");
            return select(columnNames, tableNames, "/");
        }
        return new Table(null,null);
    }


    private String printTable(String name) {
        if (!allTables.containsKey(name)) {
            System.err.println("no table with that name");
        }
        return allTables.get(name).printTable();
    }

    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
        }
        return select(m.group(1), m.group(2), m.group(3)).toString();
    }


}


