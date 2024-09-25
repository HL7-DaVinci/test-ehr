package org.hl7.codex.rems.script;

public class Error {
    public String Code;
    public String DescriptionCode;
    public String Description;

    public Error() {
        this.Code = null;
        this.DescriptionCode = null;
        this.Description = null;
    }

    public Error(String code, String descriptionCode, String description) {
        this.Code = code;
        this.DescriptionCode = descriptionCode;
        this.Description = description;
    }
}
