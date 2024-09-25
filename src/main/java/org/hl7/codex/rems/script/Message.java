package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Message")
public class Message {
    private Header header; 
    private Body body; 

    public Message() {
        this.header = null;
        this.body = null;
    }

    public Message(Header header, Body body) {
        this.header = header;
        this.body = body;
    }

    @XmlElement(name="Header")
        public Header getHeader() {
        return header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }

    @XmlElement(name="Body")
        public Body getBody() {
        return body;
    }
    public void setBody(Body body) {
        this.body = body;
    }
}
