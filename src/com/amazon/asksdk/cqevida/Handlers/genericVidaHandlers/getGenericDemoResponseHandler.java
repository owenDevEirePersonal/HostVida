package com.amazon.asksdk.cqevida.Handlers.genericVidaHandlers;

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
import static com.amazon.asksdk.cqevida.Utils.*;

/**
 * Created by owenryan on 06/04/2018.
 */
public class getGenericDemoResponseHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches(request_GenericDemoRequest))
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

        String startDate = "2017-01-01";
        String endDate = "2018-12-31";
        String dateName = "createdDate";
        String auth = "CitAuth";
        String primaryContentType = "QuoteRequest";
        String conditions = "";
        String resultIntructions = "count";
        String requirementKey1 = "user/getDisplayName";
        String requirementValue1 = "";
        try {
            requirementValue1 = "'" + getSlotID(intent.getSlots().get("username")).replace('_', ' ') + "'"; //Note you must include ' ' in values if you are using a method as a key.
        }
        catch (Exception e)
        {
            if(intent.getSlots().get("username").getValue() == null)
            {
                return handlerInput.getResponseBuilder().withSpeech("I'm sorry I didn't catch that name, what was it again?").addElicitSlotDirective("username", intent).build();
            }
            else
            {
                return tell(handlerInput, "I'm sorry but I could not find " + intent.getSlots().get("username").getValue() + " in the list of users.");
            }
        }

        Optional<Response> errorResponse = null;//handleInputsAndIntentConfirmation(new String[]{startDate, endDate}, requestEnvelope);
        if(errorResponse != null)
        {
            return errorResponse;
        }
        else
        {
            try {
                startDate = formatDate(startDate);
                endDate = formatDate(endDate);

                //[Json formatting]
                JSONObject jsonInput = new JSONObject();
                jsonInput.put(auth_input_AuthType, auth);
                jsonInput.put(input_generic_dateName, dateName);
                jsonInput.put(input_generic_startDate, startDate);
                jsonInput.put(input_generic_endDate, endDate);
                jsonInput.put(input_generic_primaryContentType, primaryContentType);
                jsonInput.put(input_generic_Conditions, conditions);
                jsonInput.put(input_generic_result, resultIntructions);

                JSONObject jsonRequirements = new JSONObject();
                jsonRequirements.put(requirementKey1, requirementValue1);
                jsonInput.put(input_generic_Requirements, jsonRequirements);

                JSONObject jsonCustom = new JSONObject();
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                //Note if this method returns a fileNotFound Exception it is possible that conditions or requirements make reference to a class that does not exist in the dari database.
                //[Servlet Output Parsing]
                if(Utils.retrieveServletErrors(handlerInput, jsonOutputString) == null)
                {
                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tellWithCard(handlerInput, "CIP Query Demo", "There have been " + jsonOutput.getString("Output0") + " quotes from " + requirementValue1 + " since the start of last year.");
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
            printLog("Error: Something went horribly wrong in getGenericGetFirst: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getGenericGetFirst: " + speechText);
        }
    }
}
