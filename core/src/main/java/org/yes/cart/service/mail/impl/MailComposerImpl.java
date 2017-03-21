/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.mail.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.MailPart;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.mail.MailComposerTemplateSupport;
import org.yes.cart.service.mail.MailTemplateResourcesProvider;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible to compose mail from given:
 * templates (txt and html);
 * set of variables;
 * current shop context;
 * current spring context.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class MailComposerImpl implements MailComposer {

    private static final Logger LOG = LoggerFactory.getLogger(MailComposerImpl.class);

    private static final String ATTACHMENT_PREFIX = "attachment:";
    private static final String ATTACHMENT_SUFFIX = ";";

    /**
     * Default regular expression.
     */
    private final static String RE_EXPRESSION = "cid\\:([^'\"]+)['|\"]";

    private String resourceExpression = RE_EXPRESSION;

    private Pattern resourcePattern;

    private final MailComposerTemplateSupport templateSupport;

    private final MailTemplateResourcesProvider mailTemplateResourcesProvider;


    /**
     * Construct mail composer
     *
     * @param mailTemplateResourcesProvider mail resources provider
     */
    public MailComposerImpl(final MailTemplateResourcesProvider mailTemplateResourcesProvider,
                            final MailComposerTemplateSupport templateSupport) throws ClassNotFoundException {
        this.mailTemplateResourcesProvider = mailTemplateResourcesProvider;
        final ClassLoader classLoader = this.getClass().getClassLoader();
        classLoader.loadClass(DecimalFormat.class.getName());
        this.templateSupport = templateSupport;

        this.templateSupport.registerFunction("include", new MailComposerTemplateSupport.FunctionProvider() {
            @Override
            public Object doAction(final Object... params) {

                if (params != null && params.length == 3) {

                    final String uri = String.valueOf(params[0]);

                    final String locale = String.valueOf(params[1]);
                    final Map<String, Object> context = (Map<String, Object>) params[2];

                    final Map<String, Object> mailComposer = (Map<String, Object>) context.get("MailComposer");
                    final List<String> mailTemplateChain = (List<String>) mailComposer.get("mailTemplateChain");
                    final String shopCode = (String) mailComposer.get("shopCode");
                    final String ext = (String) mailComposer.get("ext");
                    final Map<String, Object> model = (Map<String, Object>) mailComposer.get("model");

                    return processTemplate(mailTemplateChain, shopCode, locale, uri, ext, model, true);

                }

                return "";
            }
        });


    }

    void composeMessage(final MimeMessage message,
                        final String shopCode,
                        final String locale,
                        final List<String> mailTemplateChain,
                        final String templateName,
                        final String from,
                        final String toEmail,
                        final String ccEmail,
                        final String bccEmail,
                        final Map<String, Object> model)
            throws MessagingException, IOException, ClassNotFoundException {


        final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);

        helper.setSentDate(new Date());

        if (ccEmail != null) {
            helper.setCc(ccEmail);
        }

        if (bccEmail != null) {
            helper.setBcc(bccEmail);
        }


        final String textContent = processTemplate(mailTemplateChain, shopCode, locale, templateName, ".txt", model);
        final String htmlContent = processTemplate(mailTemplateChain, shopCode, locale, templateName, ".html", model);
        final String propString = processTemplate(mailTemplateChain, shopCode, locale, templateName, ".properties", model);
        final Properties prop = new Properties();
        if (propString != null) {
            prop.load(new StringReader(propString));
        }
        helper.setSubject(prop.getProperty("subject"));

        if (from == null) {
            helper.setFrom(prop.getProperty("from"));
        } else {
            helper.setFrom(from);
        }

        final Map<String, byte[]> attachments = collectAttachments(model);

        composeMessage(helper, textContent, htmlContent, attachments, mailTemplateChain, shopCode, locale, templateName);

    }

    /**
     * Collect attachments from model using key match
     *
     * @param model mail model
     *
     * @return map of attachments
     */
    Map<String, byte[]> collectAttachments(final Map<String, Object> model) {

        final Map<String, byte[]> attachments = new HashMap<String, byte[]>();
        for (final String key : model.keySet()) {
            if (key.startsWith(ATTACHMENT_PREFIX)) {

                final Object attachment = model.get(key);
                if (attachment instanceof byte[]) {

                    attachments.put(key, (byte[]) attachment);

                } else {

                    LOG.error("Invalid attachment in model. Attachments must be of type 'byte[]'");

                }

            }
        }
        return attachments;
    }


    /**
     * Fill mail message. At least one of the templates must be given.
     *
     * @param helper          mail message helper
     * @param textContent       optional text template
     * @param htmlContent       optional html template
     * @param attachments       optional attachments
     * @param mailTemplateChain path to template folder
     * @param shopCode        shop code
     * @param locale          locale
     * @param templateName    template name
     * @throws MessagingException     in case if message can not be composed
     * @throws java.io.IOException    in case of inline resources can not be found
     * @throws ClassNotFoundException in case if something wrong with template engine
     */
    void composeMessage(final MimeMessageHelper helper,
                        final String textContent,
                        final String htmlContent,
                        final Map<String, byte[]> attachments,
                        final List<String> mailTemplateChain,
                        final String shopCode,
                        final String locale,
                        final String templateName)
            throws MessagingException, ClassNotFoundException, IOException {

        if (textContent == null || htmlContent == null) {
            if (textContent != null) {
                helper.setText(textContent, false);
            }

            if (htmlContent != null) {
                helper.setText(htmlContent, true);
                inlineResources(helper, htmlContent, mailTemplateChain, shopCode, locale, templateName);
                addAttachments(helper, attachments);
            }

        } else {
            helper.setText(textContent, htmlContent);
            inlineResources(helper, htmlContent, mailTemplateChain, shopCode, locale, templateName);
            addAttachments(helper, attachments);
        }

    }


    /**
     * Add inline resource to mail message.
     * Resource id will be interpreted as file name in following fashion: filename_ext.
     *
     * @param helper          MimeMessageHelper, that has mail message
     * @param htmlTemplate    html message template
     * @param mailTemplateChain physical path to resources
     * @param shopCode        shop code
     * @param locale          locale
     * @param templateName    template name
     *
     * @throws javax.mail.MessagingException in case if resource can not be inlined
     */
    void inlineResources(final MimeMessageHelper helper,
                         final String htmlTemplate,
                         final List<String> mailTemplateChain,
                         final String shopCode,
                         final String locale,
                         final String templateName) throws MessagingException, IOException {

        if (StringUtils.isNotBlank(htmlTemplate)) {
            final List<String> resourcesIds = getResourcesId(htmlTemplate);
            if (!resourcesIds.isEmpty()) {
                for (String resourceId : resourcesIds) {
                    final String resourceFilename = transformResourceIdToFileName(resourceId);
                    final byte[] content = mailTemplateResourcesProvider.getResource(mailTemplateChain, shopCode, locale, templateName, resourceFilename);
                    helper.addInline(resourceId, new ByteArrayResource(content) {
                        @Override
                        public String getFilename() {
                            return resourceFilename;
                        }
                    });
                }
            }
        }

    }

    /**
     * Convert model key into attachment meta data.
     *
     * Example:
     * attachment:image/jpeg;myimage.jpg
     *
     * This produces:
     * Content Type: image/jpeg
     * Filename: myimage.jpg
     *
     * @param key model key, or null if format is invalid
     *
     * @return content type and filename
     */
    Pair<String, String> convertAttachmentKeyIntoContentTypeAndFilename(final String key) {
        try {
            final String[] contentTypeAndFile = key.substring(ATTACHMENT_PREFIX.length()).split(ATTACHMENT_SUFFIX);
            return new Pair<String, String>(contentTypeAndFile[0], contentTypeAndFile[1]);
        } catch (Exception exp) {
            LOG.error("Invalid attachment key {} ... attachment is skipped", key);
            return null;
        }
    }

    /**
     * Add attachments.
     *
     * @param helper mime message helper
     * @param attachments attachments from model
     *
     * @throws MessagingException
     * @throws IOException
     */
    void addAttachments(final MimeMessageHelper helper,
                        final Map<String, byte[]> attachments) throws MessagingException, IOException {

        if (MapUtils.isNotEmpty(attachments)) {

            for (final Map.Entry<String, byte[]> attach : attachments.entrySet()) {

                final Pair<String, String> contentTypeAndFile = convertAttachmentKeyIntoContentTypeAndFilename(attach.getKey());
                if (contentTypeAndFile != null) {
                    helper.addAttachment(contentTypeAndFile.getSecond(), new ByteArrayResource(attach.getValue()), contentTypeAndFile.getFirst());
                }

            }

        }
    }

    /**
     * Transform resource id in filename_extension format to filename.extension
     *
     * @param resourceName resource id
     * @return filename
     */
    String transformResourceIdToFileName(final String resourceName) {
        return resourceName.replace('_', '.');
    }


    /**
     * Collect resource ids
     *
     * @param htmlTemplate given html template
     * @return list of resource ids in template order.
     */
    List<String> getResourcesId(final String htmlTemplate) {
        final List<String> resourceIds = new ArrayList<String>();
        final Matcher matcher = getResourcePattern().matcher(htmlTemplate);
        while (matcher.find()) {
            resourceIds.add(matcher.group(1));
        }
        return resourceIds;
    }


    /**
     * Get regular expression to collect resources to inline.
     *
     * @return regular expression
     */
    public String getResourceExpression() {
        return resourceExpression;
    }

    /**
     * Set regular expression  to collect resources to inline.
     * Also set pattern to null.
     *
     * @param resourceExpression regular expression
     */
    public void setResourceExpression(final String resourceExpression) {
        this.resourceExpression = resourceExpression;
        this.resourcePattern = null;
    }


    private Pattern getResourcePattern() {
        if (resourcePattern == null) {
            resourcePattern = Pattern.compile(getResourceExpression(), Pattern.CASE_INSENSITIVE);
        }
        return resourcePattern;
    }


    /**
     * Get template as string.
     *
     * @param mailTemplateChain path to template folder
     * @param shopCode          shop code
     * @param locale            locale
     * @param fileName          file name
     * @param ext               file extension
     * @param model             model for email
     *
     * @return template if exists
     */
    String processTemplate(final List<String> mailTemplateChain,
                           final String shopCode,
                           final String locale,
                           final String fileName,
                           final String ext,
                           final Map<String, Object> model) {

        return processTemplate(mailTemplateChain, shopCode, locale, fileName, ext, model, false);
    }

    /**
     * Get template as string.
     *
     * @param mailTemplateChain path to template folder
     * @param shopCode          shop code
     * @param locale            locale
     * @param fileName          file name
     * @param ext               file extension
     * @param model             model for email
     * @param include           true if this is include processing
     *
     * @return template if exists
     */
    String processTemplate(final List<String> mailTemplateChain,
                           final String shopCode,
                           final String locale,
                           final String fileName,
                           final String ext,
                           final Map<String, Object> model,
                           final boolean include) {
        try {
            // Get top level template
            final String template = mailTemplateResourcesProvider.getTemplate(mailTemplateChain, shopCode, locale, fileName, ext);

            final Map<String, Object> enhancedModel = new HashMap<String, Object>(model);
            final Map<String, Object> mailComposer = new HashMap<String, Object>();
            enhancedModel.put("MailComposer", mailComposer);
            mailComposer.put("mailTemplateChain", mailTemplateChain);
            mailComposer.put("shopCode", shopCode);
            mailComposer.put("locale", locale);
            mailComposer.put("fileName", fileName);
            mailComposer.put("ext", ext);
            mailComposer.put("model", model);

            // Process the top level template (which will cascade includes)
            final String content = templateSupport.processTemplate(template, locale, enhancedModel);

            if (!include) {
                LOG.debug("Processed template for locale {}, template: {}, ext: {}\n{}", new Object[]{locale, fileName, ext, content});
            }

            return content;

        } catch (IOException e) {
            LOG.warn("No template found for locale {}, template: {}, ext: {}", new Object[] { locale, fileName, ext });
            return null;
        }

    }

    /** {@inheritDoc} */
    @Override
    public void composeMessage(final Mail mail,
                               final String shopCode,
                               final String locale,
                               final List<String> mailTemplateChain,
                               final String templateName,
                               final String from,
                               final String toEmail,
                               final String ccEmail,
                               final String bccEmail,
                               final Map<String, Object> model)
            throws MessagingException, IOException, ClassNotFoundException {

        mail.setShopCode(shopCode);
        mail.setRecipients(toEmail);

        if (ccEmail != null) {
            mail.setCc(ccEmail);
        }

        if (bccEmail != null) {
            mail.setBcc(bccEmail);
        }

        final String textContent = processTemplate(mailTemplateChain, shopCode, locale, templateName, ".txt", model);
        final String htmlContent = processTemplate(mailTemplateChain, shopCode, locale, templateName, ".html", model);
        final String propString = processTemplate(mailTemplateChain, shopCode, locale, templateName, ".properties", model);
        final Properties prop = new Properties();
        if (propString != null) {

            prop.load(new StringReader(propString));

        }

        mail.setSubject(prop.getProperty("subject"));

        if (from == null) {
            mail.setFrom(prop.getProperty("from"));
        } else {
            mail.setFrom(from);
        }

        final Map<String, byte[]> attachments = collectAttachments(model);

        composeMessage(mail, textContent, htmlContent, attachments, mailTemplateChain, shopCode, locale, templateName);

    }


    /**
     * Fill mail message. At least one of the templates must be given.
     *
     * @param mail            mail message
     * @param textContent       optional text template
     * @param htmlContent       optional html template
     * @param attachments       optional attachments
     * @param mailTemplateChain path to template folder
     * @param shopCode        shop code
     * @param locale          locale
     * @param templateName    template name
     *
     * @throws MessagingException     in case if message can not be composed
     * @throws java.io.IOException    in case of inline resources can not be found
     * @throws ClassNotFoundException in case if something wrong with template engine
     */
    void composeMessage(final Mail mail,
                        final String textContent,
                        final String htmlContent,
                        final Map<String, byte[]> attachments,
                        final List<String> mailTemplateChain,
                        final String shopCode,
                        final String locale,
                        final String templateName)
            throws MessagingException, ClassNotFoundException, IOException {

        if (textContent == null || htmlContent == null) {
            if (textContent != null) {
                mail.setTextVersion(textContent);
            }
            if (htmlContent != null) {
                mail.setHtmlVersion(htmlContent);
                inlineResources(mail, htmlContent, mailTemplateChain, shopCode, locale, templateName);
                addAttachments(mail, attachments);
            }

        } else {
            mail.setTextVersion(textContent);
            mail.setHtmlVersion(htmlContent);
            inlineResources(mail, htmlContent, mailTemplateChain, shopCode, locale, templateName);
            addAttachments(mail, attachments);
        }

    }

    /**
     * Add attachments.
     *
     * @param mail mail
     * @param attachments attachments from model
     *
     * @throws MessagingException
     * @throws IOException
     */
    void addAttachments(final Mail mail,
                        final Map<String, byte[]> attachments) throws MessagingException, IOException {

        if (MapUtils.isNotEmpty(attachments)) {

            for (final Map.Entry<String, byte[]> attach : attachments.entrySet()) {

                final Pair<String, String> contentTypeAndFile = convertAttachmentKeyIntoContentTypeAndFilename(attach.getKey());
                if (contentTypeAndFile != null) {
                    final MailPart part = mail.addPart();
                    part.setFilename(contentTypeAndFile.getSecond());
                    part.setResourceId(attach.getKey());
                    part.setData(attach.getValue());
                }

            }

        }
    }



    /**
     * Add inline resource to mail message.
     * Resource id will be interpreted as file name in following fashion: filename_ext.
     *
     * @param mail            MimeMessageHelper, that has mail message
     * @param htmlTemplate    html message template
     * @param mailTemplateChain path to template folder
     * @param shopCode          shop code
     * @param locale          locale
     * @param templateName    template name
     *
     * @throws javax.mail.MessagingException in case if resource can not be inlined
     */
    void inlineResources(final Mail mail,
                         final String htmlTemplate,
                         final List<String> mailTemplateChain,
                         final String shopCode,
                         final String locale,
                         final String templateName) throws MessagingException, IOException {

        if (StringUtils.isNotBlank(htmlTemplate)) {
            final List<String> resourcesIds = getResourcesId(htmlTemplate);
            if (!resourcesIds.isEmpty()) {
                for (String resourceId : resourcesIds) {
                    final String resourceFilename = transformResourceIdToFileName(resourceId);
                    final byte[] content = mailTemplateResourcesProvider.getResource(mailTemplateChain, shopCode, locale, templateName, resourceFilename);
                    final MailPart part = mail.addPart();
                    part.setResourceId(resourceId);
                    part.setFilename(resourceFilename);
                    part.setData(content);
                }
            }
        }

    }



    /** {@inheritDoc} */
    @Override
    public void convertMessage(final Mail mail, final MimeMessage mimeMessage)
            throws MessagingException, IOException, ClassNotFoundException {

        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(mail.getRecipients());

        helper.setSentDate(new Date());

        if (mail.getCc() != null) {
            helper.setCc(mail.getCc());
        }

        if (mail.getBcc() != null) {
            helper.setBcc(mail.getBcc());
        }

        final String textTemplate = mail.getTextVersion();
        final String htmlTemplate = mail.getHtmlVersion();

        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        if (textTemplate == null || htmlTemplate == null) {
            if (textTemplate != null) {
                helper.setText(textTemplate, false);
            }

            if (htmlTemplate != null) {
                helper.setText(htmlTemplate, true);
                inlineResources(helper, mail);
            }

        } else {
            helper.setText(textTemplate, htmlTemplate);
            inlineResources(helper, mail);
        }

    }


    /**
     * Add inline resource to mail message.
     * Resource id will be interpreted as file name in following fashion: filename_ext.
     *
     * @param helper          MimeMessageHelper, that has mail message
     * @param mail            html message template
     *
     * @throws javax.mail.MessagingException in case if resource can not be inlined
     */
    void inlineResources(final MimeMessageHelper helper,
                         final Mail mail) throws MessagingException {

        if (CollectionUtils.isNotEmpty(mail.getParts())) {
            for (final MailPart part : mail.getParts()) {
                final String fileName = part.getFilename();
                final String resourceId = part.getResourceId();
                if (resourceId.startsWith(ATTACHMENT_PREFIX)) {
                    final Pair<String, String> contentTypeAndFile = convertAttachmentKeyIntoContentTypeAndFilename(resourceId);
                    if (contentTypeAndFile != null) {
                        helper.addAttachment(part.getFilename(), new ByteArrayResource(part.getData()), contentTypeAndFile.getFirst());
                    }
                } else {
                    helper.addInline(resourceId, new ByteArrayResource(part.getData()) {
                        @Override
                        public String getFilename() {
                            return fileName;
                        }
                    });
                }
            }
        }

    }


}
