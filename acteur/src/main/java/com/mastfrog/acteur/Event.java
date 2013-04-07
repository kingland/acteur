/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mastfrog.acteur;

import com.mastfrog.acteur.util.HeaderValueType;
import com.google.common.base.Optional;
import com.mastfrog.acteur.util.Method;
import com.mastfrog.url.Path;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpMessage;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;

/**
 * An HTTP request or similar, which is passed to Pages for their
 * Acteurs to respond to.
 *
 * @author Tim Boudreau
 */
public interface Event {

    Channel getChannel();

    /**
     * Get the actual HTTP request in all its gory detail
     * @return An http request
     */
    HttpMessage getRequest();

    /**
     * Get the HTTP method for this request
     * @return A method name
     */
    Method getMethod();

    /**
     * Get the remote address of whoever made the request
     * @return 
     */
    SocketAddress getRemoteAddress();

    /**
     * Get a single HTTP header
     * @param nm The header name
     * @return The header
     */
    String getHeader(String nm);

    /**
     * Get a single request parameter
     * @param param The parameter name
     * @return A parameter or null
     */
    String getParameter(String param);

    /**
     * Get the <i>logical</i> path of this request.  The web application
     * may be "mounted" on some path (i.e. it is being proxied as part of
     * a larger site).  This method will give you the path, sans the
     * base portion of it.  So, if a request URL might be for
     * <code>http://example.com/myapp/foo/bar</code>, <code>getPath()</code>
     * might return <code>foo/bar</code>.
     * 
     * @return 
     */
    Path getPath();

    /**
     * Get a header as an appropriate Java object, or null if it is not
     * present.
     * 
     * @see Headers for a standard HTTP header types
     * @param <T> The return type
     * @param value A header definition/parser/encoder
     * @return An object or null if the header is missing or invalid
     */
     <T> T getHeader(HeaderValueType<T> value);

    /**
     * Gets the HTTP parameters as a flat map, ignoring duplicated
     * keys.  To be technically correct, the same request parameter may
     * be repeated any number of times, so a Map &lt;String, List&lt;String&gt;&gt;
     * would losslessly represent parameters.  In practice, this is usually
     * a bit pedantic and annoying, so this provides a convenient way
     * to flatten it into a map.
     * 
     * @return A map
     */
    Map<String, String> getParametersAsMap();

    /**
     * Very primitive Java dynamic proxy magic: You write a Java interface
     * with methods that each return a primitive type, and whose name
     * corresponds to a URL parameter you expect to get.
     * <p/>
     * Calling this method will generate a dynamic proxy object of the
     * interface type you pass, which implements these methods to return
     * objects, doing the necessary conversions.
     * <p/>
     * Be aware that conversion can result in NumberFormatExceptions, etc.
     * 
     * @param <T>
     * @param type
     * @return 
     */
     <T> T getParametersAs(Class<T> type);

    /**
     * Will use Jackson to parse the request body and return an object of
     * the type requested if possible.
     * <p/>
     * 
     * @param <T> The type
     * @param type The type of object to return
     * @return An object of type T
     * @throws IOException if the body is malformed or for some other reason,
     * cannot be parsed
     */
     <T> T getContentAsJSON(Class<T> type) throws IOException;

    ByteBuf getContent() throws IOException;
    
    Optional<Integer> getIntParameter(String name);
    Optional<Long> getLongParameter(String name);

    boolean isKeepAlive();
}
