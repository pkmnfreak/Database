import static org.junit.Assert.*;
import org.junit.Test;
public class TestArrayDeque1B {

    @Test
    public void StudentArrayDequeTest() {
        StudentArrayDeque<Integer> S = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> A = new ArrayDequeSolution<>();
        int counter = StdRandom.uniform(10);
        OperationSequence error = new OperationSequence();
        for (int i = 0; i < counter; i++) {
            int random = StdRandom.uniform(10);
            S.addFirst(random);
            A.addFirst(random);
            error.addOperation(new DequeOperation("addFirst", random));
        }
        for (int i = 0; i < counter; i++) {
            int random = StdRandom.uniform(10);
            S.addLast(StdRandom.uniform(10));
            A.addLast(StdRandom.uniform(10));
            error.addOperation(new DequeOperation("addLast", random));
            /* Can use assertEquals(error.toString(), A.get(A.size() - 1), S.get(S.size() - 1)); */

        }
        for (int i = 0; i < StdRandom.uniform(0, counter); i++) {
            error.addOperation(new DequeOperation("removeFirst"));
            assertEquals(error.toString(), A.removeFirst(), S.removeFirst());
        }
        for (int i = 0; i < StdRandom.uniform(0, counter); i++) {
            error.addOperation(new DequeOperation("removeLast"));
            assertEquals(error.toString(), A.removeLast(), S.removeLast());
        }
    }
}