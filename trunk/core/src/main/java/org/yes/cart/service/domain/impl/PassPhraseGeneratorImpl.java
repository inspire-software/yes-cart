package org.yes.cart.service.domain.impl;

import org.yes.cart.service.domain.PassPhrazeGenerator;

/**
 * (C)opypasted from some internet resource, sorry, dont remember.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PassPhraseGeneratorImpl implements PassPhrazeGenerator {

    /**
     * Minimum length for a decent password
     */
    private int passwordLenght = 8;

    /**
     * The random number generator.
     */
    protected static java.util.Random rand = new java.util.Random();


    /*
    * Set of characters that is valid. Must be printable, memorable, and "won't
    * break HTML" (i.e., not ' <', '>', '&', '=', ...). or break shell commands
    * (i.e., not ' <', '>', '$', '!', ...). I, L and O are good to leave out,
    * as are numeric zero and one.
    */
    protected static char[] goodChar = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '@',};


    /**
     * Create password generator
     *
     * @param passwordLenght the length of generated password.
     */
    public PassPhraseGeneratorImpl(final int passwordLenght) {
        this.passwordLenght = passwordLenght;
    }

    /**
     * Set the length of generated password.
     *
     * @param passwordLenght the length of generated password.
     */
    public void setPasswordLenght(final int passwordLenght) {
        this.passwordLenght = passwordLenght;
    }

    /**
     * Generate a random pass phraze string with a random password.
     *
     * @return generated pass phraze
     */
    public String getNextPassPhrase() {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < passwordLenght; i++) {
            buffer.append(goodChar[rand.nextInt(goodChar.length)]);
        }
        return buffer.toString();
    }

}
