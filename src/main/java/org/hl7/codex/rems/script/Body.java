package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Body")
public class Body {
    private RxFill rxFill; 
    private Status status;
    private Error error;

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

    @XmlElement(name="Error")
    public Error getError() {
        return error;
    }
    public void setError(Error error) {
        this.error = error;
    }

    public Body() {
        this.rxFill = null;
        this.status = null;
        this.error = null;
    }

    public Body(RxFill rxFill) {
        this.rxFill = rxFill;
    }

    public Body(Status status) {
        this.status = status;
    }

    public Body(Error error) {
        this.error = error;
    }
}
