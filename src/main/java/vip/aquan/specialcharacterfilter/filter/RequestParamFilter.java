package vip.aquan.specialcharacterfilter.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 请求参数过滤器（过滤非法字符，解决SQL盲注的问题）
 *
 * @author LYG
 * @version 2019-09-04
 */
public class RequestParamFilter extends OncePerRequestFilter {

    public static final Logger logger = LoggerFactory.getLogger(RequestParamFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //只对GET请求执行危险字符过滤
        if ("GET".equals(request.getMethod())) {
            filterChain.doFilter(new ParamRequestWrapper(request), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private class ParamRequestWrapper extends HttpServletRequestWrapper {

        public ParamRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            // 返回值之前 先进行过滤
            return filterDangerousCharacters(name, super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String name) {
            // 返回值之前 先进行过滤
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            for (int i = 0; i < values.length; i++) {
                values[i] = filterDangerousCharacters(name, values[i]);
            }

            return values;
        }

        @Override
        public Map getParameterMap() {
            Map keys = new HashMap();
            Set set = keys.entrySet();
            Iterator iters = set.iterator();
            while (iters.hasNext()) {
                Object key = iters.next();
                Object value = keys.get(key);
                keys.put(key, filterDangerString(key.toString(), (String[]) value));
            }
            return keys;
        }


        public String filterDangerousCharacters(String name, String value) {
            if ("token".equals(name)) {
                return value;
            }
            // 匹配参数值格式为：数字,数字,.....的格式（例如：1,2  1,2,  1  1,），此格式的参数不过滤
            if (StringUtils.isNotBlank(value)) {
                if (value.matches("(\\d+,\\d*,?)+|(\\d+,?)")) {
                    return value;
                }
            }
            if (value == null || "".equals(value)) {
                return null;
            }
            if (!validateParam(value)) {
                String queryString = super.getQueryString();
                queryString = queryString != null && !"".equals(queryString) ? "?" + queryString : "";
                logger.info(new String(super.getRequestURL()) + queryString);
                logger.info("param[" + name + "] contains dangerous characters!");
                return null;
            }
            return value;
        }

        public String[] filterDangerString(String name, String[] value) {
            if (value == null) {
                return null;
            }
            for (int i = 0; i < value.length; i++) {
                String val = filterDangerousCharacters(name, value[i]);
                value[i] = val;
            }

            return value;
        }

        private boolean validateParam(String str) {
            String injStr = "=|'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|+|,";
            String[] injStrArr = injStr.split("\\|");
            for (int i = 0; i < injStrArr.length; i++) {
                if (str.contains(injStrArr[i])) {
                    return false;
                }
            }
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            if (str.matches(regEx)) {
                return false;
            }
            return true;
        }
    }
}