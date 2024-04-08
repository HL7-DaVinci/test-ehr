package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Message")
public class Message {
    protected Header header; 

    @XmlElement(name="Header")
        public Header getHeader() {
        return header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }
}
