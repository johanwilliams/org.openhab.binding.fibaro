package org.openhab.binding.fibaro.internal.model.json;

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
