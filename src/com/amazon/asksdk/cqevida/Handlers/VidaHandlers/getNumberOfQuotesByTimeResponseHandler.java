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

import static com.amazon.asksdk.cqevida.Utils.*;
import static com.amazon.asksdk.cqevida.Utils.tell;
import static com.amazon.asksdk.cqevida.Constants.*;

/**
 * Created by owenryan on 06/04/2018.
 */
public class getNumberOfQuotesByTimeResponseHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        IntentRequest request = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
        Intent intent = request.getIntent();
        if(intent.getName().matches(request_NumberOfQuotesByTime))
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

        String startDate = intent.getSlots().get(slot_startDate).getValue();
        String endDate = intent.getSlots().get(slot_endDate).getValue();

        Optional<Response> errorResponse = handleInputsAndIntentConfirmation(new String[]{startDate, endDate}, handlerInput);
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
                JSONObject jsonCustom = new JSONObject();
                JSONObject jsonInput = new JSONObject();
                jsonCustom.put(input_requestType, request_NumberOfQuotesByTime);
                jsonCustom.put(input_startDate, startDate);
                jsonCustom.put(input_endDate, endDate);
                jsonCustom.put(auth_input_signInEmail, userEmail);
                jsonCustom.put(auth_input_signInPassword, userPassword);
                jsonInput.put(input_Custom, jsonCustom);
                jsonInput.put(auth_input_AuthType, "CitAuth");
                //[/Json formatting]

                //[Connect to Servlet]
                printLog("Attempting to connect to servlet at: " + siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));
                String jsonOutputString = requestHTTP(siteAddress + "/vida?params=" + URLEncoder.encode(jsonInput.toString(), "UTF-8"));//[Servlet Output Parsing]
                if(Utils.retrieveServletErrors(handlerInput, jsonOutputString) == null)
                {
                    JSONObject jsonOutput = new JSONObject(jsonOutputString);
                    return tellWithCard(handlerInput, "CIP Quotes","There have been " + jsonOutput.getString("outputQuoteCount") + " quotes given between " + formatDateToAmerican(startDate) + " and " + formatDateToAmerican(endDate));
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
            printLog("Error: Something went horribly wrong in getNumberOfQuotesByTimeResponseHandler: " + speechText);
            return tell(handlerInput, "Error: Something went horribly wrong in getNumberOfQuotesByTimeResponseHandler: " + speechText);
        }
    }
}
