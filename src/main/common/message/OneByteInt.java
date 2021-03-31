package main.common.message;

/**
 * The type One byte int.
 */
public class OneByteInt {

    private int value;

    /**
     * Instantiates a new One byte int.
     *
     * @param value the value
     */
    public OneByteInt(int value){
        this.value = value;
    }

    /**
     * Set value.
     *
     * @param value the value
     */
    public void setValue(int value){
        this.value = value;
    }

    /**
     * Get value int.
     *
     * @return the int
     */
    public int getValue(){
        return this.value;
    }
}
