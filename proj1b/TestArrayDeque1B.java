import static org.junit.Assert.*;
import org.junit.Test;
public class TestArrayDeque1B {

    @Test
    public void studentArrayDequeTest() {
        StudentArrayDeque<Integer> S = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> A = new ArrayDequeSolution<>();
        int counter = StdRandom.uniform(1, 1000);
        int counter2 = StdRandom.uniform(counter, 1000);
        OperationSequence error = new OperationSequence();
        for (int i = 0; i < counter; i++) {
            int random = StdRandom.uniform(10);
            S.addFirst(random);
            A.addFirst(random);
            error.addOperation(new DequeOperation("addFirst", random));
            assertEquals(A.get(0), S.get(0));
        }
        for (int i = 0; i < counter; i++) {
            int random = StdRandom.uniform(10);
            S.addLast(random);
            A.addLast(random);
            error.addOperation(new DequeOperation("addLast", random));
            assertEquals(error.toString(), A.get(A.size() - 1), S.get(S.size() - 1));
        }
        for (int i = 0; i < StdRandom.uniform(counter2, 1000); i++) {
            error.addOperation(new DequeOperation("removeFirst"));
            assertEquals(error.toString(), A.removeFirst(), S.removeFirst());
        }
        for (int i = 0; i < StdRandom.uniform(counter2, 1000); i++) {
            error.addOperation(new DequeOperation("removeLast"));
            assertEquals(error.toString(), A.removeLast(), S.removeLast());
        }
    }
}
