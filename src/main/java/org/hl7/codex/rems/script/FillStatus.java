package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlElement;

public class FillStatus {
    private DispensedStatus dispensed;
    private DispensedStatus partiallyDispensed;
    private DispensedStatus notDispensed;
    private DispensedStatus transferred;

    enum DispensedStatusEnum {
        DISPENSED,
        PARTIALLY_DISPENSED,
        NOT_DISPENSED,
        TRANSFERRED,
        UNKNOWN
    }

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

    public DispensedStatusEnum getStatus() {
        if (getDispensed() != null) {
            return DispensedStatusEnum.DISPENSED;
        } else if (getPartiallyDispensed() != null) {
            return DispensedStatusEnum.PARTIALLY_DISPENSED;
        } else if (getNotDispensed() != null) {
            return DispensedStatusEnum.NOT_DISPENSED;
        } else if (getTransferred() != null) {
            return DispensedStatusEnum.TRANSFERRED;
        } else {
            return DispensedStatusEnum.UNKNOWN;
        }
    }
    
}
