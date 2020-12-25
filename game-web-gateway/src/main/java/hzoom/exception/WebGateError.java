package hzoom.exception;

public enum  WebGateError {
    UNKNOWN(-2, "网关服务器未知道异常"),
    ;
    private int errorCode;
    private String errorDesc;



    private WebGateError(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorDesc() {
        return this.errorDesc;
    }

    @Override
    public String toString() {
        return "errorCode:" + errorCode + "; errorMsg:" + this.errorDesc;
    }
}
