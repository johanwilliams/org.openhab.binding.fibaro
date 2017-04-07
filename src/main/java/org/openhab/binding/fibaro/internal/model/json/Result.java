package org.openhab.binding.fibaro.internal.model.json;

/**
 * Json pojo representing the result of a call to the Fibaro API. The result is nested in other json pojos such as the
 * {@link ApiResponse}
 *
 * @author Johan Williams - Initial contribution
 */
public class Result {

    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result [result=" + result + "]";
    }

}
