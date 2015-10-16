package com.startechup.tools;

/**
 * Thrown to indicate that the parse has problem on parsing objects
 * e.g. casting org.json.JSONArray into int[]
 */
public class ModelParserException extends Exception {
    public ModelParserException() {
    }

    public ModelParserException(String detailMessage) {
        super(detailMessage);
    }

    public ModelParserException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ModelParserException(Throwable throwable) {
        super(throwable);
    }
}
