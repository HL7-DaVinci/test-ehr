package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlElement;

public class RxFillBody {
    private RxFill rxFill;

    @XmlElement(name="RxFill")
    public RxFill getRxFill() {
        return rxFill;
    }
    public void setRxFill(RxFill rxFill) {
        this.rxFill = rxFill;
    }
}
