package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlElement;

public class FillStatus {
    private DispensedStatus dispensed;
    private DispensedStatus partiallyDispensed;
    private DispensedStatus notDispensed;
    private DispensedStatus transferred;

    @XmlElement(name="Dispensed")
    public DispensedStatus getDispensed() {
        return dispensed;
    }
    public void setDispensed(DispensedStatus dispensed) {
        this.dispensed = dispensed;
    }

    @XmlElement(name="PartiallyDispensed")
    public DispensedStatus getPartiallyDispensed() {
        return partiallyDispensed;
    }
    public void setPartiallyDispensed(DispensedStatus partiallyDispensed) {
        this.partiallyDispensed = partiallyDispensed;
    }

    @XmlElement(name="NotDispensed")
    public DispensedStatus getNotDispensed() {
        return notDispensed;
    }
    public void setNotDispensed(DispensedStatus notDispensed) {
        this.notDispensed = notDispensed;
    }

    @XmlElement(name="Transferred")
    public DispensedStatus getTransferred() {
        return transferred;
    }
    public void setTransferred(DispensedStatus transferred) {
        this.transferred = transferred;
    }
    
}
