package com.mashy.mashy;


public class Request {
    Boolean success = null;
    Data shipment;
    String message;
    String error;

    @Override
    public String toString() {
        return "Request{" +
                "success=" + success +
                ", shipment=" + shipment +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    class Data {
        String status;
        String details;

        @Override
        public String toString() {
            return "Data{" +
                    "status='" + status + '\'' +
                    ", details='" + details + '\'' +
                    '}';
        }
    }
}