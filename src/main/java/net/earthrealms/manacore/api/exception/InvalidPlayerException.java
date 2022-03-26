package net.earthrealms.manacore.api.exception;

public class InvalidPlayerException extends Exception { 
    public InvalidPlayerException(String errorMessage) {
        super(errorMessage);
    }
}
