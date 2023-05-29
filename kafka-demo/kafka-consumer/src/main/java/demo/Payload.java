package demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {

    private String id;
    private String data;

    public Payload() {
    }
    
    @JsonCreator
    public Payload(@JsonProperty("id") String id,
                    @JsonProperty("data") String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }


    public String getData() {
        return data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("id: '%s', data: '%s'", id, data);
    }
}