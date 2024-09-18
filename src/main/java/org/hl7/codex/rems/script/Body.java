package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Body")
public class Body {
    private RxFill rxFill; 
    private Status status;

    @XmlElement(name="RxFill")
    public RxFill getRxFill() {
        return rxFill;
    }
    public void setRxFill(RxFill rxFill) {
        this.rxFill = rxFill;
    }

    @XmlElement(name="Status")
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public Body() {
        this.status = null;
    }

    public Body(Status status) {
        this.status = status;
    }
}
