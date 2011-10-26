package jdojo.oktober2011;


public interface SingleSignOnRegistry {
    boolean tokenIsValid(String token);
    String registerNewSession(String userName);
    void endSession(String token);
}
