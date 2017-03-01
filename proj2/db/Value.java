package db;

/**
 * Created by pkmnfreak on 2/27/17.
 */
public class Value<Item>{
    public String label;
    public Object value;
    public String stringNoValue = "";
    public int intNoValue = 0;
    public float floatNoValue = 0.0f;

    public Value() {
        this.label = "NOVALUE";
    }

    public Value(Item x) {
        if (x instanceof Float) {
            if (((Float) x).isNaN()) {
                this.label = "NaN";
            }
        } else {
            this.label = x.toString();
            this.value = x;

        }
    }

    public Object getValue() {
        if (this.label.equals("NOVALUE")) {
            try {
                Integer.parseInt(this.value.toString());
                return intNoValue;
            } catch (NumberFormatException e) {
            }
            try {
                Float.parseFloat(this.value.toString());
                return floatNoValue;
            } catch(NumberFormatException e) {
                return stringNoValue;
            }
        }
        if (this.label.equals("NaN")) {
            return Float.NaN;
        }
        return this.value;
    }
<<<<<<< HEAD:proj2/Value.java
}

/*getclass().getname()*/
=======

    public String toString() {
        return this.value.toString();
    }
 }
>>>>>>> 758f5e2a16dfa2f743ca2e59de78a6db42b65bad:proj2/db/Value.java
