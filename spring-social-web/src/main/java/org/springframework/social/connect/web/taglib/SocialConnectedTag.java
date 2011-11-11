package org.springframework.social.connect.web.taglib;


/**
 * JSP Tag to return true/false if you're connected to a provider.
 *
 * Sample usages in JSP:
 *
 * Include at top of JSP:
 * <%@ taglib prefix="social" uri="http://org.springframework/social" %>
 *
 * <social:connected provider="facebook">
 * 	    [ show some FB profile info ]
 * </social:connected>
 *
 * OR another usage, more suitable for if you need to toggle on a page a connect check
 * or multiple checks need to be made:
 *
 * <c:set var="connectedToFB" value="false"/>
 * <social:connected provider="facebook">
 * 	    <c:set var="connectedToFB" value="true"/>
 * 	    [ Show Disconnect link ]
 * </social:connected>
 * <c:if test="${!connectedToFB}">
 *  	[ Show Connect Button/link/form ]
 * </c:if>
 *
 * Note: You could use social:notConnected tag in place of using c:if and having to set the page scoped
 * connectedToFB var, but it's a bit more efficient to not have to make the FB connection more than once
 * on page
 *
 * @author Rick Reumann
 * @author Craig Walls
 */
public class SocialConnectedTag extends BaseSocialConnectedTag {

	@Override
    protected int doStartTagInternal() throws Exception {
        return super.evaluateBodyIfConnected(true);
    }
}