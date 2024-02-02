package org.hl7.codex.rems.script;

//import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Message")
//public class Message implements Serializable {
public class Message {
    protected Header header; 
    //protected Body body;

    @XmlElement(name="Header")
        public Header getHeader() {
        return header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }

    /*
    @XmlElement(name="Body")
    public Body getBody() {
        return body;
    }
    public void setBody(Body body) {
        this.body = body;
    }
    */
}
