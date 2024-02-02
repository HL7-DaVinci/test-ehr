package org.hl7.codex.rems.script;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Header {
    private Tag to; 
    private Tag from; 
    private String sentTime;
    private String messageId;
    private String relatesToMessageId;
    private String prescriberOrderNumber;

    public Header() {
        this.to = null;
        this.from = null;
        this.sentTime = null;
        this.messageId = null;
        this.relatesToMessageId = null;
        this.prescriberOrderNumber = null;
    }

    public Header(String to, String from, String relatesToMessageId, 
            String prescriberOrderNumber) {
        this.to = new Tag("P", to);
        this.from = new Tag("C", from);

        LocalDateTime now = LocalDateTime.now();
        this.sentTime = now.toString();

        DateTimeFormatter nowFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");
        String nowFormatted = now.format(nowFormatter);
        this.messageId = nowFormatted;

        this.relatesToMessageId = relatesToMessageId;
        this.prescriberOrderNumber = prescriberOrderNumber;
    }

    @XmlElement(name="To")
    public Tag getTo() {
        return to;
    }
    public void setTo(Tag to) {
        this.to = to;
    }

    @XmlElement(name="From")
    public Tag getFrom() {
        return from;
    }
    public void setFrom(Tag from) {
        this.from = from;
    }

    @XmlElement(name="SentTime")
    public String getSentTime() {
        return sentTime;
    }
    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }
    
    @XmlElement(name="MessageID")
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    @XmlElement(name="RelatesToMessageID")
    public String getRelatesToMessageId() {
        return relatesToMessageId;
    }
    public void setRelatesToMessageId(String relatesToMessageId) {
        this.relatesToMessageId = relatesToMessageId;
    }

    @XmlElement(name="PrescriberOrderNumber")
    public String getPrescriberOrderNumber() {
        return prescriberOrderNumber;
    }
    public void setPrescriberOrderNumber(String prescriberOrderNumber) {
        this.prescriberOrderNumber = prescriberOrderNumber;
    }
}
