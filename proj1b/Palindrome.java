public class Palindrome {

    public static Deque<Character> wordToDeque(String word) {
        ArrayDequeSolution<Character> list = new ArrayDequeSolution<>();
        for (int i = 0; i < word.length(); i++) {
            list.addLast(word.charAt(i));
        }
        return list;
    }

    public static boolean isPalindrome(String word) {
        Deque<Character> list = wordToDeque(word);
        if (word.length() <= 1) {
            return true;
        }
        else if (list.removeFirst() == list.removeLast()) {
            String newList = "";
            for (int i = 0; i < list.size(); i++) {
                newList += list.get(i);
            }
            return isPalindrome(newList);
        }
        else {
            return false;
        }
    }

    public static boolean isPalindrome(String word, CharacterComparator cc) {
        Deque<Character> list = wordToDeque(word);
        if (word.length() <= 1) {
            return true;
        }
        else if (cc.equalChars(list.removeFirst(), list.removeLast())) {
            String newList = "";
            for (int i = 0; i < list.size(); i++) {
                newList += list.get(i);
            }
            return isPalindrome(newList, cc);
        }
        else {
            return false;
        }
    }
}
