package org.springframework.samples.petclinic.log;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Component
public class MdcInsertingServletFilter implements Filter
{
    public static final String REQUEST_PREFIX = "req."; // corresponds to what kibana logs.
    public static final String REQUEST_PREFIX_HEADERS = REQUEST_PREFIX + "headers.";
    private static final String HEADERS[] = new String[]{
            "host",
            "referer",
            "user-agent",
            "x-correlation-id",
            "x-forwarded-host",
            "x-forwarded-port",
            "x-forwarded-proto",
            "x-real-ip",
            "x-request-id"
    };

    public static final String REQUEST_METHOD = REQUEST_PREFIX + "method";
    public static final String REQUEST_REMOTE_ADDRESS = REQUEST_PREFIX + "remoteAddress";

    public static final String REQUEST_REQUEST_URI = REQUEST_PREFIX + "uri";
    public static final String REQUEST_QUERY_STRING = REQUEST_PREFIX + "queryString";
    public static final String REQUEST_REQUEST_URL = REQUEST_PREFIX + "url";
    public static final String REQUEST_REQUEST_PATH = REQUEST_PREFIX + "path";
    public static final String REQUEST_REMOTE_USER = REQUEST_PREFIX + "remoteUser";
    public static final String REQUEST_REQEUST_ID = REQUEST_PREFIX + "requestId";
    public static final String REQUEST_SESSION_ID = REQUEST_PREFIX + "sessionId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    @Override
    public void destroy()
    {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        final Map<String, String> originalContextMap = MDC.getCopyOfContextMap();
        try
        {
            insertBasicProperties(request);

            if (request instanceof HttpServletRequest)
            {
                final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                insertAdditionalProperties(httpServletRequest);
            }
            chain.doFilter(request, response);
        }
        finally
        {
            if (originalContextMap == null)
            {
                MDC.clear();
            }
            else
            {
                MDC.setContextMap(originalContextMap);
            }
        }
    }

    protected void insertBasicProperties(ServletRequest request)
    {
        MDC.put(REQUEST_REQEUST_ID, UUID.randomUUID().toString());
        MDC.put(REQUEST_REMOTE_ADDRESS, request.getRemoteHost());

        if (request instanceof HttpServletRequest)
        {
            // from MDCInsertingServletFilter
            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            MDC.put(REQUEST_METHOD, httpServletRequest.getMethod());
            MDC.put(REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
            final StringBuffer requestURL = httpServletRequest.getRequestURL();
            if (requestURL != null)
            {
                MDC.put(REQUEST_REQUEST_URL, requestURL.toString());
            }
            MDC.put(REQUEST_QUERY_STRING, httpServletRequest.getQueryString());

            Arrays.stream(HEADERS)
                    .map(h -> new AbstractMap.SimpleEntry<>(h, httpServletRequest.getHeader(h)))
                    .filter(kv -> kv.getValue() != null)
                    .forEach(kv -> MDC.put(REQUEST_PREFIX_HEADERS + kv.getKey(), kv.getValue()));

            // additional attributes
            MDC.put(REQUEST_REQUEST_PATH, getRequestPath(httpServletRequest));
            MDC.put(REQUEST_REMOTE_USER, httpServletRequest.getRemoteUser());
            final HttpSession session = httpServletRequest.getSession(false);
            if (session != null)
            {
                MDC.put(REQUEST_SESSION_ID, session.getId());
            }
        }
    }

    /**
     * Override this method to insert additional properties into MDC.
     * @param request servlet request
     */
    protected void insertAdditionalProperties(HttpServletRequest request)
    {

    }

    protected String getRequestPath(HttpServletRequest request)
    {
        final StringBuilder sb = new StringBuilder();
        final String servletPath = request.getServletPath();
        if (servletPath != null)
        {
            sb.append(servletPath);
        }
        final String pathInfo = request.getPathInfo();
        if (pathInfo != null)
        {
            sb.append(pathInfo);
        }
        return sb.toString();
    }
}
