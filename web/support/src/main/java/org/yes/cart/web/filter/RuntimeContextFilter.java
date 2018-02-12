package org.yes.cart.web.filter;

import org.yes.cart.search.query.impl.SearchUtil;
import org.yes.cart.util.TimeContext;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * User: denispavlov
 * Date: 09/02/2018
 * Time: 08:14
 */
public class RuntimeContextFilter extends AbstractFilter implements Filter {

    @Override
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {
        TimeContext.setNow();  // TODO: Time Machine
        return servletRequest;
    }

    @Override
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {
        TimeContext.clear();
    }

    @Override
    public void destroy() {
        TimeContext.destroy();
        SearchUtil.destroy(); // Ensure we clear our the threadlocal analysers
        super.destroy();
    }
}
