package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlElement;

public class RxFillStatusBody {
    private Status status;

    @XmlElement(name="Status")
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public RxFillStatusBody() {
        this.status = null;
    }

    public RxFillStatusBody(Status status) {
        this.status = status;
    }
}
