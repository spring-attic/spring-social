package org.springframework.social.connect.web.taglib;

/**
 *  JSP Tag to return true/false if you're NOT connected to a provider.
 *  See {@link SocialConnectedTag} for sample usages with a JSP.
 *
 * @author Rick Reumann
 * @author Craig Walls
 */
public class SocialNotConnectedTag extends BaseSocialConnectedTag {

    @Override
    protected int doStartTagInternal() throws Exception {
        return super.evaluateBodyIfConnected(false);
    }
}
