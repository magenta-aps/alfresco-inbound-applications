import org.alfresco.email.server.handler.AbstractEmailMessageHandler;
import org.alfresco.service.cmr.email.EmailMessage;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Created by flemmingheidepedersen on 22/08/2017.
 */
public class MagentaEmailMessageHandler extends AbstractEmailMessageHandler {
    @Override
    public void processMessage(NodeRef nodeRef, EmailMessage emailMessage) {
        System.out.println("the eagle has landed");
    }
}
