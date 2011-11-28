package org.springframework.social.connect.web.taglib;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 *  {@link SocialConnectedTag} and {@link SocialNotConnectedTag} extend to
 *  provide functionality within a JSP to determine if you are connected to a
 *  provider or not.
 *
 * @author Rick Reumann
 * @author Craig Walls
 */
public abstract class BaseSocialConnectedTag extends RequestContextAwareTag {

    //tag attribute
    protected String provider;

    protected int evaluateBodyIfConnected(boolean evaluateIfConnected) {

        if (supportsProvider(provider) && getConnectionRepository().findConnections(provider).size() > 0) {
            //we are connected to provider
            return evaluateIfConnected ? EVAL_BODY_INCLUDE : SKIP_BODY;
        }
        //we aren't connected
        return evaluateIfConnected ? SKIP_BODY : EVAL_BODY_INCLUDE;
    }

    private ConnectionRepository getConnectionRepository() {
        WebApplicationContext applicationContext = getRequestContext().getWebApplicationContext();
        return applicationContext.getBean(ConnectionRepository.class);
    }

	//TODO: more versatile approach to hanlde other providers?
    private boolean supportsProvider(String provider) {
        return provider.equals("facebook") || provider.equals("twitter");
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}