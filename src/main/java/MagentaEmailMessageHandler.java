import org.json.simple.JSONArray;
import org.alfresco.email.server.handler.AbstractEmailMessageHandler;
import org.alfresco.email.server.handler.FolderEmailMessageHandler;
import org.alfresco.email.server.handler.DocumentEmailMessageHandler;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.email.EmailMessage;
import org.alfresco.service.cmr.email.EmailMessagePart;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.text.AbstractDocument;
import java.io.InputStream;

/**
 * Created by flemmingheidepedersen on 22/08/2017.
 */
public class MagentaEmailMessageHandler extends FolderEmailMessageHandler {


    private FileFolderService fileFolderService;

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    private final String applicants_filename = "applicants_email.txt";
    private final QName property = QName.createQName("", "emails");


    private void addApplicantToList(String email, NodeRef root) throws JSONException, ParseException {

        NodeRef n = super.getNodeService().getChildByName(root, ContentModel.ASSOC_CONTAINS, applicants_filename);

        if (n == null) {
            n = (fileFolderService.create(root, applicants_filename, ContentModel.TYPE_CONTENT)).getNodeRef();
            System.out.println("hvad blev n:" + n);

            JSONObject tmp = new JSONObject();
            JSONArray tmpArray =  new JSONArray();
            tmp.put("email", tmpArray);


            super.getNodeService().setProperty(n, property, tmp.toString());
        }

        String content = (String) super.getNodeService().getProperty(n, property);

        org.json.simple.JSONObject j = null;

        JSONParser parser = new JSONParser();
        j = (org.json.simple.JSONObject) parser.parse(content);

        JSONArray jsonArray = (JSONArray)(j.get("email"));
        jsonArray.add(email);
        j.put("email", jsonArray);

        System.out.println("the new:" + j.toString());

        super.getNodeService().setProperty(n,property,j.toString());
    }


    @Override
    public void processMessage(NodeRef nodeRef, EmailMessage emailMessage) {


        try {
            this.addApplicantToList(emailMessage.getFrom(), nodeRef);
            } catch (JSONException e) {
                e.printStackTrace();
             }
        catch (ParseException pex) {
            pex.printStackTrace();
        }

        EmailMessagePart[] attachments = emailMessage.getAttachments();



        String subject = emailMessage.getSubject();

        FileInfo fileInfo = fileFolderService.create(nodeRef, subject, ContentModel.TYPE_FOLDER);
        super.processMessage(fileInfo.getNodeRef(), emailMessage);
    }
}
