/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.domainlib;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;

/**
 * new
 * @author hexvaara
 */
public class Kryptoniter
{
    StrongTextEncryptor kryptoniter;
    
    
    public Kryptoniter(String encryption_key)
    {
        kryptoniter = new StrongTextEncryptor();
        kryptoniter.setPassword(encryption_key);
    }
    
    public Token encryptedToken(Token token)
    {
        return new Token(kryptoniter.encrypt(token.toString()));
    }
    public Token decryptedToken(Token token)
    {
        String decryptedToken = "";
        try {
            decryptedToken = kryptoniter.decrypt(token.toString());
            //return new Token(kryptoniter.decrypt(token.toString()));
        } catch (EncryptionOperationNotPossibleException e) {
            return null;
        }
        
        if (decryptedToken.equals("")) return null;
        
        return new Token(decryptedToken);
    }
    public Token noll()
    {
        return null;
    }
}