package com.docusign.controller.examples;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.EnvelopeDocument;
import com.docusign.esign.model.EnvelopeDocumentsResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/eg006")
public class EG006ControllerEnvelopeDocs extends EGController {

    @Autowired
    HttpSession session;

    @Override
    protected void addSpecialAttributes(ModelMap model) {
        model.addAttribute("envelopeOk", null != session.getAttribute("envelopeId"));
    }

    @Override
    protected String getEgName() {
        return "eg006";
    }

    @Override
    protected String getTitle() {
        return "List envelope documents";
    }

    @Override
    protected String getResponseTitle() {
        return "List envelope documents result";
    }

    @Override
    // ***DS.snippet.0.start
    protected Object doWork(WorkArguments args, ModelMap model,
                            String accessToken, String basePath) throws ApiException {
        // Data for this method
        // accessToken    (argument)
        // basePath       (argument)
        String accountId = args.getAccountId();
        String envelopeId = args.getEnvelopeId();


        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        // Step 1. List the envelope's documents
        EnvelopeDocumentsResult result = envelopesApi.listDocuments(accountId, envelopeId);

        // Step 2. Process results
        // Save the envelopeId and its list of documents in the session so
        // they can be used in example 7 (download a document)
        JSONArray envelopeDocItems = new JSONArray();
        envelopeDocItems.put(createStandardDoc("Combined", "content", "combined"));
        envelopeDocItems.put(createStandardDoc("Zip archive", "zip", "archive"));

        for (EnvelopeDocument doc : result.getEnvelopeDocuments()) {
            JSONObject o = new JSONObject()
            .put("documentId", doc.getDocumentId())
            .put("name", doc.getDocumentId() == "certificate" ? "Certificate of completion" : doc.getName())
            .put("type", doc.getType());

            envelopeDocItems.put(o);
        }

        JSONObject envelopeDocuments = new JSONObject();
        envelopeDocuments.put("envelopeId", envelopeId);
        envelopeDocuments.put("documents", envelopeDocItems);

        session.setAttribute("envelopeDocuments", envelopeDocuments);
        setMessage("Results from the EnvelopeDocuments::list method:");

        return result;
    }

    private JSONObject createStandardDoc(String name, String type, String documentId) {
        JSONObject o = new JSONObject();
        o.put("name", name);
        o.put("type", type);
        o.put("documentId", documentId);
        return o;
    }
    // ***DS.snippet.0.end
}
