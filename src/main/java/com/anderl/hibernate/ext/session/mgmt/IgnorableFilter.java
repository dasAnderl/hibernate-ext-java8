/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anderl.hibernate.ext.session.mgmt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is an abstract only performing logic for urls which are not defined in its init ignoredUrls parameter
 * <p>
 * <init-param>
 * <param-name>ignoredUrls</param-name>
 * <param-value>url1, url2, url3</param-value>
 * </init-param>
 */
public abstract class IgnorableFilter extends OncePerRequestFilter {

    public static Logger log = LoggerFactory.getLogger(IgnorableFilter.class);

    private List<String> ignoredUrls;

    {
        final String ignoredUrlsString = getFilterConfig().getInitParameter("ignoredUrls");
        if (ignoredUrlsString != null && !StringUtils.isEmpty(ignoredUrlsString)) {
            this.ignoredUrls = Arrays.asList(ignoredUrlsString.split(","))
                    .stream()
                    .filter(url -> !StringUtils.isEmpty(url))
                    .map(url -> url = url.trim())
                    .collect(Collectors.toList());
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isIgnoredUrl(request)) {
            log.debug("{} has been ignored. see IgnorableFilter in web.xml", request.getServletPath());
            filterChain.doFilter(request, response);
        } else {
            doFilterUnignored(request, response, filterChain);
        }
    }

    protected abstract void doFilterUnignored(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException;

    private boolean isIgnoredUrl(HttpServletRequest request) {
        final String currentUrl = request.getServletPath();
        if (ignoredUrls == null) return false;
        for (final String ignoredUrl : ignoredUrls) {
            if (currentUrl.startsWith(ignoredUrl)) return true;
        }
        return false;
    }
}
