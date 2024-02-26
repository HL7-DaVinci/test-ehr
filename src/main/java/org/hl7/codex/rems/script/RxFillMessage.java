package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Message")
public class RxFillMessage {
    protected Header header; 
    private RxFillBody body;

    @XmlElement(name="Header")
        public Header getHeader() {
        return header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }

    @XmlElement(name="Body")
    public RxFillBody getBody() {
        return body;
    }
    public void setBody(RxFillBody body) {
        this.body = body;
    }
}
