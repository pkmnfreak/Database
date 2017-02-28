package examples;

/**
 * Created by pkmnfreak on 2/17/17.
 */
public class Table {

    public int size = 0;
    public int rows;
    public int columns;
    public int[][] table;

    public void Table(int rows, int columns) {
        this.table = new int[rows][columns];
        this.rows = rows;
        this.columns = columns;
        this.size += 1;
    }

    public void addRow() {
        if (size + 1 > rows) {
            int[][] newTable = new int[rows + 1][columns];
            System.arraycopy(table, 0, newTable, 0, rows);
            this.table = newTable;
        }
        this.table = new int[rows + 1][columns];
        this.rows += 1;
    }


}
