package org.hl7.codex.rems.script;

public class Status {
    public String Code;
    public String DescriptionCode;
    public String Description;
    public String PrescriptionDeliveryMethod;

    public Status() {
        this.Code = null;
        this.DescriptionCode = null;
        this.Description = null;
        this.PrescriptionDeliveryMethod = null;
    }

    public Status(String code, String descriptionCode, String description, String prescriptionDeliveryMethod) {
        this.Code = code;
        this.DescriptionCode = descriptionCode;
        this.Description = description;
        this.PrescriptionDeliveryMethod = prescriptionDeliveryMethod;
    }
}
