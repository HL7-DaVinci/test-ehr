package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Message")
public class RxFillStatusMessage {
    protected Header header; 
    private RxFillStatusBody body;

    public RxFillStatusMessage() {
        this.header = null;
        this.body = null;
    }

    public RxFillStatusMessage(Header header, RxFillStatusBody body) {
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
    public RxFillStatusBody getBody() {
        return body;
    }
    public void setBody(RxFillStatusBody body) {
        this.body = body;
    }    
}
