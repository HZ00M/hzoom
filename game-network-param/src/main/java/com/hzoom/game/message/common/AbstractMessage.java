package com.hzoom.game.message.common;

public abstract class AbstractMessage implements IMessage {
    private Header header;
    private byte[] body;

    public AbstractMessage() {
        MessageMetadata metadata = this.getClass().getAnnotation(MessageMetadata.class);
        if (metadata == null) {
            throw new IllegalArgumentException("消息没有添加元数据注解:" + this.getClass());
        }
        header = new Header(metadata);
    }

    protected abstract byte[] encode();

    protected abstract void decode(byte[] body);

    protected abstract boolean isNullBody();

    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public void setHeader(Header messageHeader) {
        this.header = messageHeader;
    }

    @Override
    public MessageType getMessageType(){
        return header.getMessageType();
    }

    @Override
    public void read(byte[] body) {
        this.body = body;
        if (body != null) {// 如果body不为null，才反序列化，这样不用考虑为null的情况，防止忘记判断。
            this.decode(body);
        }
    }

    @Override
    public byte[] body() {
        if (body == null){
            if (!isNullBody()){// 如果内容不为null，再去序列化，这样子类实现的时候，不需要考虑null的问题了。
                body = encode();
                if (body==null){// 检测是否返回的空，防止开发者默认返回null
                    throw new IllegalArgumentException("消息序列化之后的值为null:" + this.getClass().getName());
                }
            }
        }
        return body;
    }
}
