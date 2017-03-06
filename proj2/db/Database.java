package db;


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
        String newQuery = query.replaceAll("\\s+"," ");
        return this.eval(newQuery);
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
            System.err.printf("ERROR: Malformed query: %s\n", query);
            return "ERROR: Malformed query";
        }
    }


    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("ERROR: Malformed create: %s\n", expr);
            return "ERROR: Malformed Create";
        }
    }

    private String createNewTable(String name, String[] cols) {
        if (cols.length == 0) {
            System.err.println("ERROR: No Columns");
            return "ERROR: No Columns";
        }
        if (allTables.containsKey(name)) {
            System.err.println("ERROR: No Columns");
            return "ERROR: This table already exists";
        }

        String[] colnames = new String[cols.length];
        String[] coltypes = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            String delims = "[ ,]";
            String[] splitColType = cols[i].split(delims);
            if (!(splitColType[1].equals("int") ||
                    splitColType[1].equals("string") || splitColType[1].equals("float"))) {
                System.err.println("ERROR: This is an incorrect type.");
                return "ERROR: This is an incorrect type.";
            }
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
                String[] row = nextLine.split(",");
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
            System.err.println("ERROR: Malformed Table" + e);
            return "ERROR: Malformed Table";
        } catch (IndexOutOfBoundsException e){
            System.err.println("ERROR: Malformed Table" + e);
            return "ERROR: Malformed Table";
        } catch (IOException e) {
            System.err.println("ERROR: Malformed Table" + e);
            return "ERROR: Malformed Table";
        } catch (NullPointerException e) {
            System.err.println("ERROR: Malformed Table" + e);
            return "ERROR: Malformed Table";
        }
        return "";
    }

        //helper method to convertTypes
    public static Value convertValue(String item, String type) {
        if (item.equals("NOVALUE")) {
            return new Value();
        }
        if (type.equals("int")) {
            return new Value(Integer.parseInt(item));
        } else if (type.equals("float")) {
            return new Value(Float.parseFloat(item));
        } else {
            return new Value(item);
        }
    }

    private String storeTable(String name) {
        if(allTables.containsKey(name)) {
            allTables.get(name).Store(name);
            return "";
        }
        System.err.println("ERROR: Table does not exist");
        return "ERROR: Table does not exist";
    }

    private String dropTable(String name) {
        if (!allTables.containsKey(name)) {
            System.out.println("ERROR: This table does not exist");
            return "ERROR: This table does not exist";
        }
        allTables.remove(name);
        return "";
    }

    private String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("ERROR: Malformed insert: %s\n", expr);
            return "ERROR: Malformed insert";
        }
        Table selectedTable = allTables.get(m.group(1));
        String[] rowArray = m.group(2).split(",");
        for(int i = 0; i < rowArray.length; i++) {
            rowArray[i] = rowArray[i].trim();
        }
        Value[] returnArray = new Value[rowArray.length];
        for (int i = 0; i < rowArray.length; i++) {
            if (selectedTable.columntypes[i] == "int") {
                try {
                    Integer.parseInt(rowArray[i]);
                } catch (NumberFormatException e) {
                    System.err.printf("ERROR: Malformed insert: %s\n", e);
                    return "ERROR: Malformed insert";
                }
                catch (NullPointerException e) {
                    System.err.printf("ERROR: Malformed insert: %s\n", e);
                    return "ERROR: Malformed insert";
                }

            } else if (selectedTable.columntypes[i] == "float") {
                try {
                    Float.parseFloat(rowArray[i]);
                } catch (NumberFormatException e) {
                    System.err.printf("ERROR: Malformed insert: %s\n", e);
                    return "ERROR: Malformed insert";
                } catch (NullPointerException e) {
                    System.err.printf("ERROR: Malformed insert: %s\n", e);
                    return "ERROR: Malformed insert";
                }
            } else {
                try {
                    char[] test = new char[]{'a', 'b', 'c', 'd', 'e', 'f'};
                    rowArray[i].getChars(0, 0, test, 0);
                } catch (NumberFormatException e) {
                    System.err.printf("ERROR: Malformed insert: %s\n", e);
                    return "ERROR: Malformed insert";
                } catch (NullPointerException e) {
                    System.err.printf("ERROR: Malformed insert: %s\n", e);
                    return "ERROR: Malformed insert";
                }
            }
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
            String[] copyTables = new String[tables.length + 1];
            System.arraycopy(tables, 0, copyTables, 1, tables.length);
            joinMultipleTables(tables);
            copyTables[0] = "joinedtemp";
            tables = copyTables;
        }
        Table copyTable = (Table) allTables.get(tables[0]);
        column[] copyColumns = new column[columns.length];
        String[] newType = new String[columns.length];
        for(int i = 0; i < columns.length; i++) {
           String[] colNam = copyTable.columnnames;
           int index = Arrays.asList(colNam).indexOf(columns[i]);
           newType[i] = copyTable.columntypes[index];
        }
        Table newTable = new Table(columns, newType);
        for(int i = 0; i < columns.length;i++) {
            if(copyTable.containsKey(columns[i])) {
                newTable.put(columns[i], copyTable.get(columns[i]));
            }
        }
        newTable.numRows = copyTable.numRows;
        return newTable;
    }

    public Table joinMultipleTables(String[] tables) {
        //tables = tables.split(",");
        int i = tables.length;
        if (i ==1) {
            allTables.put("joinedtemp", allTables.get(tables[0]));
            return allTables.get("joinedtemp");
        }
       if (i == 2) {
            allTables.put("joinedtemp", Table.join((Table) allTables.get(tables[0]),(Table) allTables.get(tables[1])));
            return allTables.get("joinedtemp");
       } else {
            Table[] joinedTables = new Table[i-1];
            joinedTables[0] = Table.join((Table) allTables.get(tables[0]),(Table) allTables.get(tables[1]));
            for(int j = 1; j < joinedTables.length; j+= 1) {
                joinedTables[j] = Table.join(joinedTables[j-1], allTables.get(tables[j+1]));
            }
            allTables.put("joinedtemp",joinedTables[joinedTables.length-1]);
            return allTables.get("joinedtemp");
        }
    }

    public Table joinMultipleTables(Table[] tables) {
        int i = tables.length;
        if (i ==1) {
            return tables[0];
        }
        if (i == 2) {
            return Table.join(tables[0], tables[1]);
        } else {
            Table[] joinedTables = new Table[i-1];
            joinedTables[0] = Table.join(tables[0], tables[1]);
            for(int j = 1; j < joinedTables.length; j+= 1) {
                joinedTables[j] = Table.join(joinedTables[j-1], tables[j+1]);
            }
            allTables.put("joinedtempfromtables",joinedTables[joinedTables.length-1]);
            return allTables.get("joinedtempfromtables");
        }
    }
    public Table select(String[] columns, Table table) {
        Table newTable = table;
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
        newTable.numRows = table.numRows;
        return newTable;
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
        int index;
        if (columns[0].contains(",")) {
            index = Arrays.asList(table.columnnames).indexOf(columns[0].split(", ")[0]);
        } else {
            index = Arrays.asList(table.columnnames).indexOf(columns[0]);
        }
        String[] newColumn = {columns[2]};
        String[] newType = {table.columntypes[index]};
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
    private Table selectConditional(String[] columns, String[] tables, String conditional, String[] comparisons) {
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
        Table table = allTables.get(tables[0]);
        String[] newColumn;
        String[] newType;
        if (columns[0].equals("*")) {
            newColumn = table.columnnames;
            newType = table.columntypes;
        } else {
            newColumn = columns;
            newType = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                newType[i] = table.columntypes[Arrays.asList(table.columnnames).indexOf(columns[i])];
            }
        }
        Table resultTable = new Table(newColumn, newType);
        Value[] tempForAddRow = new Value[newColumn.length];
        if (conditional.equals(">")) {
            for (int j = 0; j < table.numRows; j++) {
                if (Arrays.asList(table.columnnames).contains(comparisons[1])) {
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
            for (int j = 0; j < table.numRows; j++) {
                if (Arrays.asList(table.columnnames).contains(comparisons[1])) {
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
            for (int j = 0; j < table.numRows; j++) {
                if (Arrays.asList(table.columnnames).contains(comparisons[1])) {
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
            for (int j = 0; j < table.numRows; j++) {
                if (Arrays.asList(table.columnnames).contains(comparisons[1])) {
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
            for (int j = 0; j < table.numRows; j++) {
                if (Arrays.asList(table.columnnames).contains(comparisons[1])) {
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
            for (int j = 0; j < table.numRows; j++) {
                if (Arrays.asList(table.columnnames).contains(comparisons[1])) {
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
        allTables.put("table" + tables[0] + conditional, resultTable);
        return resultTable;
    }

    private Table select(String columns, String tables, String conditionals) {
        /*Check if select statement contains operators */
        if (columns.toCharArray()[0] == '*' && conditionals == null) {
            String[] tableNames = tables.split(",");
            return joinMultipleTables(tableNames);
        }
        String[] columnNames = columns.split(",");
        for(int i = 0; i < columnNames.length; i++) {
            columnNames[i].trim();
        }
        String[] tableNames = tables.split(",");
        if (conditionals != null) {
            String[] conditionalPhrases = conditionals.split(AND);
            Table[] conditionalTables = new Table[conditionalPhrases.length];
            for (int i = 0; i < conditionalPhrases.length; i++) {
                String cond = conditionalPhrases[i].split(" ")[1];
                String[] conditionalNames = conditionalPhrases[i].split(" > | < | == | <= | >= | != ");
                conditionalTables[i] = selectConditional(columnNames, tableNames, cond, conditionalNames);
            }
            return select(columnNames, joinMultipleTables(conditionalTables));
        }
        /* will we have multiple expression statments????
        String[] expressionTables = new String[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].contains("as")) {

            }
        }
        */
        if (columns.contains("+")) {
            columnNames = columns.split("\\+");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            tableNames = tables.split(", ");
            return select(columnNames, tableNames, "+");
        } else if (columns.contains("-")) {
            columnNames = columns.split("-");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            tableNames = tables.split(", ");
            return select(columnNames, tableNames, "-");
        } else if (columns.contains("*")) {
            columnNames = columns.split("\\*");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            tableNames = tables.split(", ");
            return select(columnNames, tableNames, "*");
        } else if (columns.contains("/")) {
            columnNames = columns.split("/");
            String[] afterOperator = columnNames[1].split(" as ");
            columnNames[1] = afterOperator[0];
            String[] copyTemp = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, copyTemp, 0, 2);
            columnNames = copyTemp;
            columnNames[2] = afterOperator[1];
            tableNames = tables.split(", ");
            return select(columnNames, tableNames, "/");
        }
        return select(columnNames, tableNames);
    }


    private String printTable(String name) {
        /*if (!allTables.containsKey(name)) {
            System.err.println("Table does not exist");
        }
        return allTables.get(name).printTable();*/
        try {
            allTables.get(name);
            return allTables.get(name).printTable();
        } catch (NullPointerException e) {
            System.err.println("ERROR: No Such table");
            return "ERROR: No Such table";
        }
    }

    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("ERROR: Malformed select: %s\n", expr);
            return "ERROR: Malformed Select";
        }
        String returnString = select(m.group(1), m.group(2), m.group(3)).toString();
        System.out.println(returnString);
        return returnString;
    }

}


