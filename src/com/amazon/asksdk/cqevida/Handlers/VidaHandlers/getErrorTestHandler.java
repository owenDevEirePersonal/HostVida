package com.amazon.asksdk.cqevida.Handlers.VidaHandlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;
import com.amazon.asksdk.cqevida.Utils;
import com.amazonaws.util.json.JSONObject;

import java.net.URLEncoder;
import java.util.Optional;

import static com.amazon.asksdk.cqevida.Constants.*;
import static com.amazon.asksdk.cqevida.Constants.siteAddress;
import static com.amazon.asksdk.cqevida.Utils.*;
import static com.amazon.asksdk.cqevida.Utils.tell;

/**
 * Created by owenryan on 14/06/2018.
 */
public class getErrorTestHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches(request_ErrorTest))
        {
            return true;
        }
        return false;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest)handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        Session session = handlerInput.getRequestEnvelope().getSession();
        String speechText = "";

        Optional<Response> errorResponse = handleInputsChecking(new String[]{}, handlerInput);
        if(errorResponse != null)
        {
            return errorResponse;
        }
        else
        {
            try {
                //[Json formatting]
                JSONObject jsonCustom = new JSONObject();
                JSONObject jsonInput = new JSONObject();
                jsonCustom.put(input_requestType, "VidaErrorTest");
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                jsonInput.put(auth_input_AuthType, "CitAuth");
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                //return tell(handlerInput, jsonOutputString);

                //[Servlet Output Parsing]
                if(Utils.retrieveServletErrors(handlerInput, jsonOutputString) == null)
                {
                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tellWithCard(handlerInput, "CIP Error Test","Something has gone wrong, an error was supposed to occur and it has not, this is a matter of some great concern, and not just because of the implied paradox of this code intending to fail and then succeeding");
                }
                else
                {
                    return Utils.retrieveServletErrors(handlerInput, jsonOutputString);
                }
                //[/Servlet Output Parsing]
                //[/Connect to Servlet]
            } catch (Exception e) {
                speechText = "Error on output: " + e.toString();
            }
            printLog("Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getNumberOfQuotesByInsurersAndTimeResponse: " + speechText);
        }
    }
}
