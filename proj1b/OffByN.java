public class OffByN implements CharacterComparator {

    private int value;

    public OffByN(int N) {
        value = N;
    }

    @Override
    public boolean equalChars(char x, char y) {
        return (x - y == value || x - y == -value);
    }
}
