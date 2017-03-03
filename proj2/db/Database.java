package db;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringJoiner;


public class Database {
    Map<String, Table> allTables;

    public Database() {
        allTables = new HashMap<>();
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
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    //parses input
    public String transact(String query) {
        this.eval(query);
        return " ";
    }

    private void eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
        } else {
            System.err.printf("Malformed query: %s\n", query);
        }
    }


    private void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: %s\n", expr);
        }
    }

    private void createNewTable(String name, String[] cols) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length-1; i++) {
            joiner.add(cols[i]);
        }
        String[] colnames = new String[cols.length];
        String[] coltypes = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
                String delims = "[, ]";
                String[] splitColType = cols[i].split(delims);
                colnames[i] = splitColType[0];
                coltypes[i] = splitColType[1];
        }
        Table newTable = new Table(colnames, coltypes);
        allTables.put(name, newTable);
    }

    private String createSelectedTable(String name, String exprs, String tables, String conds) {
        //allTables.put(name, select(name, tables,conds))
        System.out.printf("You are trying to create a table named %s by selecting these expressions:" +
                " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", name, exprs, tables, conds);
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
            String nextLine;
            //populate table with values, do this several time per row
            while ((nextLine = in.readLine()) != null) {
                String[] row = nextLine.split(delims);
                Value[] returnRow = new Value[row.length];
                for (int i = 0; i < row.length; i++) {
                    returnRow[i] = convertType(row[i], newTable.columntypes[i]);
                }
                newTable.addRow(returnRow);
            }
            allTables.put(name, newTable);
        } catch (Exception e) {
            System.out.println("No file found");
        }
        return "";
        }

        //helper method to convertTypes
    public static Value convertType(String item,String type) {
        if (type.equals("int")){
            return new Value(Integer.parseInt(item));
        } else if (type.equals("int")) {
            return new Value(Float.parseFloat(item));
        } else if (type.equals("int")) {
            return new Value (item);
        } else {
            throw new Error();
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

    private String insertRow(String expr){
        try {
            Matcher m = INSERT_CLS.matcher(expr);
            Table selectedTable = allTables.get(m.group(1));
            String[] rowArray = m.group(2).split(",");
            Value[] returnArray = new Value[rowArray.length];
            for (int i = 0; i < rowArray.length; i++) {
                returnArray[i] = (Value) convertType(rowArray[i], selectedTable.columntypes[i]);
            }
            selectedTable.addRow(returnArray);
        } catch (Exception e) {
            System.out.println("Malformed query");
        }
        return " ";
    }

    public Table select(String[] columns, String[] tables) {
        try {
            String i = tables[0];
        } catch (Exception e) {
            System.out.println("No tables given");
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

    public Table Binaryselect(String[] columns, String[] tables, String operator) {
        try {
            if (tables.length > 1) {
                joinMultipleTables(tables);
                String[] copyTables = new String[tables.length + 1];
                System.arraycopy(tables, 0, copyTables, 1, tables.length);
                copyTables[0] = "joinedtemp";
                tables = copyTables;
            }

        } catch (Exception e) {
            System.out.println("Malformed query");
        }
        Table table = (Table) allTables.get(tables[0]);
        String[] newColumn = {columns[2]};
        String[] newType = {((column) table.get(columns[0])).get(0).getClass().getName()};
        Table resultTable = new Table(newColumn, newType);
        resultTable.numRows = table.numRows;
        if (operator.equals("*")) {
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


    private String printTable(String name) {
        allTables.get(name).printTable();
        return "";
    }

    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return "";
        }

        select(m.group(1), m.group(2), m.group(3));
        return "";
    }

    private String select(String exprs, String tables, String conds) {
        return " ";
    }

   /* public static void main(String[] args) {
        System.out.print("hi");
    }*/


}


