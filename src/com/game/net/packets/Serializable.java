package com.game.net.packets;

import java.nio.ByteBuffer;

public interface Serializable<S extends Serializable> {
    byte[] toByteCode();

    S fromByteCode(ByteBuffer data) throws InvalidFormatException;
    int getNumberOfBytes();

    class InvalidFormatException extends Exception {
        public InvalidFormatException(Serializable target, String provided) {
            super(String.format("Invalid format for %s : %d/%d:'%s'", target.getClass().getCanonicalName(), provided.length(),target.getNumberOfBytes(), provided));
        }
    }

}
