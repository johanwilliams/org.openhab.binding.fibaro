package org.openhab.binding.fibaro.internal.model.json;

/**
 * Json pojo for the response message from the Fibao API
 *
 * @author Johan Williams - Initial contribution
 */
public class ApiResponse {

    private String id;
    private String jsonrpc;
    private Result result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ApiResponse [id=" + id + ", jsonrpc=" + jsonrpc + ", result=" + result + "]";
    }

}
