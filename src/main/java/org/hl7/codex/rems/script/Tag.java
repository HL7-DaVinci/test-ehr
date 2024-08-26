package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Tag {
    @XmlAttribute(name="Qualifier")
    public String qualifier;

    @XmlValue
    public String value;

    public Tag() {
        this.qualifier = null;
        this.value = null;
    }

    public Tag(String qualifier, String value) {
        this.qualifier = qualifier;
        this.value = value;
    }
}
