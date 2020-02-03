package com.hw.provider.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * author：pc-20171125
 * data:2020/1/12 18:24
 */
@Entity
public class ChatBeanLastMessage {
    //TODO:消息的id，应该设置自增加, @Id(autoincrement = true)
    @Id
    public Long id;

    /**
     * 显示在列表中的消息类型
     * 对应ChatMultipleItem
     */
    public int messageType;

    //发送人的名称
    public String name;

    //消息内容
    public String textContent;

    //时间
    public Date time;

    //true：发出去的消息
    //false：接收到的消息
    public boolean isSend;

    //true:已读
    //false：未读
    public boolean isRead;

    //true：群聊消息
    //false：点对点消息
    public boolean isGroup;

    //会话Id
    public String conversationId;

    //聊天界面顶部显示的名称
    public String conversationUserName;

    public ChatBeanLastMessage(Builder builder) {
        this.messageType = builder.messageType;
        this.name = builder.name;
        this.textContent = builder.content;
        this.time = builder.sendDate;
        this.isSend = builder.isSend;
        this.isRead = builder.isRead;
        this.isGroup = builder.isGroup;
        this.conversationId = builder.conversationId;
        this.conversationUserName = builder.conversationUserName;
    }

    @Generated(hash = 1528011727)
    public ChatBeanLastMessage(
            Long id,
            int messageType,
            String name,
            String textContent,
            Date time,
            boolean isSend,
            boolean isRead,
            boolean isGroup,
            String conversationId,
            String conversationUserName) {
        this.id = id;
        this.messageType = messageType;
        this.name = name;
        this.textContent = textContent;
        this.time = time;
        this.isSend = isSend;
        this.isRead = isRead;
        this.isGroup = isGroup;
        this.conversationId = conversationId;
        this.conversationUserName = conversationUserName;
    }

    @Generated(hash = 1862251999)
    public ChatBeanLastMessage() {
    }

    public static class Builder {
        private int messageType;

        //发送人的名称
        private String name;

        //消息内容
        private String content;

        //时间
        private Date sendDate;

        //true：发出去的消息
        //false：接收到的消息
        private boolean isSend;

        //true:已读
        //false：未读
        private boolean isRead;

        //true：群聊消息
        //false：点对点消息
        private boolean isGroup;

        //会话Id
        private String conversationId;

        //聊天界面顶部显示的名称
        private String conversationUserName;

        public Builder setMessageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setSendDate(Date sendDate) {
            this.sendDate = sendDate;
            return this;
        }

        public Builder setSend(boolean send) {
            isSend = send;
            return this;
        }

        public Builder setRead(boolean read) {
            isRead = read;
            return this;
        }

        public Builder setGroup(boolean group) {
            isGroup = group;
            return this;
        }

        public Builder setConversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder setConversationUserName(String conversationUserName) {
            this.conversationUserName = conversationUserName;
            return this;
        }

        public ChatBeanLastMessage builder() {
            return new ChatBeanLastMessage(this);
        }
    }

    @Override
    public String toString() {
        return "ChatBean{" +
                "id=" + id +
                ", messageType=" + messageType +
                ", name='" + name + '\'' +
                ", textContent='" + textContent + '\'' +
                ", time=" + time +
                ", isSend=" + isSend +
                ", isRead=" + isRead +
                ", isGroup=" + isGroup +
                ", conversationId='" + conversationId + '\'' +
                ", conversationUserName='" + conversationUserName + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextContent() {
        return this.textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean getIsSend() {
        return this.isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean getIsGroup() {
        return this.isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getConversationId() {
        return this.conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationUserName() {
        return this.conversationUserName;
    }

    public void setConversationUserName(String conversationUserName) {
        this.conversationUserName = conversationUserName;
    }
}
