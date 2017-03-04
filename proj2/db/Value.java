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
            } else {
                this.label = x.toString();
                this.value = x;
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
            } catch (NumberFormatException e) {
                return intNoValue;
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

    public String toString() {
        return this.label;
    }

    public boolean equals(Value x) {
        if (!this.getValue().equals(x.getValue())) {
            return false;
        }
        return true;
    }

 }

