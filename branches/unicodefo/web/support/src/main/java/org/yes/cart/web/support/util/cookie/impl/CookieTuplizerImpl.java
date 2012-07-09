package org.yes.cart.web.support.util.cookie.impl;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.util.cookie.CookieTuplizer;
import org.yes.cart.web.support.util.cookie.UnableToCookielizeObjectException;
import org.yes.cart.web.support.util.cookie.UnableToObjectizeCookieException;
import org.yes.cart.web.support.util.cookie.annotations.PersistentCookie;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.Cookie;
import java.io.*;
import java.security.InvalidKeyException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of cookie tuplizer. Uses annotation
 * {@link PersistentCookie}
 * in order to determine group of Cookies that are needed for assembling object.
 * <p/>
 * The cookies are crypted in base64 form. According to RFC 2109
 * specification cookie storage limit at least 300 cookies at least 4096
 * bytes per cookie, so it allow to store 1228800 bytes aprox 1 Mb.
 * Base64 representation will be splited to chunks.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 2:17:57 PM
 */
public class CookieTuplizerImpl implements CookieTuplizer {

    private static final long serialVersionUID = 20100116L;

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private static final Cookie[] EMPTY_COOKIES = new Cookie[0];

    private static final Map<String, Pattern> PATTERNS_CACHE = new HashMap<String, Pattern>();

    private final Cipher desCipher;
    private final Cipher desUnCipher;

    private final int chunkSize;

    private SecretKey secretKey;


    /**
     * Default Constructor.
     *
     * @param keyRingPassword      key ring password to use.
     * @param chunkSize            Base64 chunk size.
     * @param secretKeyFactoryName Secret Key Factory Name.
     * @param cipherName           Chipher name.
     */
    public CookieTuplizerImpl(final String keyRingPassword, final int chunkSize,
                              final String secretKeyFactoryName, final String cipherName) {

        this.chunkSize = chunkSize;

        try {
            final DESKeySpec desKeySpec = new DESKeySpec(keyRingPassword.getBytes());

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretKeyFactoryName);
            secretKey = keyFactory.generateSecret(desKeySpec);

            // Create Cipher
            desCipher = Cipher.getInstance(cipherName);
            desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // create uncipher
            desUnCipher = Cipher.getInstance(cipherName);

            desUnCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception ike) {
            if (LOG.isErrorEnabled()) {
                LOG.error(ike.getMessage(), ike);
            }
            throw new RuntimeException("Unable to load Cipher for CookieTuplizer", ike);
        }

    }

    /**
     * Split string to chunks by size.
     *
     * @param stringToSplit string to split.
     * @return array of string cunks or null if string is blank.
     */
    String[] split(final String stringToSplit) {
        if (StringUtils.isNotBlank(stringToSplit)) {
            int strLenght = stringToSplit.length();
            int splitNum = strLenght / chunkSize;
            if (strLenght % chunkSize > 0) {
                splitNum += 1;
            }
            String[] result = new String[splitNum];
            for (int i = 0; i < splitNum; i++) {
                int startPos = i * chunkSize; // the substring starts here
                int endPos = startPos + chunkSize; // the substring ends here
                if (endPos > strLenght) { // make sure we don't cause an IndexOutOfBoundsException
                    endPos = strLenght;
                }
                result[i] = stringToSplit.substring(startPos, endPos);
            }
            return result;
        }
        return null;
    }

    /**
     * Creates Cookie objects for given keys.
     *
     * @param oldCookies cookies that have come from request
     * @param values     cookie value to be written
     * @param object     object for which cookies are written
     * @return cookies that represent requested object
     * @throws UnableToCookielizeObjectException
     *          thrown when annotation are not found on the object.
     */
    Cookie[] assembleCookiesForObject(final Cookie[] oldCookies, final String[] values, final Object object) throws UnableToCookielizeObjectException {

        final TuplizerSetting meta = extractFromObject(object);

        if (meta.key == null || meta.expiry == null) {
            final String errMsg = "object of type: " + meta.checkedClasses.toString()
                    + "must be annotated with PersistentCookie";
            if (LOG.isErrorEnabled()) {
                LOG.error(errMsg);
            }
            throw new UnableToCookielizeObjectException(errMsg);
        }

        if (values == null || values.length == 0) {
            return EMPTY_COOKIES;
        }

        final int previousCookieCount = countOldCookies(oldCookies, meta.key);
        final int currentCookieCount = values.length + 1;

        final Cookie[] cookies = new Cookie[Math.max(previousCookieCount, currentCookieCount)];
        for (int index = 0; index < currentCookieCount - 1; index++) {
            cookies[index] = createNewCookie(meta.key + index, values[index], meta.expiry, meta.path);
        }
        final int terminatorIndex = currentCookieCount - 1;
        cookies[terminatorIndex] = createNewCookie(meta.key + (terminatorIndex), String.valueOf(meta.key.hashCode()), meta.expiry, meta.path);


        if (currentCookieCount < previousCookieCount) {
            for (int index = currentCookieCount; index < previousCookieCount; index++) {
                cookies[index] = createNewCookie(meta.key + index, "", 0, meta.path); // overwrite surplus cookies.
            }
        }
        return cookies;
    }

    Pattern getOrCreatePattern(final String key) {
        if (PATTERNS_CACHE.containsKey(key)) {
            return PATTERNS_CACHE.get(key);
        }
        final Pattern pattern = Pattern.compile(key + "(\\d+)");
        PATTERNS_CACHE.put(key, pattern);
        return pattern;
    }

    int countOldCookies(final Cookie[] oldCookies, final String objectKey) {

        if (oldCookies == null || oldCookies.length == 0) {
            return 0;
        }

        final Pattern pattern = getOrCreatePattern(objectKey);

        int maxIndex = -1;
        for (Cookie cookie : oldCookies) {
            if (cookie.getName().startsWith(objectKey)) {
                final Matcher matcher = pattern.matcher(cookie.getName());
                if (matcher.matches()) {
                    final Integer digits = Integer.valueOf(matcher.group(1));
                    maxIndex = Math.max(digits, maxIndex);
                }
            }
        }
        return maxIndex + 1;

    }

    private Cookie createNewCookie(final String name, final String value, final int maxAgeInSeconds, final String path) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath("/");
        cookie.setVersion(1); // allow to have base64 encoded value in cookie
        return cookie;
    }

    /**
     * {@inheritDoc}
     */
    public Cookie[] toCookies(final Cookie[] oldCookies, final Serializable serializable)
            throws UnableToCookielizeObjectException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        synchronized (desCipher) {
            BASE64EncoderStream base64EncoderStream = new BASE64EncoderStream(byteArrayOutputStream, Integer.MAX_VALUE); //will be splited manually
            CipherOutputStream cipherOutputStream = new CipherOutputStream(base64EncoderStream, desCipher);
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(cipherOutputStream);
                objectOutputStream.writeObject(serializable);
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (Throwable ioe) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(
                            MessageFormat.format("Unable to serialize object {0}", serializable),
                            ioe
                    );
                }
                throw new UnableToCookielizeObjectException(ioe);
            } finally {
                try {
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }
                    cipherOutputStream.close();
                    base64EncoderStream.close();
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Can not close stream", e);
                    }
                }
            }
        }
        return assembleCookiesForObject(oldCookies, split(byteArrayOutputStream.toString()), serializable);
    }


    /**
     * Assemble cookies array to string.
     *
     * @param cookies cookies array to assemble the string
     * @param object  the object whose representation we need to extract from cookies
     * @return assembled string
     * @throws UnableToObjectizeCookieException
     *          thrown when annotation {@link PersistentCookie} is not found on
     *          the object
     */
    String assembleStringRespresentationOfObjectFromCookies(final Cookie[] cookies, final Serializable object)
            throws UnableToObjectizeCookieException {

        final TuplizerSetting meta = extractFromObject(object);

        if (meta.key == null) {
            final String errMsg = "current object of types: " + meta.checkedClasses.toString()
                    + " must be annotated with PersistentCookie";
            if (LOG.isErrorEnabled()) {
                LOG.error(errMsg);
            }
            throw new UnableToObjectizeCookieException(errMsg);
        }


        final StringBuilder stringBuilder = new StringBuilder();
        int cookieIndex = 0;
        boolean continueSearch = true;

        if (cookies != null) {

            final String terminator = String.valueOf(meta.key.hashCode());
            final String objectKey = meta.key;

            while (continueSearch) {

                final String seekKey = objectKey + cookieIndex;
                boolean keyFound = false;

                for (Cookie cookie : cookies) {


                    if (cookie.getName().equals(seekKey)) {
                        keyFound = true;
                        continueSearch = !terminator.equals(cookie.getValue());
                        if (continueSearch) {
                            stringBuilder.append(cookie.getValue());
                            cookieIndex++;
                        }
                        break;
                    }
                }

                if (!keyFound) {
                    continueSearch = false;
                }
            }
        }
        return stringBuilder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T toObject(Cookie[] cookies, final T object) throws UnableToObjectizeCookieException {
        final String input = assembleStringRespresentationOfObjectFromCookies(cookies, object);
        if (input.length() == 0) {
            return object;
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        final BASE64DecoderStream base64DecoderStream = new BASE64DecoderStream(byteArrayInputStream);
        final CipherInputStream cipherInputStream = new CipherInputStream(base64DecoderStream, desUnCipher);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(cipherInputStream);
            return (T) objectInputStream.readObject();

        } catch (Exception exception) {
            try {
                desUnCipher.init(Cipher.DECRYPT_MODE, secretKey); //reinit
            } catch (InvalidKeyException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Cant reinit desUnCipher", exception);
                }
            }
            final String errMsg = "Unable to convert bytes assembled from cookies into object";
            if (LOG.isErrorEnabled()) {
                LOG.error(errMsg, exception);
            }
            throw new UnableToObjectizeCookieException(errMsg, exception);
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                cipherInputStream.close();
                base64DecoderStream.close();
                byteArrayInputStream.close();
            } catch (IOException ioe) { // leave this one silent as we have the object on hands.
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to close object stream", ioe);
                }
            }

        }
    }

    /**
     * Extract meta data from object.
     *
     * @param object the object
     * @return meta data
     */
    TuplizerSetting extractFromObject(final Object object) {

        final TuplizerSetting meta = new TuplizerSetting();

        PersistentCookie cookieMeta = object.getClass().getAnnotation(PersistentCookie.class);
        if (cookieMeta == null) {
            meta.checkedClasses.append(object.getClass().getCanonicalName()).append(',');
            // try look on interfaces.
            final Class<?>[] ifaces = object.getClass().getInterfaces();
            if (ifaces != null && ifaces.length > 0) {
                for (Class<?> iface : ifaces) {
                    cookieMeta = iface.getAnnotation(PersistentCookie.class);
                    if (cookieMeta == null) {
                        meta.checkedClasses.append(iface.getCanonicalName()).append(',');
                    } else {
                        meta.set(cookieMeta);
                        return meta;
                    }
                }
            }
        } else {
            meta.set(cookieMeta);
        }
        return meta;

    }


    /**
     * Convenience class for meta data of object.
     */
    static class TuplizerSetting {

        private String key;
        private Integer expiry;
        private String path;

        private StringBuilder checkedClasses = new StringBuilder();

        void set(final PersistentCookie cookieMeta) {
            this.key = cookieMeta.value();
            this.expiry = cookieMeta.expirySeconds();
            this.path = cookieMeta.path();
        }

    }


}
