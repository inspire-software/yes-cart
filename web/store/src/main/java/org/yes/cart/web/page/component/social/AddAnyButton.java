package org.yes.cart.web.page.component.social;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.web.page.component.BaseComponent;

import javax.servlet.http.HttpServletRequest;

/**
 * User: denispavlov
 * Date: 13-03-17
 * Time: 8:36 AM
 */
public class AddAnyButton extends BaseComponent {

    private final Product product;

    public AddAnyButton(final String id, final Product product) {
        super(id);
        this.product = product;
    }

    @Override
    protected void onBeforeRender() {

        final BookmarkablePageLink link = (BookmarkablePageLink) getWicketSupportFacade().links().newProductLink("link", product.getProductId());

        final String lang = getLocale().getLanguage();

        final CharSequence uri = link.urlFor(link.getPageClass(), link.getPageParameters());
        final HttpServletRequest req = (HttpServletRequest)((WebRequest) RequestCycle.get().getRequest()).getContainerRequest();
        final String absUri = RequestUtils.toAbsolutePath(req.getRequestURL().toString(), uri.toString());

        final String name = getI18NSupport().getFailoverModel(product.getDisplayName(), product.getName()).getValue(lang);

        /*
            <a class="a2a_dd" href="http://www.addtoany.com/share_save?linkurl=www.abc-style.com.ua&amp;linkname=Some%20page%20name">Share</a>
         */
        final StringBuilder anchor = new StringBuilder()
                .append("<a class=\"a2a_dd\" href=\"http://www.addtoany.com/share_save?linkurl=")
                .append(absUri)
                .append("&amp;linkname=")
                .append(name)
                .append("\">Share</a>");

        /*

            <script type="text/javascript">
                var a2a_config = a2a_config || {};
                a2a_config.linkname = "Some page name";
                a2a_config.linkurl = "www.abc-style.com.ua";
                a2a_config.locale = "uk";
                a2a_config.color_main = "D7E5ED";
                a2a_config.color_border = "AECADB";
                a2a_config.color_link_text = "333333";
                a2a_config.color_link_text_hover = "333333";
            </script>

         */
        final StringBuilder js = new StringBuilder()
                .append("<script type=\"text/javascript\">\n")
                .append("            var a2a_config = a2a_config || {};\n")
                .append("            a2a_config.linkname = \"").append(name).append("\";\n")
                .append("            a2a_config.linkurl = \"").append(absUri).append("\";\n")
                .append("            a2a_config.locale = \"").append(lang).append("\";")
                .append("            a2a_config.color_main = \"D7E5ED\";")
                .append("            a2a_config.color_border = \"AECADB\";")
                .append("            a2a_config.color_link_text = \"333333\";")
                .append("            a2a_config.color_link_text_hover = \"333333\";")
                .append("</script>");

        addOrReplace(new Label("anchor", anchor.toString()).setEscapeModelStrings(false));
        addOrReplace(new Label("js", js.toString()).setEscapeModelStrings(false));

        super.onBeforeRender();
    }
}
