
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
       Character[] simColumn = findSimColumnNames(x,y);
       for (Object k: joinedTable.keySet()) {
           //handles the shared key set
           column temp = (column) x.get(joinIndices.get(0));
           for(int i = 0; i < temp.size() ;i++) {
               int temp2 = (int) temp.get(i);
               column temp3 = (column) x.get(k);
               joinedTable.put(k, temp3.get(temp2));
           }
           //handles the non-shared
       }
       return joinedTable;
    }

    /** helper method to find similar columns **/
    private static Stack findSimColumnNames(Table x, Table y) {
        /** solution borrowed from http://stackoverflow.com/questions/23562308/java-find-matching-keys-of-two-hashmaps
         * Sets remove duplicates automatically which is why it's used here **/

    }

    /** helper method, this will become the column names for joined table**/
    private static Character[] jointColumnNames(Table x, Table y) {
        /** Character[] array1and2 = new Character[x.columntypes.length + y.columntypes.length];
        arraycopy(x.columntypes, 0, array1and2, 0, x.columntypes.length);
        arraycopy(y.columntypes, 0, array1and2, x.columntypes.length, y.columntypes.length);
        Set<Character> returnSet = new HashSet<Character> (Arrays.asList(array1and2));
        Character[] returnArray = returnSet.toArray(new Character[returnSet.size()]);
        return returnArray;**/
        Set<Character>  simColumnNames = findSimColumnNames (x,y);
        Character[] returnArray = new Character[x.columnnames.length + y.columnnames.length - simColumnNames.size()];
        for (int i = 0; i < x.columntypes.length; i++) {
            returnArray[i] = x.columnnames[i];
        }
        for (int j = 0; j < y.columnnames.length; j++) {
            if (!simColumnNames.contains(y.columnnames[j])) {
                returnArray[j + x.columnnames.length - 1] = y.columnnames[j];
            }
        }
        return returnArray;
    }

    /** this will be put into the Table constructor in the join method**/
    private static String[] jointColumnTypes(Table x, Table y) {
        Character[] jointColumnNames = jointColumnNames(x,y);
        Set<Character>  simColumnNames = findSimColumnNames (x,y);
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
        Set<Character> commonKeys = findSimColumnNames(x,y);
        ArrayList xindex = new ArrayList();
        ArrayList yindex = new ArrayList();
        for (Character key: commonKeys) {
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


    public static void main(String[] args) {
        Character[] x = {'x','y'};
        String[] n = {"int", "int"};
        Table T1 = new Table(x,n);
        int [] firstrow = {1,4};
        T1.addRow(firstrow);
        int[] secondrow = {2,5};
        T1.addRow(secondrow);
        int[] thirdrow = {3,6};
        T1.addRow(thirdrow);
        T1.printTable();

        Character[] l = {'x','z'};
        Table T2 = new Table(l,n);
        int[] frow = {1,7};
        T2.addRow(frow);
        int[] srow = {7,7};
        T2.addRow(srow);
        int[] trow = {1,9};
        T2.addRow(trow);
        int[] forow = {1,11};
        T2.addRow(forow);

        T2.printTable();
        System.out.println(findSimItemsinColumn(T1,T2));
    }
}
