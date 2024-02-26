package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlElement;

public class RxFill {
    private FillStatus fillStatus;

    @XmlElement(name="FillStatus")
    public FillStatus getFillStatus() {
        return fillStatus;
    }
    public void setFillStatus(FillStatus fillStatus) {
        this.fillStatus = fillStatus;
    }
}
